package com.hyerijang.dailypay.budget.dto;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import java.time.YearMonth;
import java.util.List;

public record BudgetDetail(Long id,
                           YearMonth yearMonth,
                           Category category,
                           Long amount) {

    public static List<BudgetDetail> getBudgetDetailList(List<Budget> budgets) {
        return budgets.stream().map(
                x -> new BudgetDetail(x.getId(), x.getYearMonth(), x.getCategory(),
                    x.getBudgetAmount()))
            .toList();
    }
}
