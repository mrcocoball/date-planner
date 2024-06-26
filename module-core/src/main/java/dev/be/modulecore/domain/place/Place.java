package dev.be.modulecore.domain.place;

import dev.be.modulecore.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j(topic = "ENTITY")
@Table(indexes = {
        @Index(columnList = "placeId"),
        @Index(columnList = "createdAt")
})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id")
    private Category category;

    // TODO : JOIN이 많아지는 경우에서 카테고리를 조회해야 할 경우를 위해 반정규화를 하였음
    private String categoryCode;

    private String placeName;

    private String placeId;

    private String placeUrl;

    private String addressName;

    @Column(name = "road_address_name")
    private String roadAddressName;

    @Column(name = "region_1depth_name")
    private String region1DepthName;

    @Column(name = "region_2depth_name")
    private String region2DepthName;

    @Column(name = "region_3depth_name")
    private String region3DepthName;

    private double longitude;

    private double latitude;

    private Long reviewScore;

    private Long reviewCount;

    @Column(length = 1000)
    private String imageUrl;

    private String description;

    private Place(Category category, String categoryCode, String placeName, String placeId, String placeUrl, String addressName,
                  String roadAddressName, String region1DepthName, String region2DepthName, String region3DepthName,
                  double longitude, double latitude, Long reviewScore, Long reviewCount, String imageUrl, String description) {

        this.category = category;
        this.categoryCode = categoryCode;
        this.placeName = placeName;
        this.placeId = placeId;
        this.placeUrl = placeUrl;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.region1DepthName = region1DepthName;
        this.region2DepthName = region2DepthName;
        this.region3DepthName = region3DepthName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.reviewScore = reviewScore;
        this.reviewCount = reviewCount;
        this.imageUrl = imageUrl;
        this.description = description;

    }

    private Place(Category category, String categoryCode, String placeName, String placeId, String placeUrl, String addressName,
                  String roadAddressName, String region1DepthName, String region2DepthName, String region3DepthName,
                  double longitude, double latitude, Long reviewScore, Long reviewCount) {

        this.category = category;
        this.categoryCode = categoryCode;
        this.placeName = placeName;
        this.placeId = placeId;
        this.placeUrl = placeUrl;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.region1DepthName = region1DepthName;
        this.region2DepthName = region2DepthName;
        this.region3DepthName = region3DepthName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.reviewScore = reviewScore;
        this.reviewCount = reviewCount;

    }

    public static Place of(Category category, String categoryCode, String placeName, String placeId, String placeUrl, String addressName,
                           String roadAddressName, String region1DepthName, String region2DepthName, String region3DepthName,
                           double longitude, double latitude, Long reviewScore, Long reviewCount, String imageUrl, String description) {

        return new Place(category, categoryCode, placeName, placeId, placeUrl, addressName, roadAddressName,
                region1DepthName, region2DepthName, region3DepthName, longitude, latitude, reviewScore, reviewCount, imageUrl, description);
    }

    public static Place of(Category category, String categoryCode, String placeName, String placeId, String placeUrl, String addressName,
                           String roadAddressName, String region1DepthName, String region2DepthName, String region3DepthName,
                           double longitude, double latitude, Long reviewScore, Long reviewCount) {

        return new Place(category, categoryCode, placeName, placeId, placeUrl, addressName, roadAddressName,
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

    public void changePlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void changePlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void changePlaceUrl(String placeUrl) {
        this.placeUrl = placeUrl;
    }

    public void changeAddressName(String addressName) {
        this.addressName = addressName;
    }

    public void changeRoadAdressName(String roadAddressName) {
        this.roadAddressName = roadAddressName;
    }

    public void changeRegion1(String region1DepthName) {
        this.region1DepthName = region1DepthName;
    }

    public void changeRegion2(String region2DepthName) {
        this.region2DepthName = region2DepthName;
    }

    public void changeRegion3(String region3DepthName) {
        this.region3DepthName = region3DepthName;
    }

    public void changeLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void changeLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

}
