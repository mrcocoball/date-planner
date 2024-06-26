package dev.be.moduleapi.place.service;

import dev.be.moduleapi.advice.exception.SearchResultNotFoundException;
import dev.be.moduleapi.place.dto.PlaceRecommendationDto;
import dev.be.moduleapi.place.repository.PlaceRecommendationRepository;
import dev.be.modulecore.service.PaginationService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j(topic = "SERVICE")
@Timed("business.service.place_recommendation")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlaceRecommendationService {

    private final PlaceRecommendationRepository placeRecommendationRepository;
    private final PaginationService paginationService;


    public Page<PlaceRecommendationDto> getPlaceRecommendation(String region1, String region2, Pageable pageable) {

        List<PlaceRecommendationDto> result = placeRecommendationRepository.placeRecommendation(region1, region2);

        if (Objects.isNull(result) || result.isEmpty()) {
            log.warn("[PlaceRecommendationService getPlaceRecommendation] recommendation result is null");
            throw new SearchResultNotFoundException();
        }

        for (PlaceRecommendationDto dto : result) {
            double avgScore = PlaceRecommendationDto.calculateAvgScore(dto.getReviewScore(), dto.getReviewCount());
            dto.setAvgReviewScore(avgScore);
        }

        return paginationService.listToPage(result, pageable);

    }

}
