package com.dateplanner;

import com.dateplanner.place.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@RequiredArgsConstructor
public class InitCategory {

    private final InitCategoryService initCategoryService;

    @PostConstruct
    public void init() {
        initCategoryService.init();
    }

    @Component
    static class InitCategoryService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            Category CE7 = Category.of("CE7", "카페");
            Category MT1 = Category.of("MT1", "대형 마트");
            Category CS2 = Category.of("CS2", "편의점");
            Category PK6 = Category.of("PK6", "주차장");
            Category OL7 = Category.of("OL7", "주유소, 충전소");
            Category SW8 = Category.of("SW8", "지하철역");
            Category CT1 = Category.of("CT1", "문화시설");
            Category AT4 = Category.of("AT4", "관광명소");
            Category AD5 = Category.of("AD5", "숙박");
            Category FD6 = Category.of("FD6", "음식점");

            em.persist(CE7);
            em.persist(MT1);
            em.persist(CS2);
            em.persist(PK6);
            em.persist(OL7);
            em.persist(SW8);
            em.persist(CT1);
            em.persist(AT4);
            em.persist(AD5);
            em.persist(FD6);
        }
    }
}