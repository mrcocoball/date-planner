package com.dateplanner.place.controller;

import com.dateplanner.api.dto.DocumentDto;
import com.dateplanner.api.dto.KakaoApiResponseDto;
import com.dateplanner.api.dto.MetaDto;
import com.dateplanner.place.dto.PlaceDto;
import com.dateplanner.place.service.PlaceApiService;
import com.dateplanner.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j(topic = "CONTROLLER")
@Tag(name = "PlaceApiController")
@RequiredArgsConstructor
@RestController
public class PlaceApiController {

    /**
     * PlaceService : KAKAO API를 통한 API 호출 및 장소 정보 DB 저장 등 핵심 비즈니스 로직 담당
     * PlaceApiService : 화면 처리를 위한 전용 서비스
     */

    private final PlaceApiService placeApiService;
    private final PlaceService placeService;


    /**
     * 카카오 주소 검색 API + 카테고리 장소 검색 결과를 그대로 화면으로 처리
     */
    @Operation(summary = "Place List", description = "[GET] 주소, 카테고리로 장소 리스트 출력 (KAKAO)")
    @GetMapping("/api/v1/placesKakao")
    public Page<DocumentDto> placesKakaoV1(String address, String category, @PageableDefault(size = 10, sort = "reviewScore") Pageable pageable) {

        DocumentDto addressDto = placeService.convertingPlaceLongitudeAndLatitude(address);
        KakaoApiResponseDto dto =  placeService.placeSearchByKakao(addressDto, category);
        placeService.placePersist(dto);
        return placeApiService.getPlacesByKakao(dto, pageable);
    }


    /**
     * 카카오 주소 검색 API으로 반환한 좌표 + 주소 기준 DB 조회 후 거리 계산하여 장소 출력 후 화면으로 처리
     */
    @Operation(summary = "Place List", description = "[GET] 주소, 카테고리로 장소 리스트 출력")
    @GetMapping("/api/v1/places")
    public Page<PlaceDto> placesV1(String address, String category, @PageableDefault(size = 10, sort = "reviewScore") Pageable pageable) {

        DocumentDto addressDto = placeService.convertingPlaceLongitudeAndLatitude(address);
        KakaoApiResponseDto dto =  placeService.placeSearchByKakao(addressDto, category);
        placeService.placePersist(dto);
        return placeApiService.getPlacesByKeyword(addressDto, dto, pageable);
    }

    @Operation(summary = "Place", description = "[GET] 장소 ID로 단일 장소 정보 조회")
    @GetMapping("/api/v1/places/{place_id}")
    public PlaceDto getPlaceV1(@PathVariable("place_id")String placeId) {

        return placeApiService.getPlace(placeId);
    }


    /**
     * API 테스트 비교용 메서드
     */
    @Operation(summary = "KAKAO", description = "[GET] 카카오 API Meta Dto 테스트용")
    @GetMapping("/api/v1/test/meta")
    public MetaDto testMetaV1(String address, String category) {

        return placeService.getMetaDto(address, category);
    }

    @Operation(summary = "KAKAO", description = "[GET] 카카오 API Document Dto 테스트용")
    @GetMapping("/api/v1/test/document")
    public List<DocumentDto> testDocumentV1(String address, String category) {

        return placeService.getDocumentDto(address, category);
    }

}