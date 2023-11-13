package com.hyerijang.dailypay.expense.dto;

import com.hyerijang.dailypay.budget.domain.Category;
import java.time.LocalDateTime;

public record UpdateExpenseRequest
    (
        Long id,
        Category category,
        Long amount,
        String memo,
        boolean excludeFromTotal,
        LocalDateTime expenseDate
    ) {

}
