package com.hyerijang.dailypay.statistics.controller;

import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.statistics.service.StatisticsDummyDataGenerator;
import com.hyerijang.dailypay.statistics.service.StatisticsService;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final Environment environment;
    private final StatisticsDummyDataGenerator dummyDataGenerator;
    private final StatisticsService statisticsService;

    // === 통계  API === //

    /**
     * 지난 달 대비 총액 및 카테고리 별 소비율을 반환
     */
    public ResponseEntity<Result> getExpenseComparison(@RequestParam String condition,
        Authentication authentication) {

        Result result;

        switch (condition) {
            case "lastMonth":
                // 지난 달 대비 총액 및 카테고리 별 소비율
                result = Result.builder()
                    .data(statisticsService.getExpenseComparisonLastMonth(authentication))
                    .build();
                break;
            case "lastWeek":
                // 지난주 같은 요일 대비 소비율
//                result = Result.builder().data(statisticsService.getLastWeekSameDayComparison(authentication)).build();
                break;
            case "otherUser":
                // 다른 유저 대비 소비율
//                result = Result.builder().data(statisticsService.getExpenseComparisonWithOtherUser(authentication)).build();
                break;
            default:
                // 다른 경우에 대한 처리 (예: 유효하지 않은 조건)
                throw new ApiException(ExceptionEnum.WRONG_EXPENSE_COMPARISON_CONDITION);
        }
        return ResponseEntity.ok().body(result);
    }


    @Getter
    @Builder
    static class Result<T> {

        private int count;
        private T data; // 리스트의 값
    }

    // == 더미데이터 생성 == //

    /**
     * 개발 환경에서만 실행가능한 API, (application-dev.yml)
     */
    @ExeTimer
    @PostMapping("/dummy-data")
    void generateDummy(Authentication authentication) {

        //개발 환경인지 체크
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isNotDevActive = Arrays.stream(activeProfiles)
            .noneMatch(profile -> profile.equals("dev"));
        if (isNotDevActive) {
            throw new ApiException(ExceptionEnum.NOT_DEV_ENVIRONMENT);
        }

        dummyDataGenerator.generateDummy(authentication);
    }

}
