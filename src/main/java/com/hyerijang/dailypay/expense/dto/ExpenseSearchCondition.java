package com.hyerijang.dailypay.expense.dto;

import com.hyerijang.dailypay.budget.domain.Category;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ExpenseSearchCondition(
    LocalDateTime start,
    LocalDateTime end,
    Long userId,
    Category category,
    Long minAmount,
    Long maxAmount,
    Boolean exclusion
) {

    public static ExpenseSearchCondition of(GetAllExpenseParam getAllExpenseParam, Long userId) {
        //시작일의 0시 0분 0초
        LocalDateTime start =
            getAllExpenseParam.start() != null ? getAllExpenseParam.start().atStartOfDay() : null;

        //종료일의 23시 59분 59초
        LocalDateTime end =
            getAllExpenseParam.end() != null ? getAllExpenseParam.end().atTime(23, 59, 59) : null;

        return ExpenseSearchCondition.builder()
            .start(start)
            .end(end)
            .userId(userId)
            .category(getAllExpenseParam.category())
            .minAmount(getAllExpenseParam.min_amount())
            .maxAmount(getAllExpenseParam.max_amount())
            .build();
    }

}
