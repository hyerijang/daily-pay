package com.hyerijang.dailypay.statistics.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyerijang.dailypay.auth.CurrentUser;
import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.statistics.service.StatisticsDummyDataGenerator;
import com.hyerijang.dailypay.statistics.service.StatisticsService;
import com.hyerijang.dailypay.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "statistics", description = "통계  API")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final Environment environment;
    private final StatisticsDummyDataGenerator dummyDataGenerator;
    private final StatisticsService statisticsService;

    // === 통계  API === //

    @ExeTimer
    @Operation(summary = "통계", description = "총액 및 카테고리 별 소비율(퍼센티지) 을 반환")
    @GetMapping
    public ResponseEntity<Result> getExpenseComparison(@Param("condition") String condition,
        @CurrentUser User user) {

        Result result;
        switch (condition) {
            case "last-month":
                // (1)  지난 달 대비 총액 및 카테고리 별 소비율
                result = Result.builder()
                    .expenseComparisonLastMonth(
                        statisticsService.getExpenseComparisonLastMonth(user.getId()))
                    .build();
                return ResponseEntity.ok().body(result);
            case "last-week":
                // (2) 지난주 같은 요일 대비 소비율
                result = Result.builder()
                    .lastWeekSameWeekDayComparison(
                        statisticsService.getLastWeekSameWeekDayComparison(user.getId())).build();
                return ResponseEntity.ok().body(result);
            case "other-user":
                // (3) 다른 유저 대비 소비율
                Double userExpenseRatio = statisticsService.getExpenseComparisonWithOtherUser(
                    user.getId());
                result = Result.builder()
                    .expenseComparisonWithOtherUser(userExpenseRatio)
                    .build();
                return ResponseEntity.ok().body(result);
        }
        throw new ApiException(ExceptionEnum.WRONG_EXPENSE_COMPARISON_CONDITION);
    }



    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Result<T> {

        private T expenseComparisonLastMonth; // (1) 지난 달 대비 총액 및 카테고리 별 소비율
        private Double lastWeekSameWeekDayComparison; // (2) 지난주 같은 요일 대비 소비율
        private Double expenseComparisonWithOtherUser; // (3) 다른 유저 대비 소비율
    }

    // == 더미데이터 생성 == //

    @ExeTimer
    @Operation(summary = "더미데이터 생성", description = "개발 환경에서만 실행가능한 API, (application-dev.yml)")
    @PostMapping("/dummy-data")
    void generateDummy(@CurrentUser User user) {

        //개발 환경인지 체크
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isNotDevActive = Arrays.stream(activeProfiles)
            .noneMatch(profile -> profile.equals("dev"));
        if (isNotDevActive) {
            throw new ApiException(ExceptionEnum.NOT_DEV_ENVIRONMENT);
        }

        dummyDataGenerator.generateDummy(user.getId());
    }

}
