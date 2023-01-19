package com.dateplanner.place.entity;

import com.dateplanner.constrant.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String placeName;

    private String placeId;

    private String placeUrl;

    private String addressName;

    @Column(name = "region_1depth_name")
    private String region1DepthName;

    @Column(name = "region_2depth_name")
    private String region2DepthName;

    @Column(name = "region_3depth_name")
    private String region3DepthName;

    private String longitude;

    private String latitude;

    private Long reviewScore;

    private Long reviewCount;

    private Place(Category category, String placeName, String placeId, String placeUrl, String addressName,
                  String region1DepthName, String region2DepthName, String region3DepthName,
                  String longitude, String latitude, Long reviewScore, Long reviewCount) {

        this.category = category;
        this.placeName = placeName;
        this.placeId = placeId;
        this.placeUrl = placeUrl;
        this.addressName = addressName;
        this.region1DepthName = region1DepthName;
        this.region2DepthName = region2DepthName;
        this.region3DepthName = region3DepthName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.reviewScore = reviewScore;
        this.reviewCount = reviewCount;

    }

    public static Place of(Category category, String placeName, String placeId, String placeUrl, String addressName,
                           String region1DepthName, String region2DepthName, String region3DepthName,
                           String longitude, String latitude, Long reviewScore, Long reviewCount) {

        return new Place(category, placeName, placeId, placeUrl, addressName,
                region1DepthName, region2DepthName, region3DepthName, longitude, latitude, reviewScore, reviewCount);
    }

    public void addScoreAndCount(Long score) {
        this.reviewCount += 1;
        this.reviewScore += score;
    }

    public void subtractScoreAndCount(Long score) {
        this.reviewCount -= 1;
        this.reviewScore -= score;
    }

}