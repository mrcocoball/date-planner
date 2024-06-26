package dev.be.moduleapi.review.service;

import dev.be.fixture.Fixture;
import dev.be.moduleapi.advice.exception.ReviewNotFoundApiException;
import dev.be.moduleapi.review.dto.ReviewDto;
import dev.be.modulecore.domain.review.Review;
import dev.be.modulecore.repositories.review.ReviewRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[단일] 리뷰 화면 처리 서비스 - 리뷰 조회 테스트")
@ExtendWith(MockitoExtension.class)
class ReviewApiServiceTest {

    @InjectMocks
    private ReviewApiService sut;

    @Mock
    private ReviewRepository reviewRepository;

    @DisplayName("READ - 장소 내 리뷰 리스트 조회")
    @Test
    public void 장소_내_리뷰_리스트_조회() {

        // Given
        String placeId = "1";
        Pageable pageable = Pageable.ofSize(10);
        given(reviewRepository.findByKpid(placeId, pageable)).willReturn(Page.empty());

        // When
        Page<ReviewDto> result = sut.getReviewListByPlaceId(placeId, pageable);

        // Then
        assertThat(result).isEmpty();
        then(reviewRepository).should().findByKpid(placeId, pageable);

    }

    @DisplayName("READ - 사용자 작성 리뷰 리스트 조회")
    @Test
    public void 사용자_작성_리뷰_리스트_조회() {

        // Given
        String nickname = "test";
        Pageable pageable = Pageable.ofSize(10);
        given(reviewRepository.findByUser_Nickname(nickname, pageable)).willReturn(Page.empty());

        // When
        Page<ReviewDto> result = sut.getReviewListByNickname(nickname, pageable);

        // Then
        assertThat(result).isEmpty();
        then(reviewRepository).should().findByUser_Nickname(nickname, pageable);

    }

    @DisplayName("READ - 리뷰 단건 조회")
    @Test
    public void 리뷰_단건_조회_성공() {

        // Given
        Long id = 1L;
        Review review = Fixture.review();
        given(reviewRepository.findById(id)).willReturn(Optional.of(review));

        // When
        ReviewDto dto = sut.getReview(id);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", review.getId())
                .hasFieldOrPropertyWithValue("placeId", review.getKpid());
        then(reviewRepository).should().findById(id);

    }

    @DisplayName("READ - 리뷰 단건 조회 - 실패(존재하지 않는 실패)")
    @Test
    public void 리뷰_단건_조회_실패() {

        // Given
        Long id = 0L;
        given(reviewRepository.findById(id)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThrows(ReviewNotFoundApiException.class, () -> {
            sut.getReview(id);
        });
        then(reviewRepository).should().findById(id);

    }


}