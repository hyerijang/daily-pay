package com.hyerijang.dailypay.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.domain.Expense;
import java.time.LocalDateTime;

public record UpdateExpenseRequest
    (
        Category category,
        Long amount,
        String memo,
        Boolean excludeFromTotal,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expenseDate
    ) {

    public void updateFoundWithRequest(Expense found) {
        found.update(category, amount, memo, excludeFromTotal, expenseDate);
    }
}
