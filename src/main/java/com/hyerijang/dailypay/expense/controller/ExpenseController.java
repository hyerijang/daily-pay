package com.hyerijang.dailypay.expense.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyerijang.dailypay.auth.CurrentUser;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.expense.dto.CreateExpenseRequest;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import com.hyerijang.dailypay.expense.dto.GetAllExpenseParam;
import com.hyerijang.dailypay.expense.dto.UpdateExpenseRequest;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import com.hyerijang.dailypay.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "expenses", description = "지출 API")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {


    private final ExpenseService expenseService;

    @ExeTimer
    @Operation(summary = "새 지출 내역 (단건) 생성", description = "본인의 지출 내역만 생성 가능")
    @PostMapping
    public ResponseEntity<Result> createExpense(
        @RequestBody @Validated CreateExpenseRequest createExpenseRequest,
        @CurrentUser User user) {
        ExpenseDto createdExpenseDto = expenseService.createExpense(createExpenseRequest,
            user.getId());
        return ResponseEntity.ok().body(Result.builder().data(createdExpenseDto).build());

    }

    @ExeTimer
    @Operation(summary = "유저의 지출 내역(단건) 조회", description = "본인의 지출 내역만 조회 가능")
    @GetMapping("/{id}")
    public ResponseEntity<Result> getExpenseById(@PathVariable Long id,
        @CurrentUser User user) {
        ExpenseDto expenseDto = expenseService.getExpenseById(id, user.getId());
        if (expenseDto != null) {
            return ResponseEntity.ok().body(Result.builder().data(expenseDto).build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @ExeTimer
    @Operation(summary = "유저의 지출 내역(단건) 수정", description = "본인의 지출 내역만 수정 가능")
    @PatchMapping("/{id}")
    public ResponseEntity<Result> updateExpense(@PathVariable Long id,
        @RequestBody @Validated UpdateExpenseRequest updateExpenseRequest,
        @CurrentUser User user) {
        ExpenseDto updatedExpenseDto = expenseService.updateExpense(id, updateExpenseRequest,
            user.getId());
        if (updatedExpenseDto != null) {
            return ResponseEntity.ok().body(Result.builder().data(updatedExpenseDto).build());
        }
        return ResponseEntity.notFound().build();
    }


    @ExeTimer
    @Operation(summary = "유저의 지출 내역(단건) 삭제", description = "본인의 지출 내역만 삭제 가능")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteExpense(@PathVariable Long id,
        @CurrentUser User user) {
        ExpenseDto updatedExpenseDto = expenseService.deleteExpense(id, user.getId());
        if (updatedExpenseDto != null) {
            return ResponseEntity.ok().body(Result.builder().data(updatedExpenseDto).build());
        }
        return ResponseEntity.notFound().build();
    }


    @ExeTimer
    @Operation(summary = "유저의 지출 내역(단건)을 지출 합계에서 제외", description = "본인의 지출 내역만 제외 가능")
    @PatchMapping("/{id}/exclude-total-sum")
    public ResponseEntity<Result> excludeFromTotal(@PathVariable Long id,
        @CurrentUser User user) {
        ExpenseDto updatedExpenseDto = expenseService.excludeFromTotal(id, user.getId());
        if (updatedExpenseDto != null) {
            return ResponseEntity.ok().body(Result.builder().data(updatedExpenseDto).build());
        }
        return ResponseEntity.notFound().build();
    }

    @Schema(description = "지출 응답")
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Result<T> {

        @Schema(description = "데이터의 개수")
        private Integer count;
        @Schema(description = "총 지출액")
        private Long totalExpense;
        @Schema(description = "API 응답 결과를 data에 담아서 반환")
        private T data; // 리스트의 값
        @Schema(description = "지출 목록 API의 경우 카테고리 별 지출 합계를 포함")
        private Map<Category, BigDecimal> CategoryWiseExpenseSum;
    }


    @ExeTimer
    @Operation(summary = " 유저의 지출 내역 (목록) 조회", description = "본인의 지출 내역만 조회 가능")
    @GetMapping
    public ResponseEntity<Result> search(GetAllExpenseParam getAllExpenseParam,
        @CurrentUser User user) {
        ExpenseSearchCondition condition = ExpenseSearchCondition.of(getAllExpenseParam,
            user.getId()); //본인 지출내역만 조회 가능

        //1. 기간 별 지출 내역 조회 (QueryDsl)
        List<ExpenseDto> userAllExpenses = expenseService.search(condition);

        //2. 지출 내역 토대로 지출 합계 , 카테고리 별 지출 합계 계산
        //지출 합계 (excludeFromTotal이 true인 경우 제외)
        Long totalExpense = userAllExpenses.stream()
            .filter(exDto -> !exDto.excludeFromTotal())
            .mapToLong(exDto -> exDto.amount()).sum();

        //3. 카테고리 별 지출 합계 (excludeFromTotal이 true인 경우 제외)
        Map<Category, BigDecimal> categoryWiseExpenseSum = userAllExpenses.stream()
            .filter(exDto -> !exDto.excludeFromTotal())
            .collect(Collectors.groupingBy(ExpenseDto::category,
                Collectors.reducing(BigDecimal.ZERO,
                    exDto -> BigDecimal.valueOf(exDto.amount()), BigDecimal::add)
            ));

        return ResponseEntity.ok()
            .body(Result.builder().data(userAllExpenses).count(userAllExpenses.size())
                .totalExpense(totalExpense)
                .CategoryWiseExpenseSum(categoryWiseExpenseSum).build());
    }


}
