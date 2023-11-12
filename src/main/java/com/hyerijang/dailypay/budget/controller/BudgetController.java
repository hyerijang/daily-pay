package com.hyerijang.dailypay.budget.controller;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
