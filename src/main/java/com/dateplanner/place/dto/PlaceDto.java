package com.dateplanner.place.dto;

import com.dateplanner.place.entity.Place;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDto {

    private Long id;

    @JsonProperty("category_group_id")
    private String categoryGroupId;

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("place_url")
    private String placeUrl;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("road_address_name")
    private String roadAddressName;

    @JsonProperty("region_1depth_name")
    private String region1DepthName;

    @JsonProperty("region_2depth_name")
    private String region2DepthName;

    @JsonProperty("region_3depth_name")
    private String region3DepthName;

    @JsonProperty("x")
    private double longitude;

    @JsonProperty("y")
    private double latitude;

    @JsonProperty("review_score")
    private Long reviewScore;

    @JsonProperty("review_count")
    private Long reviewCount;

    public static PlaceDto from(Place entity) {
        return PlaceDto.builder()
                .id(entity.getId())
                .categoryGroupId(entity.getCategory().getId())
                .placeName(entity.getPlaceName())
                .placeId(entity.getPlaceId())
                .placeUrl(entity.getPlaceUrl())
                .addressName(entity.getAddressName())
                .roadAddressName(entity.getRoadAddressName())
                .region1DepthName(entity.getRegion1DepthName())
                .region2DepthName(entity.getRegion2DepthName())
                .region3DepthName(entity.getRegion3DepthName())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .reviewScore(entity.getReviewScore())
                .reviewCount(entity.getReviewCount())
                .build();
    }

}