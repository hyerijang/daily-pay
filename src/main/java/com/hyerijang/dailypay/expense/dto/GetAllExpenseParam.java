package com.hyerijang.dailypay.expense.dto;

import com.hyerijang.dailypay.budget.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

@Schema(description = "지출 내역 목록 조회 조건")
public record GetAllExpenseParam(
    @Schema(description = "시작일")
    @RequestParam(name = "start")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate start,
    
    @Schema(description = "종료일")
    @RequestParam(name = "end")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate end,

    @Schema(description = "카테고리")
    @RequestParam(name = "category")
    Category category
) {

}
