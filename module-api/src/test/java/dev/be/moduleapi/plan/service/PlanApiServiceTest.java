package dev.be.moduleapi.plan.service;

import dev.be.fixture.Fixture;
import dev.be.moduleapi.plan.dto.PlanDto;
import dev.be.modulecore.domain.plan.Plan;
import dev.be.modulecore.repositories.plan.PlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[단일] 플랜 화면 처리 서비스 - 플랜 조회 테스트")
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PlanApiServiceTest {

    @Autowired
    private PlanApiService sut;

    @MockBean
    private PlanRepository planRepository;


    @DisplayName("READ - 사용자 작성 플랜 리스트 조회")
    @Test
    public void 사용자_작성_플랜_리스트_조회_성공() {

        // Given
        String nickname = "test";
        Pageable pageable = Pageable.ofSize(10);
        given(planRepository.findByUser_Nickname(nickname, pageable)).willReturn(Page.empty());

        // When
        Page<PlanDto> result = sut.getPlanListByNickname(nickname, pageable);

        // Then
        assertThat(result).isEmpty();
        then(planRepository).should().findByUser_Nickname(nickname, pageable);

    }

    @DisplayName("READ - 플랜 단건 조회")
    @Test
    public void 플랜_단건_조회_성공() {

        // Given
        Long id = 1L;
        Plan plan = Fixture.plan();
        String nickname = "test";
        given(planRepository.findById(id)).willReturn(Optional.of(plan));

        // When
        PlanDto dto = sut.getPlan(id, nickname);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", plan.getId())
                .hasFieldOrPropertyWithValue("nickname", plan.getUser().getNickname());
        then(planRepository).should().findById(id);

    }

}