package com.hyerijang.dailypay.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Schema(description = "지출 생성 요청 DTO")
public record CreateExpenseRequest
    (
        @Schema(description = "카테고리")
        Category category,
        @Schema(description = "지출액")
        Long amount,
        @Schema(description = "메모")

        String memo,
        @Schema(description = "전체 지출 제외 유무")

        Boolean excludeFromTotal,
        @Schema(description = "지출일시")
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
