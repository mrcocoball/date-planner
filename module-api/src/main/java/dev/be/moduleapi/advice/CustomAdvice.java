package dev.be.moduleapi.advice;

import dev.be.moduleapi.advice.exception.*;
import dev.be.moduleapi.api.model.CommonResult;
import dev.be.moduleapi.api.model.MapResult;
import dev.be.moduleapi.api.service.ResponseService;
import dev.be.modulecore.enums.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomAdvice {

    private final ResponseService responseService;


    /**
     * 회원 가입, 인증, 인가 관련
     */

    @ExceptionHandler(CustomAuthenticationEntrypointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResult customAuthenticationEntrypointException(HttpServletRequest request, CustomAuthenticationEntrypointException e) {
        return responseService.getFailResult
                (ErrorCode.AUTHENTICATION_FAILED.getCode(), ErrorCode.AUTHENTICATION_FAILED.getDescription());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResult accessDeniedException(HttpServletRequest request, AccessDeniedException e) {

        log.error("access is denied");

        return responseService.getFailResult
                (ErrorCode.ACCESS_DENIED.getCode(), ErrorCode.ACCESS_DENIED.getDescription());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResult expiredJwtException(HttpServletRequest request, ExpiredJwtException e) {

        log.error("JWT token is expired");

        return responseService.getFailResult
                (ErrorCode.TOKEN_EXPIRED.getCode(), ErrorCode.TOKEN_EXPIRED.getDescription());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult usernameNotFoundException(HttpServletRequest request, UsernameNotFoundException e) {

        log.error("username is not found");

        return responseService.getFailResult
                (ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(UserNotFoundApiException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult userNotFoundApiException(HttpServletRequest request, UserNotFoundApiException e) {

        log.error("user is not found");

        return responseService.getFailResult
                (ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(UserNicknameDuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult userNicknameDuplicateException(HttpServletRequest request, UserNicknameDuplicateException e) {

        log.warn("input nickname is duplicated");

        return responseService.getFailResult
                (ErrorCode.USER_NICKNAME_DUPLICATED.getCode(), ErrorCode.USER_NICKNAME_DUPLICATED.getDescription());
    }

    @ExceptionHandler(EmailDuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult emailDuplicateException(HttpServletRequest request, EmailDuplicateException e) {

        log.warn("input email is duplicated");

        return responseService.getFailResult
                (ErrorCode.EMAIL_DUPLICATED.getCode(), ErrorCode.EMAIL_DUPLICATED.getDescription());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult passwordMismatchException(HttpServletRequest request, PasswordMismatchException e) {

        log.warn("input password is mismatched");

        return responseService.getFailResult
                (ErrorCode.PASSWORD_MISMATCH.getCode(), ErrorCode.PASSWORD_MISMATCH.getDescription());
    }

    @ExceptionHandler(CustomRefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResult customRefreshTokenException(HttpServletRequest request, CustomRefreshTokenException e) {

        log.error("refresh token is invalid");

        return responseService.getFailResult
                (ErrorCode.REFRESH_TOKEN_INVALID.getCode(), ErrorCode.REFRESH_TOKEN_INVALID.getDescription());
    }

    @ExceptionHandler(OAuthRequestFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult oAuthRequestFailedException(HttpServletRequest request, OAuthRequestFailedException e) {

        log.error("OAuth request is failed");

        return responseService.getFailResult
                (ErrorCode.OAUTH_FAILED.getCode(), ErrorCode.OAUTH_FAILED.getDescription());
    }

    @ExceptionHandler(OAuthAgreementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult oAuthAgreementException(HttpServletRequest request, OAuthAgreementException e) {

        log.error("OAuth agreement is failed");

        return responseService.getFailResult
                (ErrorCode.OAUTH_NOT_AGREED.getCode(), ErrorCode.OAUTH_NOT_AGREED.getDescription());
    }

    @ExceptionHandler(UserExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult userExistException(HttpServletRequest request, UserExistException e) {

        log.error("user is already exists");

        return responseService.getFailResult
                (ErrorCode.USER_ALREADY_EXISTS.getCode(), ErrorCode.USER_ALREADY_EXISTS.getDescription());
    }


    /**
     * 비즈니스 로직 관련
     */

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected MapResult<String> bindException(HttpServletRequest request, BindException e) {

        Map<String, String> errorMap = new HashMap<>();

        if (e.hasErrors()) {

            BindingResult bindingResult = e.getBindingResult();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                log.warn("invalid data : {},{}", fieldError.getField(), fieldError.getDefaultMessage());
            });
        }

        return responseService.getFailResult
                (ErrorCode.VALIDATION_ERROR.getCode(), ErrorCode.VALIDATION_ERROR.getDescription(), errorMap);
    }

    @ExceptionHandler(PlaceNotFoundApiException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult placeNotFoundApiException(HttpServletRequest request, PlaceNotFoundApiException e) {

        log.error("place is not found");

        return responseService.getFailResult
                (ErrorCode.PLACE_NOT_FOUND.getCode(), ErrorCode.PLACE_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(BookmarkNotFoundApiException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult bookmarkNotFoundApiException(HttpServletRequest request, BookmarkNotFoundApiException e) {

        log.error("bookmark is not found");

        return responseService.getFailResult
                (ErrorCode.BOOKMARK_NOT_FOUND.getCode(), ErrorCode.BOOKMARK_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(BookmarkDuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult bookmarkDuplicateException(HttpServletRequest request, BookmarkDuplicateException e) {

        log.error("bookmark is already registered");

        return responseService.getFailResult
                (ErrorCode.BOOKMARK_DUPLICATED.getCode(), ErrorCode.BOOKMARK_DUPLICATED.getDescription());
    }

    @ExceptionHandler(ReviewNotFoundApiException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult reviewNotFoundApiException(HttpServletRequest request, ReviewNotFoundApiException e) {

        log.error("review is not found");

        return responseService.getFailResult
                (ErrorCode.REVIEW_NOT_FOUND.getCode(), ErrorCode.REVIEW_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(PlanNotFoundApiException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult planFoundApiException(HttpServletRequest request, PlanNotFoundApiException e) {

        log.error("plan is not found");

        return responseService.getFailResult
                (ErrorCode.PLAN_NOT_FOUND.getCode(), ErrorCode.PLAN_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(DetailPlanNotFoundApiException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult detailPlanFoundApiException(HttpServletRequest request, DetailPlanNotFoundApiException e) {

        log.error("detailPlan is not found");

        return responseService.getFailResult
                (ErrorCode.DETAIL_PLAN_NOT_FOUND.getCode(), ErrorCode.DETAIL_PLAN_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(CustomRetryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult customRetryException(HttpServletRequest request, CustomRetryException e) {

        log.warn("retry is failed");

        return responseService.getFailResult
                (ErrorCode.REQUEST_FAILED.getCode(), ErrorCode.REQUEST_FAILED.getDescription());
    }

    @ExceptionHandler(SearchResultNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult searchResultNotFoundException(HttpServletRequest request, SearchResultNotFoundException e) {
        return responseService.getFailResult
                (ErrorCode.SEARCH_RESULT_NOT_FOUND.getCode(), ErrorCode.SEARCH_RESULT_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(AddressInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult addressInvalidException(HttpServletRequest request, AddressInvalidException e) {
        return responseService.getFailResult
                (ErrorCode.ADDRESS_INVALID.getCode(), ErrorCode.ADDRESS_INVALID.getDescription());
    }

    @ExceptionHandler(CategoryInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult categoryInvalidException(HttpServletRequest request, CategoryInvalidException e) {
        return responseService.getFailResult
                (ErrorCode.CATEGORY_INVALID.getCode(), ErrorCode.CATEGORY_INVALID.getDescription());
    }

    @ExceptionHandler(AnnouncementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult announcementNotFoundException(HttpServletRequest request, AnnouncementNotFoundException e) {

        log.error("announcement is not found");

        return responseService.getFailResult
                (ErrorCode.ANNOUNCEMENT_NOT_FOUND.getCode(), ErrorCode.ANNOUNCEMENT_NOT_FOUND.getDescription());
    }

    @ExceptionHandler(SortTypeInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult SortTypeInvalidException(HttpServletRequest request, SortTypeInvalidException e) {
        return responseService.getFailResult
                (ErrorCode.SORTTYPE_INVALID.getCode(), ErrorCode.SORTTYPE_INVALID.getDescription());
    }


    /**
     * Data Access 관련
     */

    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult badSqlGrammarException(HttpServletRequest request, BadSqlGrammarException e) {

        log.error("BadSqlGrammarException is occurred {}", e);

        return responseService.getFailResult
                (ErrorCode.SQL_GRAMMAR_ERROR.getCode(), ErrorCode.SQL_GRAMMAR_ERROR.getDescription());
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult dataAccessResourceFailureException(HttpServletRequest request, DataAccessResourceFailureException e) {

        log.error("DataAccessResourceFailureException is occurred {}", e);

        return responseService.getFailResult
                (ErrorCode.DB_CONNECTION_OUT.getCode(), ErrorCode.DB_CONNECTION_OUT.getDescription());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult dataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {

        log.error("DataIntegrityViolationException is occurred {}", e);

        return responseService.getFailResult
                (ErrorCode.DATA_INTEGRITY_VIOLATION.getCode(), ErrorCode.DATA_INTEGRITY_VIOLATION.getDescription());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult duplicateKeyException(HttpServletRequest request, DuplicateKeyException e) {

        log.error("DuplicateKeyException is occurred {}", e);

        return responseService.getFailResult
                (ErrorCode.DB_KEY_DUPLICATE.getCode(), ErrorCode.DB_KEY_DUPLICATE.getDescription());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult dataAccessException(HttpServletRequest request, DataAccessException e) {

        log.error("DataAccessException is occurred {}", e);

        return responseService.getFailResult
                (ErrorCode.DATA_ACCESS_ERROR.getCode(), ErrorCode.DATA_ACCESS_ERROR.getDescription());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult entityNotFoundException(HttpServletRequest request, EntityNotFoundException e) {
        return responseService.getFailResult
                (ErrorCode.ENTITY_NOT_FOUND.getCode(), ErrorCode.ENTITY_NOT_FOUND.getDescription());
    }


    /**
     * 기타 예외 관련 (일단 개발 중이므로 잠시 주석 처리)
     */

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult Exception(HttpServletRequest request, Exception e) {

        log.error("Exception is occurred {}", e);

        return responseService.getFailResult
                (ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
    }

}
