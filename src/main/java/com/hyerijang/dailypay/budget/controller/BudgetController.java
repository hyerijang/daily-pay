package com.hyerijang.dailypay.budget.controller;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.CategoryDto;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.budget.service.BudgetService;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    @PostMapping
    void test() {
        budgetRepository.save(
            Budget.builder().budgetAmount(10000L).category(Category.SAVING).build());
    }

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

}
