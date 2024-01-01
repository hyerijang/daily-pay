package com.hyerijang.dailypay.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.domain.Expense;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ExpenseResponse(
    Long id,
    Long userId,
    Category category,
    Long amount,
    String memo,
    Boolean excludeFromTotal,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime expenseDate
) {

    public static ExpenseResponse of(Expense savedExpense) {
        return ExpenseResponse.builder()
            .id(savedExpense.getId())
            .userId(savedExpense.getUser().getId())
            .category(savedExpense.getCategory())
            .amount(savedExpense.getAmount())
            .memo(savedExpense.getMemo())
            .excludeFromTotal(savedExpense.getExcludeFromTotal())
            .expenseDate(savedExpense.getExpenseDate())
            .build();
    }

    public static List<ExpenseResponse> getExpenseDtoList(List<Expense> expenses) {
        return expenses.stream().map((expense) -> ExpenseResponse.of(expense)).toList();
    }

    @QueryProjection
    public ExpenseResponse {
    }


}
