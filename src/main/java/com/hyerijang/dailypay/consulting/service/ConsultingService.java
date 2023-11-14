package com.hyerijang.dailypay.consulting.service;

import com.hyerijang.dailypay.budget.dto.BudgetDto;
import com.hyerijang.dailypay.budget.service.BudgetService;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.time.YearMonth;
import java.util.List;
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

    private Long getAmountSpentThisMonth(Long userId) {
        //이번달 전체 지출
        List<Expense> allUserExpensesInThinMonth = expenseService.getAllUserExpensesIn(
            YearMonth.now(),
            userId);

        return allUserExpensesInThinMonth.stream().filter(expense -> !expense.getExcludeFromTotal())
            .mapToLong(e -> e.getAmount()).sum();
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


}