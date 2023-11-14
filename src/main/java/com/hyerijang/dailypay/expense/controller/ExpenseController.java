package com.hyerijang.dailypay.expense.controller;

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
     * 새 지출 내역 (단건) 생성 API
     */
    @PostMapping
    public ResponseEntity<Result> createExpense(
        @RequestBody CreateExpenseRequest createExpenseRequest, Authentication authentication) {
        ExpenseDto createdExpenseDto = expenseService.createExpense(createExpenseRequest,
            authentication);
        return ResponseEntity.ok().body(Result.builder().data(createdExpenseDto).build());

    }

    /**
     * 유저의 지출 내역 (목록) 조회 API
     */
    @GetMapping
    public ResponseEntity<Result> getAllExpenses() {
        List<ExpenseDto> userAllExpenses = expenseService.getUserAllExpenses();
        return ResponseEntity.ok().body(Result.builder().data(userAllExpenses).build());
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
    static class Result<T> {

        private int count;
        private T data; // 리스트의 값
    }


}
