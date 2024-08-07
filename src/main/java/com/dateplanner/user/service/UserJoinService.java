package com.dateplanner.user.service;

import com.dateplanner.user.dto.UserLoginRequestDto;
import com.dateplanner.user.dto.UserSocialLoginRequestDto;
import com.dateplanner.advice.exception.*;
import com.dateplanner.security.dto.TokenDto;
import com.dateplanner.security.dto.TokenRequestDto;
import com.dateplanner.security.entity.RefreshToken;
import com.dateplanner.security.jwt.JwtProvider;
import com.dateplanner.security.oauth.dto.OAuthAccessTokenDto;
import com.dateplanner.security.oauth.dto.ProfileDto;
import com.dateplanner.security.oauth.service.OAuthProviderService;
import com.dateplanner.security.repository.RefreshTokenRepository;
import com.dateplanner.user.dto.UserJoinRequestDto;
import com.dateplanner.user.entity.User;
import com.dateplanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Slf4j(topic = "SERVICE")
@RequiredArgsConstructor
@Service
@Transactional
public class UserJoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthProviderService oAuthProviderService;


    public String join(@Valid UserJoinRequestDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailDuplicateException();
        }

        if (userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new UserNicknameDuplicateException();
        }

        return userRepository.save(dto.toEntity(passwordEncoder)).getEmail();
    }

    public Boolean emailDuplicateCheck(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean nicknameDuplicateCheck(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public TokenDto login(UserLoginRequestDto dto) {

        // 회원 정보 조회
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(UserNotFoundApiException::new);

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new PasswordMismatchException();

        // 액세스 토큰, 리프레시 토큰 발급
        log.info("create token start");
        TokenDto tokenDto = jwtProvider.createToken(user.getEmail(), user.getRoleSet());
        log.info("create token complete");

        // 리프레시 토큰 저장
        log.info("refresh token persist start");
        log.info("key : {}, token : {}", user.getEmail(), tokenDto.getRefreshToken());
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getEmail())
                .token(tokenDto.getRefreshToken())
                .build();

        if (refreshTokenRepository.findByKey(user.getEmail()).isPresent()) refreshTokenRepository.deleteByKey(user.getEmail());
        refreshTokenRepository.save(refreshToken);
        log.info("refresh token persist complete");

        return tokenDto;
    }


    public String joinBySocial(UserJoinRequestDto dto) {

        /**
         * 이메일, 닉네임 중복 시 가입을 막아놨으나 카카오로부터 받은 액세스 토큰을 재활용할 수 없으므로 (다시 발급받아야 하므로)
         * 프론트엔드에서 소셜 회원가입 화면에서 닉네임 중복 체크를 꼭 하게끔 하고 넘길 수 있도록 해야 함
         * 중복 이메일, 이미 소셜 회원가입 되어 있는 이메일은 재가입 불가 처리
         */

        log.info("[UserJoinService joinBySocial] email duplicate checking");

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            if (dto.getProvider().equals("kakao")) oAuthProviderService.kakaoUnlink(dto.getAccessToken());
            throw new EmailDuplicateException();
        }

        log.info("[UserJoinService joinBySocial] email and provider duplicate checking");

        if (userRepository.findByEmailAndProvider(dto.getEmail(), dto.getProvider()).isPresent()) {
            throw new UserExistException();
        }

        log.info("[UserJoinService joinBySocial] nickname duplicate checking");

        if (userRepository.findByNickname(dto.getNickname()).isPresent()) {
            if (dto.getProvider().equals("kakao")) oAuthProviderService.kakaoUnlink(dto.getAccessToken());
            throw new UserNicknameDuplicateException();
        }

        return userRepository.save(dto.toEntity()).getEmail();
    }


    public TokenDto loginBySocial(UserSocialLoginRequestDto dto, String provider) {

        /**
         * 프론트엔드에서 소셜 로그인 화면에서 한번 더 카카오 측에 인가 코드를 요청해서 받아야 함
         */

        // 인가 코드와 인증 제공자를 토대로 인증 제공자에게 액세스 토큰 요청, 받음
        OAuthAccessTokenDto accessToken = oAuthProviderService.getAcessToken(dto.getCode(), provider);

        // 액세스 토큰으로부터 인증 프로필 전달 받음
        ProfileDto profile = oAuthProviderService.getProfile(accessToken.getAccess_token(), provider);

        if (profile == null) throw new UserNotFoundApiException();

        User user = userRepository.findByEmailAndProvider(profile.getEmail(), provider).orElseThrow(UserNotFoundApiException::new);

        TokenDto tokenDto = jwtProvider.createToken(user.getEmail(), user.getRoleSet());

        // 리프레시 토큰 저장
        log.info("refresh token persist start");
        log.info("key : {}, token : {}", user.getEmail(), tokenDto.getRefreshToken());
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getEmail())
                .token(tokenDto.getRefreshToken())
                .build();

        if (refreshTokenRepository.findByKey(user.getEmail()).isPresent()) refreshTokenRepository.deleteByKey(user.getEmail());
        refreshTokenRepository.save(refreshToken);
        log.info("refresh token persist complete");

        return tokenDto;
    }


    public TokenDto refresh(TokenRequestDto dto) {

        // 만료된 리프레시 토큰 확인
        if (!jwtProvider.validationToken(dto.getRefreshToken())) {
            throw new CustomRefreshTokenException();
        }

        // 액세스 토큰에서 username (uid) 가져오기
        String accessToken = dto.getAccessToken();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // username (email) 로 유저 검색, 리프레시 토큰 여부 확인
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UserNotFoundApiException::new);
        RefreshToken refreshToken = refreshTokenRepository.findByKey(user.getEmail()).orElseThrow(CustomRefreshTokenException::new);

        // 리프레시 토큰 불일치 여부 확인
        if (!refreshToken.getToken().equals(dto.getRefreshToken()))
            throw new CustomRefreshTokenException();

        // 액세스 토큰, 리프레시 토큰 재발급 및 리프레시 토큰 저장
        TokenDto newCreatedToken = jwtProvider.createToken(user.getEmail(), user.getRoleSet());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());

        refreshTokenRepository.save(updateRefreshToken);

        return newCreatedToken;

    }

}
