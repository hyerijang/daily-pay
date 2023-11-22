package com.hyerijang.dailypay.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.domain.Expense;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "지출 업데이트 요청")
public record UpdateExpenseRequest
    (
        @Schema(description = "카테고리")
        Category category,

        @Schema(description = "지출 금액")
        Long amount,

        @Schema(description = "메모")
        String memo,

        @Schema(description = "전체 지출 제외 유무")
        Boolean excludeFromTotal,

        @Schema(description = "지출일시")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expenseDate
    ) {

    public void updateFoundWithRequest(Expense found) {
        found.update(category, amount, memo, excludeFromTotal, expenseDate);
    }
}
