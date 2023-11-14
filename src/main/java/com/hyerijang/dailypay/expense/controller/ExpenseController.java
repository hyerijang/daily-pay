package com.hyerijang.dailypay.expense.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyerijang.dailypay.expense.dto.CreateExpenseRequest;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.UpdateExpenseRequest;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import java.util.List;
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
    public ResponseEntity<Result> getAllExpenses(Authentication authentication) {
        List<ExpenseDto> userAllExpenses = expenseService.getUserAllExpenses(authentication);
        return ResponseEntity.ok()
            .body(Result.builder().data(userAllExpenses).count(userAllExpenses.size()).build());
    }

    /***
     * 유저의 지출 내역(단건) 조회 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result> getExpenseById(@PathVariable Long id) {
        ExpenseDto expenseDto = expenseService.getExpenseById(id);
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
        private T data; // 리스트의 값
    }


}
