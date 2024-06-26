package dev.be.moduleapi.support.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.be.moduleapi.support.dto.AnnouncementDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static dev.be.modulecore.domain.support.QAnnouncement.announcement;
import static dev.be.modulecore.domain.support.QAnnouncementCategory.announcementCategory;
import static org.springframework.util.StringUtils.hasText;

@Repository
@Slf4j
public class AnnouncementCustomRepository {

    private JPAQueryFactory jpaQueryFactory;

    public AnnouncementCustomRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<AnnouncementDto> announcementList(@Param("condition") String condition,
                                                  @Param("categoryId") Long categoryId,
                                                  @Param("keyword") String keyword) {

        return jpaQueryFactory.select(Projections.fields(AnnouncementDto.class,
                        announcement.id.as("id"),
                        announcement.title.as("title"),
                        announcement.description.as("description"),
                        announcementCategory.id.as("categoryId"),
                        announcementCategory.categoryName.as("categoryName"),
                        announcement.createdAt.as("createdAt")
                ))
                .from(announcement)
                .leftJoin(announcement.announcementCategory, announcementCategory)
                .where(
                        categoryCont(categoryId),
                        searchCondition(condition, keyword)
                )
                .orderBy(announcement.createdAt.desc())
                .fetch();
    }

    private BooleanExpression titleCont(String title) {
        return hasText(title) ? announcement.title.contains(title) : null;
    }

    private BooleanExpression descriptionCont(String description) {
        return hasText(description) ? announcement.description.contains(description) : null;
    }

    private BooleanExpression categoryCont(Long categoryId) {
        return categoryId != null ? announcementCategory.id.eq(categoryId) : null;
    }

    private BooleanExpression searchCondition(String condition, String keyword) {

        if (!hasText(condition)) return null;

        if (condition.equals("title")) return titleCont(keyword);

        if (condition.equals("description")) return descriptionCont(keyword);

        return null;
    }

}
