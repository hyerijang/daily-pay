package com.hyerijang.dailypay.consulting.service;

import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetResponse;
import com.hyerijang.dailypay.budget.service.BudgetService;
import com.hyerijang.dailypay.expense.dto.ExpenseResponse;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConsultingService {

    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;

    /**
     * 이번 달 남은 예산 계산
     */
    public Long getBudgetRemainingForThisMonth(Long userId) {
        // (이번달) 남은 예산 = 예산 - 사용금액
        return getBudgetThisMonth(userId) - getAmountSpentThisMonth(userId);
    }

    /**
     * 이번 달 전체 지출
     */
    public Long getAmountSpentThisMonth(Long userId) {
        //이번달 전체 지출
        List<ExpenseResponse> allUserExpensesInThinMonth = expenseService.getAllUserExpenseDtoListIn(
            YearMonth.now(),
            userId);

        return allUserExpensesInThinMonth.stream().filter(expense -> !expense.excludeFromTotal())
            .mapToLong(ExpenseResponse::amount).sum();
    }

    /**
     * 이번달 예산
     */
    public Long getBudgetThisMonth(Long userId) {
        return budgetService.getTotalAmountOfBudgetIn(YearMonth.now(),
            userId);
    }

    /**
     * 카테고리 별 제안액
     */
    public List<BudgetResponse> getProposalInfo(Long finalTodayExpenseProposal) {
        List<BudgetResponse> recommend = budgetService.recommend(finalTodayExpenseProposal);
        return recommend;
    }


    /**
     * 오늘 지출 내역 전체
     */
    public List<ExpenseResponse> getTodayExpenseInfo(Long userId) {
        return expenseService.getAllUserExpenseDtoListIn(LocalDate.now(),
            userId);
    }

    public Map<Category, BigDecimal> getExpenseStatisticsByCategory(Long userId) {
        List<ExpenseResponse> todayExpenseInfo = getTodayExpenseInfo(userId);

        return todayExpenseInfo.stream()
            .filter(expenseDto -> !expenseDto.excludeFromTotal())
            .collect(
                Collectors.groupingBy(ExpenseResponse::category,
                    Collectors.reducing(BigDecimal.ZERO,
                        exDto -> BigDecimal.valueOf(exDto.amount()), BigDecimal::add)
                ));

    }

    /**
     * 이번 달 카테고리 별 예산 반환
     */
    public List<BudgetResponse> getBudgetsByCategoryInThisMonth(Long userId) {
        return budgetService.getBudgetDtoListOfAllCategoryListIn(YearMonth.now(), userId);
    }
}