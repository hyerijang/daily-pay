package com.hyerijang.dailypay.expense.dto;

import com.hyerijang.dailypay.budget.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 지출 목록 v1, v2에서 조회 조건을  쿼리 스트링으로 받기 위해 사용되었습니다. v3부터 QueryDsl을 적용하여 해당 record는 더이상 사용하지 않습니다. v3의
 * 쿼리 스트링은 {@link ExpenseSearchCondition} 참조
 */
@Deprecated
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
    Category category,

    //아래 변수명은 Java 네이밍 컨벤션에 맞지 않지만, 
    //record 내부의 @RequestParam에서 name, value를 지정해도 컨트롤러가 이를 인식하지 못해서 변수명을 카멜케이스로 지정함
    @Schema(description = "최소 금액")
    @RequestParam(name = "min_amount")
    Long min_amount,

    @Schema(description = "최대 금액")
    @RequestParam(name = "max_amount")
    Long max_amount
) {

}
