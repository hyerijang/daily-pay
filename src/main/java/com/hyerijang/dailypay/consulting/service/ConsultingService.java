package com.hyerijang.dailypay.consulting.service;

import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetDto;
import com.hyerijang.dailypay.budget.service.BudgetService;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
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
@RequiredArgsConstructor
@Transactional
public class ConsultingService {

    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;

    /**
     * 이번 달 남은 예산 계산
     */
    public Long getBudgetRemainingForThisMonth(Authentication authentication) {
        // (이번달) 남은 예산 = 예산 - 사용금액
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));

        return getBudgetThisMonth(user.getId()) - getAmountSpentThisMonth(user.getId());
    }

    /**
     * 이번 달 전체 지출
     */
    private Long getAmountSpentThisMonth(Long userId) {
        //이번달 전체 지출
        List<Expense> allUserExpensesInThinMonth = expenseService.getAllUserExpensesIn(
            YearMonth.now(),
            userId);

        return allUserExpensesInThinMonth.stream().filter(expense -> !expense.getExcludeFromTotal())
            .mapToLong(e -> e.getAmount()).sum();
    }

    /**
     * 이번 달 전체 지출
     */
    public Long getAmountSpentThisMonth(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));
        return getAmountSpentThisMonth(user.getId()); //인자가 다른 동일한 메서드 호출
    }


    /**
     * 이번달 예산
     */
    private Long getBudgetThisMonth(Long userId) {
        return budgetService.getTotalAmountOfBudgetIn(YearMonth.now(), userId);
    }

    /**
     * 카테고리 별 제안액
     */
    public List<BudgetDto> getProposalInfo(Long finalTodayExpenseProposal) {
        List<BudgetDto> recommend = budgetService.recommend(finalTodayExpenseProposal);
        return recommend;
    }


    public Long getBudgetThisMonth(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));

        return getBudgetThisMonth(user.getId());
    }

    /**
     * 오늘 지출 내역 전체
     */
    public List<ExpenseDto> getTodayExpenseInfo(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));
        return expenseService.getAllUserExpensesIn(LocalDate.now(),
            user.getId());
    }

    public Map<Category, BigDecimal> getExpenseStatisticsByCategory(Authentication authentication) {
        List<ExpenseDto> todayExpenseInfo = getTodayExpenseInfo(authentication);

        // excludeFromTotal true이면 합계에서 제외
        todayExpenseInfo.stream().filter(expenseDto -> !expenseDto.excludeFromTotal()).toList();

        return todayExpenseInfo.stream()
            .filter(expenseDto -> !expenseDto.excludeFromTotal())
            .collect(
                Collectors.groupingBy(ExpenseDto::category,
                    Collectors.reducing(BigDecimal.ZERO,
                        exDto -> BigDecimal.valueOf(exDto.amount()), BigDecimal::add)
                ));

    }

    /**
     * 이번 달 카테고리 별 예산 dto 반환
     */
    public List<BudgetDto> getBudgetsByCategoryInThisMonth(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));

        return budgetService.getBudgetDtoListOfAllCategoryListIn(YearMonth.now(), user.getId());

    }
}