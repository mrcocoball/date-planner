package dev.be.moduleapi.place.service;

import dev.be.moduleapi.advice.exception.SearchResultNotFoundException;
import dev.be.moduleapi.kakao.dto.DocumentDto;
import dev.be.moduleapi.kakao.dto.KakaoApiResponseDto;
import dev.be.moduleapi.kakao.service.KakaoAddressSearchService;
import dev.be.moduleapi.kakao.service.KakaoCategorySearchService;
import dev.be.modulecore.domain.place.Category;
import dev.be.modulecore.domain.place.Place;
import dev.be.modulecore.repositories.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j(topic = "SERVICE")
@RequiredArgsConstructor
@Transactional
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private static final int MAX_LADIUS = 5; // km


    public DocumentDto getPlaceLongitudeAndLatitude(String address) {

        List<DocumentDto> addressResults = kakaoAddressSearchService.requestAddressSearch(address).getDocumentList();

        if (Objects.isNull(addressResults) || addressResults.isEmpty()) {
            log.warn("[PlaceService getPlaceLongitudeAndLatitude] address search result is null");
            throw new SearchResultNotFoundException();
        }

        return addressResults.get(0);

    }


    public KakaoApiResponseDto placeSearchByKakao(DocumentDto addressDto, List<String> categories) {

        return kakaoCategorySearchService.requestCategorySearch(
                Double.valueOf(addressDto.getLatitude()), Double.valueOf(addressDto.getLongitude()), MAX_LADIUS, categories);
    }


    public List<String> placePersist(KakaoApiResponseDto dto) {

        List<DocumentDto> results = dto.getDocumentList();

        // 결과 HashMap
        Map<String, Long> resultMap = new HashMap<>();

        if (Objects.isNull(results) || results.isEmpty()) {
            log.warn("[PlaceService placePersist] category search result is null");
            resultMap.put("number of saved places : ", null);
            throw new SearchResultNotFoundException();
        }

        // 전달 받은 장소 리스트 DB 내 중복 여부 체크 후 DB에 저장
        int convertResultCount = 0;
        int nestedResultCount = 0;

        List<String> region2List = new ArrayList<>();

        for (DocumentDto result : results) {
            result.splitAddress(result.getAddressName());
            String region2DepthName = result.getRegion2DepthName();
            if (!region2List.contains(region2DepthName)) {
                region2List.add(region2DepthName);
            }
        }

        String region1DepthName = results.get(0).getRegion1DepthName();
        List<String> placeIds = placeRepository.findPlaceIdByRegion1DepthNameAndRegion2DepthName(region1DepthName, region2List);
        for (DocumentDto result : results) {

            result.splitAddress(result.getAddressName());

            if (placeIds.contains(result.getPlaceId())) {
                nestedResultCount += 1;
            }

            if (!placeIds.contains(result.getPlaceId())) {
                placeRepository.save(Place.of(
                        Category.of(result.getCategoryGroupId()),
                        result.getCategoryGroupId(),
                        result.getPlaceName(),
                        result.getPlaceId(),
                        result.getPlaceUrl(),
                        result.getAddressName(),
                        result.getRoadAddressName(),
                        result.getRegion1DepthName(),
                        result.getRegion2DepthName(),
                        result.getRegion3DepthName(),
                        result.getLongitude(),
                        result.getLatitude(),
                        0L,
                        0L));
                convertResultCount += 1;
            }
        }

        resultMap.put("number of saved places : ", Long.valueOf(convertResultCount));
        resultMap.put("number of nested places : ", Long.valueOf(nestedResultCount));
        resultMap.put("total number of searched places : ", Long.valueOf(results.size()));
        log.info("persist complete, {}", resultMap);
        return region2List;
    }
}
