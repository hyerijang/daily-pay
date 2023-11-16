package com.hyerijang.dailypay.statistics.service;

import static java.lang.Math.min;

import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.expense.repository.ExpenseRepository;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsDummyDataGenerator {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Transactional
    public void generateDummy(Authentication authentication) {
        //유저 확인
        log.info("유저 확인");
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(
                ExceptionEnum.NOT_EXIST_USER));
        log.info("유저 확인");
        log.debug("유저 확인");

        generateDummy(user);
    }


    private void generateDummy(User user) {
        final LocalDate TODAY = LocalDate.now();
        final int SIZE = 100;

        log.info("=== 더미 데이터 생성 시작 ===");
        //1. 유저의 지난달 소비 데이터 100개 생성
        createAndSaveExpenses(user, DateType.LAST_MONTH, SIZE); //유저, 지난달

        //2. 유저의 7일전 (지난주, 같은요일) 소비 데이터 생성
        createAndSaveExpenses(user, DateType.LAST_WEEK,
            SIZE); //유저, 지난주

        //3.다른 유저들의 오늘 소비 데이터 생성
        User otherUser = userRepository.findAll().stream()
            .filter(x -> x.getId() != user.getId())
            .findAny()
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_OTHER_USER));
        createAndSaveExpenses(otherUser, DateType.TODAY,
            SIZE); //다른유저, 오늘

        log.info("=== 더미 데이터 생성 완료 ===");

    }


    /**
     * 랜덤한 카테고리 가져오기
     */
    private static Category getRandomCategory() {
        Category[] categories = Category.values();
        int index = ThreadLocalRandom.current().nextInt(categories.length);
        return categories[index];
    }

    /**
     * 랜덤한 지출액 설정
     */
    private static Long getRandomAmount() {
        return ThreadLocalRandom.current().nextLong(1, 30)
            * 1000; // 1천~ 3만 랜덤 생성 (가독성을 위해 천원 단위로 끊음)
    }


    // === 지출 생성 및 저장 로직 ==
    private void createAndSaveExpenses(User user, DateType lastMonth, int size) {
        //생성
        List<Expense> expenses = IntStream.range(0, size)
            .mapToObj(
                idx -> createRandomExpense(user, idx,
                    generateRandomDateTime(lastMonth))) //지난달
            .toList();

        //저장
        expenseRepository.saveAll(expenses);
    }

    /**
     * 지출 1개 생성
     */
    private static Expense createRandomExpense(User user, Integer idx,
        LocalDateTime localDateTime) {
        Category category = getRandomCategory();
        Long amount = getRandomAmount();
        String description = "더미 데이터 " + idx;
        boolean flag = false;
        return new Expense(user, category, amount, description, flag, localDateTime);
    }


    // === 랜덤한 LocalDateTime 생성 로직 ===//
    private enum DateType {
        LAST_MONTH,
        LAST_WEEK,
        TODAY
    }

    private static LocalDateTime generateRandomDateTime(DateType dateType) {

        switch (dateType) {
            case LAST_MONTH:
                return generateRandomDateTimeOfLastMonth();
            case LAST_WEEK:
                return generateRandomDateTimeOfLastWeek();
            case TODAY:
                return generateRandomDateTimeOfToday();
            default:
                throw new IllegalArgumentException("Invalid dateType: " + dateType);
        }
    }


    /**
     * 오늘이 mm월 dd일 일때, 지난 달 1일 ~ 지난 달 b 일까지의 LocalDateTime 을 랜덤하게 생성
     */
    private static LocalDateTime generateRandomDateTimeOfLastMonth() {
        int yyyy = LocalDateTime.now().getYear(); //year
        int mm = LocalDateTime.now().getMonth().getValue(); //month
        int dd = LocalDateTime.now().getDayOfMonth(); // day

        int daysInLastMonth = YearMonth.of(yyyy, mm).lengthOfMonth(); //지난달의 일수(eg. 2월이라면 28일)

        //시작일 : 지난달 1일 , 종료일 : 지난달 b일
        LocalDateTime startDateTime = LocalDateTime.of(yyyy, Month.of(mm - 1), 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(yyyy, Month.of(mm - 1),
            min(dd, daysInLastMonth), 23, 59); // 일 : min(오늘 날짜,지난 달의 마지막 일)

        return generateRandomDateTimeBetween(startDateTime, endDateTime);
    }

    /**
     * 일주일 전 ~오늘까지의 LocalDateTime 을 랜덤하게 생성
     */
    private static LocalDateTime generateRandomDateTimeOfLastWeek() {
        //오늘
        LocalDateTime today = LocalDateTime.now();
        //7일전
        LocalDateTime sevenDaysAgo = today.minus(7, ChronoUnit.DAYS);
        return generateRandomDateTimeBetween(sevenDaysAgo, today); // 7일전 ~ 오늘
    }

    /**
     * 오늘 0시  ~ 현재 시각 까지의 LocalDateTime 을 랜덤하게 생성
     */
    private static LocalDateTime generateRandomDateTimeOfToday() {
        //오늘 , 0시
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        //오늘, 현재 시각
        LocalDateTime todayCurrent = LocalDateTime.now();
        return generateRandomDateTimeBetween(todayStart, todayCurrent);
    }


    /**
     * 시작일 ~ 종료일 사이의 랜덤 LocalDateTime 생성
     */
    private static LocalDateTime generateRandomDateTimeBetween(LocalDateTime startDateTime,
        LocalDateTime endDateTime) {
        long startEpochSecond = startDateTime.toEpochSecond(
            ZoneOffset.UTC);//startDateTime의 Unix 시간(Epoch 시간)
        long endEpochSecond = endDateTime.toEpochSecond(
            ZoneOffset.UTC);//endDateTime의 Unix 시간(Epoch 시간)

        //랜덤하게 생성
        long randomEpochSecond = ThreadLocalRandom.current()
            .nextLong(startEpochSecond, endEpochSecond);

        return LocalDateTime.ofEpochSecond(randomEpochSecond, 0, ZoneOffset.UTC);
    }

}
