package com.hyerijang.dailypay.budget.controller;


import com.hyerijang.dailypay.budget.dto.BudgetDto;
import com.hyerijang.dailypay.budget.dto.CategoryDto;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest;
import com.hyerijang.dailypay.budget.dto.RecommendBudgetRequest;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.budget.service.BudgetService;
import com.hyerijang.dailypay.common.aop.ExeTimer;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    @GetMapping("/categories")
    ResponseEntity<Result> getBudgetCategories() {
        List<CategoryDto> categoryDtoList = budgetService.getCategories();

        return ResponseEntity.ok()
            .body(Result.builder()
                .count(categoryDtoList.size())
                .data(categoryDtoList).build());
    }

    @Getter
    @Builder
    static class Result<T> {

        private int count;
        private T data; // 리스트의 값
    }

    /**
     * 예산 설정 및 업데이트 (금액만 변경 가능)
     */

    @ExeTimer
    @PostMapping
    ResponseEntity<Result> updateBudgets(@RequestBody CreateBudgetListRequest request,
        Authentication authentication) {
        List<BudgetDto> data = budgetService.update(request, authentication);
        Result result = Result.builder().count(data.size()).data(data).build();
        return ResponseEntity.ok().body(result);
    }

    /**
     * 예산 설계(추천) API
     */
    @ExeTimer
    @GetMapping
    ResponseEntity<Result> recommendBudgets(@RequestBody RecommendBudgetRequest request,
        Authentication authentication) {
        List<BudgetDto> data = budgetService.recommend(request);
        Result result = Result.builder().count(data.size()).data(data).build();
        return ResponseEntity.ok().body(result);
    }

}
