package dev.be.moduleapi.bookmark.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.be.modulecore.domain.bookmark.Bookmark;
import dev.be.modulecore.domain.place.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j(topic = "DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkDto {

    @Schema(description = "북마크 ID")
    private Long id;

    @Schema(description = "북마크 작성자 닉네임")
    private String nickname;

    // TODO : 프론트엔드에서 카테고리 코드명 -> 카테고리명 변경
    @Schema(description = "북마크가 작성된 장소의 카테고리 코드명")
    private String categoryCode;

    @Schema(description = "북마크가 작성된 장소의 place_id")
    private String placeId;

    @Schema(description = "북마크가 작성된 장소명")
    private String placeName;

    @Schema(description = "북마크가 작성된 장소의 지번 주소")
    private String addressName;

    @Schema(description = "북마크가 작성된 장소의 경도(x)")
    private double longitude;

    @Schema(description = "북마크가 작성된 장소의 위도(y)")
    private double latitude;

    @Schema(description = "북마크가 저장 시간")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    public static BookmarkDto from(Bookmark entity) {
        return BookmarkDto.builder()
                .id(entity.getId())
                .nickname(entity.getUser().getNickname())
                .categoryCode(entity.getPlace().getCategoryCode())
                .placeId(entity.getPlace().getPlaceId())
                .placeName(entity.getPlace().getPlaceName())
                .addressName(entity.getPlace().getAddressName())
                .longitude(entity.getPlace().getLongitude())
                .latitude(entity.getPlace().getLatitude())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
