package dev.be.moduleadmin.support.dto;

import dev.be.modulecore.domain.support.AnnouncementCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementCategoryDto {

    private Long id;

    private String categoryName;

    public static AnnouncementCategoryDto from(AnnouncementCategory entity) {
        return AnnouncementCategoryDto.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .build();
    }

}
