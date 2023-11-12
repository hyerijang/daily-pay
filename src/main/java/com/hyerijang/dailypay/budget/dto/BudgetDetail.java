package com.hyerijang.dailypay.budget.dto;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import java.util.List;

public record BudgetDetail(Long id,
                           String yyyyMM,
                           Category category,
                           Long amount) {

    public static List<BudgetDetail> getBudgetDetailList(List<Budget> budgets) {
        return budgets.stream().map(
                x -> new BudgetDetail(x.getId(), x.getYyyyMM(), x.getCategory(), x.getBudgetAmount()))
            .toList();
    }
}
