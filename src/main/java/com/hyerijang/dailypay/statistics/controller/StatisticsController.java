package com.hyerijang.dailypay.statistics.controller;

import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.statistics.service.StatisticsDummyDataGenerator;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsDummyDataGenerator dummyDataGenerator;

    private final Environment environment;

    // 개발 환경에서만 실행가능한 API, (application-dev.yml)
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
