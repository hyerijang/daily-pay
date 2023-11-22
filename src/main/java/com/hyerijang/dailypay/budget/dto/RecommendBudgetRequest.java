package com.hyerijang.dailypay.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예산 추천 요청")
public record RecommendBudgetRequest(
    @Schema(description = "유저의 예산 총액")
    Long userBudgetTotalAmount
) {

}
