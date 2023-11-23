package com.hyerijang.dailypay.budget.controller;


import com.hyerijang.dailypay.auth.CurrentUser;
import com.hyerijang.dailypay.budget.dto.BudgetDto;
import com.hyerijang.dailypay.budget.dto.CategoryDto;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest;
import com.hyerijang.dailypay.budget.dto.RecommendBudgetRequest;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.budget.service.BudgetService;
import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "budgets", description = "예산 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    @ExeTimer
    @Operation(summary = "카테고리 조회", description = "카테고리 조회")
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


    @ExeTimer
    @Operation(summary = "예산 설정 및 업데이트", description = "예산 설정 및 업데이트 (금액만 변경 가능)")
    @PostMapping
    ResponseEntity<Result> updateBudgets(@RequestBody @Validated CreateBudgetListRequest request,
        @CurrentUser User user) {
        List<BudgetDto> data = budgetService.update(request, user.getId());
        Result result = Result.builder().count(data.size()).data(data).build();
        return ResponseEntity.ok().body(result);
    }

    @ExeTimer
    @Operation(summary = "예산 추천", description = "예산 추천")
    @GetMapping
    ResponseEntity<Result> recommendBudgets(
        @RequestBody @Validated RecommendBudgetRequest request) {
        List<BudgetDto> data = budgetService.recommend(request);
        Result result = Result.builder().count(data.size()).data(data).build();
        return ResponseEntity.ok().body(result);
    }

}
