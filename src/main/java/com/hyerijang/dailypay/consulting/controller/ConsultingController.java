package com.hyerijang.dailypay.consulting.controller;

import static java.lang.Math.max;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyerijang.dailypay.auth.CurrentUser;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetResponse;
import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.consulting.service.ConsultingService;
import com.hyerijang.dailypay.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "consulting", description = "지출 컨설팅 API")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RestController
@RequestMapping("/api/v1/consulting")
@RequiredArgsConstructor
public class ConsultingController {

    private static final Long MIN_EXPENSE_OF_A_DAY = 10000L; // 하루 최소 소비 금액
    private final ConsultingService consultingService;


    @ExeTimer
    @GetMapping("/proposal-info")
    @Operation(summary = "오늘 지출 추천", description = "오늘 지출 추천")
    public ResponseEntity<Result> getProposalInfo(@CurrentUser User user) {
        // 1.이번 달 남은 예산 계산
        Long budgetRemainingForThisMonth = consultingService.getBudgetRemainingForThisMonth(
            user.getId());

        // 2. 오늘 쓸 수 있는 금액 = (이번달 남은 예산) / (이번 달 남은 일 수)
        Long todayExpenseProposal = budgetRemainingForThisMonth / getRemainingDaysInMonth();

        // 3. 지출 상황에 따라 응원멘트 변경
        String comments = setComment(todayExpenseProposal);

        //2-1 오늘 쓸 수 있는 금액이 일일 최소 소비금액 보다 작은경우 일일최소금액 보여줌
        todayExpenseProposal =
            todayExpenseProposal < MIN_EXPENSE_OF_A_DAY ? MIN_EXPENSE_OF_A_DAY
                : todayExpenseProposal;
        // 3.카테고리 별 제안액
        List<BudgetResponse> proposalResponse = consultingService.getProposalInfo(
            todayExpenseProposal);

        // 로그
        log.info("이번달 남은 예산 = {}", budgetRemainingForThisMonth);
        log.info("오늘 쓸 수 있는 금액 = {}", todayExpenseProposal);
        log.info("카테고리 별 제안액 = {}", proposalResponse);
        return ResponseEntity.ok().body(Result.builder()
            .budgetRemainingForThisMonth(budgetRemainingForThisMonth)
            .todayExpenseProposal(todayExpenseProposal)
            .comments(comments)
            .data(proposalResponse)
            .build());
    }

    private static String setComment(Long todayExpenseProposal) {
        String comments =
            todayExpenseProposal < MIN_EXPENSE_OF_A_DAY ? "이번달은 지출이 많네요. 남은 기간동안 노력해 봅시다."
                : "아주 잘하고 계세요!";
        comments = todayExpenseProposal <= 0 ? "이번 달 예산을 넘어섰어요! 열심히 절약해야겠네요." : comments;
        return comments;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Result<T> {

        //[D-1]
        private Long budgetRemainingForThisMonth; // 이번 달 남은 예산
        private Long todayExpenseProposal; // 오늘 쓸 수 있는 금액
        private String comments; // 응원 멘트

        //[D-2]
        private Long budgetForThisMonth; //이번 달 예산
        private Long getAmountSpentThisMonth; //이번 달 남은 예산

        // 공통
        private T data; // 카테고리 별 제안액

    }

    private static int getRemainingDaysInMonth() {
        // 현재 날짜를 가져옵니다.
        LocalDate currentDate = LocalDate.now();

        // 현재 년도와 월을 가져옵니다.
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();

        // 이번 달의 총 일수를 가져옵니다.
        int totalDaysInMonth = YearMonth.of(year, month).lengthOfMonth();

        // 현재 일을 뺀 남은 일수를 계산합니다.
        int remainingDays = totalDaysInMonth - currentDate.getDayOfMonth();

        return remainingDays;
    }


    @ExeTimer
    @Operation(summary = "오늘 지출 안내 ", description = "오늘 지출 안내 ")
    @GetMapping("/today-expenses")
    public ResponseEntity<Result> getTodayExpenses(@CurrentUser User user) {

        // 1.이번 달 예산
        Long budgetForThisMonth = consultingService.getBudgetThisMonth(user.getId());
        // 2.이번 달 남은 예산 계산
        Long getAmountSpentThisMonth = consultingService.getAmountSpentThisMonth(user.getId());
        // 3. 이번달 카테고리 별 지출 통계
        Map<Category, BigDecimal> expenseStatisticsByCategory = consultingService.getExpenseStatisticsByCategory(
            user.getId());
        // 4. 이번 달 카테고리 별 예산
        List<BudgetResponse> budgetsByCategoryInThisMonth = consultingService.getBudgetsByCategoryInThisMonth(
            user.getId());

        // 로그
        log.info("이번 달 예산 = {}", budgetForThisMonth);
        log.info("이번달 지출 금액 = {}", getAmountSpentThisMonth);
        log.info("카테고리 별 지출 금액 = {}", expenseStatisticsByCategory);
        log.info("이번 달 카테고리 별 예산 = {}", budgetsByCategoryInThisMonth);

        // 5. 3와 4를 결합하여 유저에게 지출 분석 데이터 제공
        Map<Category, CombinedDataDto> analysisData = analysis(
            expenseStatisticsByCategory, budgetsByCategoryInThisMonth);

        return ResponseEntity.ok().body(Result.builder()
            .budgetForThisMonth(budgetForThisMonth)
            .getAmountSpentThisMonth(getAmountSpentThisMonth)
            .data(analysisData)
            .build());
    }

    /**
     * 유저에게 지출 분석 데이터 제공. 제공되는 분석 데이터는 '예산에 등록되어 있는 카테고리 한정'입니다.
     */
    private Map<Category, CombinedDataDto> analysis(
        Map<Category, BigDecimal> expenseStatisticsByCategory,
        List<BudgetResponse> budgetsByCategoryInThisMonth) {
        Map<Category, CombinedDataDto> combinedDataByCategory = new LinkedHashMap<>();

        for (BudgetResponse budgetResponse : budgetsByCategoryInThisMonth) {
            //카테고리 (예산에 등록되어 있는 카테고리 한정)
            Category expectedCategory = budgetResponse.category();
            //해당 카테고리의 지출액
            Long expenseAmount = expenseStatisticsByCategory.getOrDefault(budgetResponse.category(),
                BigDecimal.ZERO).longValue(); //지출액이 없는경우 0으로 설정
            //해당 카테고리의 예산액
            Long budgetAmount = budgetResponse.amount();

            //위험도 (퍼센티지) 계산
            double riskRate = 0.0;
            if (budgetAmount != 0L) {
                riskRate = ((double) expenseAmount / budgetAmount) * 100; // 퍼센티지 (riskRate %)
            }

            CombinedDataDto combinedDataDto = new CombinedDataDto(
                (int) riskRate, budgetAmount - expenseAmount, expenseAmount, budgetAmount);
            combinedDataByCategory.put(expectedCategory, combinedDataDto);
        }
        return combinedDataByCategory;
    }

    @Getter
    @AllArgsConstructor
    private static class CombinedDataDto //카테고리의 예산
    {

        private Integer riskRate;
        private Long remainingBudgetByCategory; //카테고리의 남은 예산
        private Long expenseByCategory; // 카테고리의 지출
        private Long categoryBudge;

        public void add(Long amount) {
            this.remainingBudgetByCategory = max(0,
                remainingBudgetByCategory - amount); //남은 예산액은 0 ~ 양수만 표시
            this.expenseByCategory += amount;
        }
    }

}