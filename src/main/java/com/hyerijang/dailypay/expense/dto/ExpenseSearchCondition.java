package com.hyerijang.dailypay.expense.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ExpenseSearchCondition(
    LocalDateTime start,
    LocalDateTime end,
    Long userId
) {

    public static ExpenseSearchCondition of(GetAllExpenseParam getAllExpenseParam, Long userId) {
        return ExpenseSearchCondition.builder()
            .start(getAllExpenseParam.start().atStartOfDay()) //시작일으리 0시 0분 0초
            .end(getAllExpenseParam.end().atTime(23, 59, 59)) //종료일의 23시 59분 59초
            .userId(userId)
            .build();
    }
}
