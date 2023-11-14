package com.hyerijang.dailypay.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.user.domain.User;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateExpenseRequest
    (
        Category category,
        Long amount,
        String memo,
        boolean excludeFromTotal,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime expenseDate
    ) {

    public Expense toEntity(@NotNull User user) {
        return Expense.builder()
            .user(user)
            .amount(this.amount)
            .category(this.category)
            .excludeFromTotal(this.excludeFromTotal)
            .expenseDate(this.expenseDate)
            .memo(this.memo)
            .build();
    }
}
