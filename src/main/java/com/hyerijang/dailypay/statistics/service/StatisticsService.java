package com.hyerijang.dailypay.statistics.service;

import static java.lang.Math.min;

import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import com.hyerijang.dailypay.statistics.dto.StatisticsDto;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsService {


    private final UserRepository userRepository;
    private final ExpenseService expenseService;

    /**
     * (1) 지난 달 대비 총액 및 카테고리 별 소비율
     */
    public StatisticsDto getExpenseComparisonLastMonth(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));

        //1. 지난 달 대비 총액 소비율
        Long totalExpenseComparison = getTotalExpenseComparisonTotalLastMonth(user);
        //2. 지난 달 대비 카테고리 별 소비율
        Map<Category, Double> categoryExpenseComparison = getCategoryExpenseComparisonLastMonth(
            user);

        return new StatisticsDto(totalExpenseComparison, categoryExpenseComparison);
    }


    /**
     * 지난 달 대비 총액 소비율
     */
    private Long getTotalExpenseComparisonTotalLastMonth(User user) {
        //오늘이 yyyy 년 mm월 dd일 일때
        int yyyy = LocalDateTime.now().getYear(); //year
        int mm = LocalDateTime.now().getMonth().getValue(); //month
        int dd = LocalDateTime.now().getDayOfMonth(); // day

        // 1.지난달 1일 ~ b일 소비액
        Long totalOfLastMonth = getTotal(yyyy, mm - 1, dd, user.getId()).stream()
            .mapToLong(x -> x.amount()).sum();
        // 2.이번 달 1일 ~ b일 소비액
        Long totalOfThisMonth = getTotal(yyyy, mm, dd, user.getId()).stream()
            .mapToLong(x -> x.amount()).sum();

        double v = (double) totalOfThisMonth / totalOfLastMonth;
        return (long) v * 100;
    }

    // yy년 m월 1일~  m월 d일의 소비 내역 리턴
    private List<ExpenseDto> getTotal(int yyyy, int mm, int dd, Long userId) {
        //0월은 존재 하지 않으므로 yy-1년 12월로 변경
        if (mm == 0) {
            yyyy -= 1;
            mm = 12;
        }

        //mm월의 일수 (e.g. 2월이면 28일, 3월은 31일)
        int daysInMonth = YearMonth.of(yyyy, mm).lengthOfMonth();

        //해당 월의 1일 , dd일을 시작과 끝으로 삼는다.
        //- mm월 1일
        LocalDateTime start = LocalDateTime.of(yyyy, Month.of(mm), 1, 0, 0);
        //- mm월 dd일
        LocalDateTime end = LocalDateTime.of(yyyy, Month.of(mm), min(dd, daysInMonth), 23, 59,
            59); // min(주어진 날짜 ,이번 달의 마지막 일), e.g. 31이 주어졌는데, 2월이라 28일까지 밖에 없으면 28.

        // start 부터 end 까지의 소비 내역 DTO로 반환
        return expenseService.getAllUserExpenseDtoListIn(start, end, userId)
            .stream()
            .filter(expenseDto -> !expenseDto.excludeFromTotal())
            .toList();
    }

    /**
     * 지난 달 대비 카테고리 별 소비율
     */
    private Map<Category, Double> getCategoryExpenseComparisonLastMonth(User user) {
        //1. 지난 달 카테고리 별 소비액
        Map<Category, BigDecimal> categoryExpenseInLastMonth = getCategoryExpense(
            expenseService.getAllUserExpenseDtoListIn(YearMonth.now().minusMonths(1),
                user.getId())
        );

        //2. 이번 달 카테고리 별 소비액

        Map<Category, BigDecimal> categoryExpenseInThisMonth = getCategoryExpense(
            expenseService.getAllUserExpenseDtoListIn(YearMonth.now(),
                user.getId())
        );

        //3. 지난달, 이번 달 비교

        Map<Category, Double> comparison = new LinkedHashMap<>();

        for (Category category : categoryExpenseInThisMonth.keySet()) { // 이번달 지출 카테고리
            if (!categoryExpenseInLastMonth.containsKey(category)) {
                //지난 달에는 해당 카테고리 소비 없었음
                continue;
            }

            long lastMonthExpenseInThisCategory = categoryExpenseInLastMonth.get(category)
                .longValue(); //해당 카테고리 지난달 소비액
            long thisMonthExpenseInThisCategory = categoryExpenseInThisMonth.get(category)
                .longValue(); //해당 카테고리 이번달 소비액

            comparison.put(category, ((double) thisMonthExpenseInThisCategory
                / lastMonthExpenseInThisCategory) * 100);
        }

        return comparison;

    }


    private static Map<Category, BigDecimal> getCategoryExpense(List<ExpenseDto> expenseDtoList) {
        Map<Category, BigDecimal> collect = expenseDtoList.stream()
            .filter(expenseDto -> !expenseDto.excludeFromTotal())
            .collect(
                Collectors.groupingBy(ExpenseDto::category,
                    Collectors.reducing(BigDecimal.ZERO,
                        exDto -> BigDecimal.valueOf(exDto.amount()), BigDecimal::add)
                ));

        return collect;
    }


    /**
     * (2) 지난주 같은 요일 대비 소비율
     */
    public Double getLastWeekSameWeekDayComparison(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));

        //오늘 소비 총액
        Long today = expenseService.getAllUserExpenseDtoListIn(LocalDate.now(), user.getId())
            .stream().mapToLong(x -> x.amount()).sum();

        // 지난주 같은 요일의 소비 총액
        Long last = expenseService.getAllUserExpenseDtoListIn(LocalDate.now().minusDays(7),
                user.getId())
            .stream().mapToLong(x -> x.amount()).sum();

        return ((double) today / last) * 100;
    }

    /**
     * (3) 다른 유저 대비 소비율
     */
    public Double getExpenseComparisonWithOtherUser(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));

        //오늘 user의 소비 총액
        Long userExpenseAmount = expenseService.getAllUserExpenseDtoListIn(LocalDate.now(),
                user.getId())
            .stream().mapToLong(x -> x.amount()).sum();

        //오늘 전체 유저들의 소비 평균액
        Long averageExpenseAmount = expenseService.getAverageExpenseAmountOfToday();
        log.info("오늘 {}의 소비 총액 = {}", authentication.getName(), userExpenseAmount);
        log.info("오늘 전체 유저들의 소비 평균액 = {}", averageExpenseAmount);
        log.info("비율 = {}", ((double) userExpenseAmount / averageExpenseAmount) * 100);

        return ((double) userExpenseAmount / averageExpenseAmount) * 100;
    }
}
