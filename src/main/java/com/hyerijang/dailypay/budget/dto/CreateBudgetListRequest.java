package com.hyerijang.dailypay.budget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyerijang.dailypay.budget.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "예산 생성 및 업데이트 요청")
@Getter
public class CreateBudgetListRequest {

    @NotNull
    private List<CreateBudgetDetail> data;

    @NotNull
    @Schema(description = "예산 년월")
    @JsonFormat(pattern = "yyyy-MM") //형식 : yyyy-MM
    private YearMonth yearMonth;

    @Schema(description = "카테고리 및 카테고리 별 남은 예산액")
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateBudgetDetail {

        @NotNull
        @Schema(description = "카테고리")
        private Category category;

        @NotNull
        @PositiveOrZero
        @Schema(description = "카테고리 별 남은 예산액")
        private Long amount;
    }

}
