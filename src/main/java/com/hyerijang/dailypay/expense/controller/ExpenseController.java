package com.hyerijang.dailypay.expense.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.dto.CreateExpenseRequest;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.GetAllExpenseParam;
import com.hyerijang.dailypay.expense.dto.UpdateExpenseRequest;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {


    private final ExpenseService expenseService;

    /**
     * 새 지출 내역 (단건) 생성 API <br> 본인의 지출 내역만 생성 가능
     */
    @PostMapping
    public ResponseEntity<Result> createExpense(
        @RequestBody CreateExpenseRequest createExpenseRequest, Authentication authentication) {
        ExpenseDto createdExpenseDto = expenseService.createExpense(createExpenseRequest,
            authentication);
        return ResponseEntity.ok().body(Result.builder().data(createdExpenseDto).build());

    }

    /**
     * 유저의 지출 내역 (목록) 조회 API br> 본인의 지출 내역만 조회 가능
     */
    @GetMapping
    public ResponseEntity<Result> getAllExpenses(GetAllExpenseParam getAllExpenseParam,
        Authentication authentication) {

        //1. 기간 별 지출 내역 조회
        List<ExpenseDto> userAllExpenses = expenseService.getUserAllExpenses(getAllExpenseParam,
            authentication);

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

    /***
     * 유저의 지출 내역(단건) 조회 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result> getExpenseById(@PathVariable Long id,
        Authentication authentication) {
        ExpenseDto expenseDto = expenseService.getExpenseById(id, authentication);
        if (expenseDto != null) {
            return ResponseEntity.ok().body(Result.builder().data(expenseDto).build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 유저의 지출 내역(단건) 수정 API
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result> updateExpense(@PathVariable Long id,
        @RequestBody UpdateExpenseRequest updateExpenseRequest) {
        ExpenseDto updatedExpenseDto = expenseService.updateExpense(id, updateExpenseRequest);
        if (updatedExpenseDto != null) {
            return ResponseEntity.ok().body(Result.builder().data(updatedExpenseDto).build());
        }
        return ResponseEntity.notFound().build();
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Result<T> {

        private Integer count;
        private Long totalExpense;
        private T data; // 리스트의 값
        private Map<Category, BigDecimal> CategoryWiseExpenseSum;
    }


}
