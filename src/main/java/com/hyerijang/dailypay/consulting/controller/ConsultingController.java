package com.hyerijang.dailypay.consulting.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyerijang.dailypay.budget.dto.BudgetDto;
import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.consulting.service.ConsultingService;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/consulting")
@RequiredArgsConstructor
public class ConsultingController {

    private static final Long MIN_EXPENSE_OF_A_DAY = 10000L; // 하루 최소 소비 금액
    private final ConsultingService consultingService;

    /**
     * [D-1] 오늘 지출 추천 API
     */
    @ExeTimer
    @GetMapping("/proposal-info")
    public ResponseEntity<Result> getProposalInfo(Authentication authentication) {
        // 1.이번 달 남은 예산 계산
        Long budgetRemainingForThisMonth = consultingService.getBudgetRemainingForThisMonth(
            authentication.getName());
        log.info("이번달 남은 예산 = {}", budgetRemainingForThisMonth);

        // 2. 오늘 쓸 수 있는 금액 = (이번달 남은 예산) / (이번 달 남은 일 수)
        Long todayExpenseProposal = budgetRemainingForThisMonth / getRemainingDaysInMonth();
        log.info("오늘 쓸 수 있는 금액 = {} , 일일 최소 소비금액 = {}", todayExpenseProposal, MIN_EXPENSE_OF_A_DAY);

        // 3. 지출 상황에 따라 응원멘트 변경
        String comments = setComment(todayExpenseProposal);

        //2-1 오늘 쓸 수 있는 금액이 일일 최소 소비금액 보다 작은경우 일일최소금액 보여줌
        todayExpenseProposal =
            todayExpenseProposal < MIN_EXPENSE_OF_A_DAY ? MIN_EXPENSE_OF_A_DAY
                : todayExpenseProposal;
        log.info("오늘 쓸 수 있는 금액 (최종) = {}", todayExpenseProposal);

        // 3.카테고리 별 제안액
        List<BudgetDto> proposalResponse = consultingService.getProposalInfo(
            authentication.getName(),
            todayExpenseProposal);
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

        private Long budgetRemainingForThisMonth; // 이번 달 남은 예산
        private Long todayExpenseProposal; // 오늘 쓸 수 있는 금액
        private T data; // 카테고리 별 제안액
        private String comments; // 응원 멘트
    }

    public static int getRemainingDaysInMonth() {
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

    @GetMapping("/today-expenses")
    public List<Result> getTodayExpenses(Authentication authentication) {
//        consultingService.getTodayExpenses(user);
        return null;
    }
}