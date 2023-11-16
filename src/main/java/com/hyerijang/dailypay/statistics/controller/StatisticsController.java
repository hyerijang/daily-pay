package com.hyerijang.dailypay.statistics.controller;

import com.hyerijang.dailypay.statistics.service.StatisticsDummyDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsDummyDataGenerator dummyDataGenerator;

    // FIXME : 해당 API 호출 시 더미데이터 생성됨, 운영 환경에서는 제거하거나 인가로 특정 권한 있는 사람만 실행할 수 있도록 수정
    @PostMapping()
    void generateDummy(Authentication authentication) {
        dummyDataGenerator.generateDummy(authentication);
    }

}
