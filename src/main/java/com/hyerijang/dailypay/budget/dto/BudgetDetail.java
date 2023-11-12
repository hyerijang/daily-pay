package com.hyerijang.dailypay.budget.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudgetDetail(Long id,
                           YearMonth yearMonth,
                           Category category,
                           Long amount) {

    public static List<BudgetDetail> getBudgetDetailList(List<Budget> budgets) {
        return budgets.stream().map(
                x -> new BudgetDetail(x.getId(), x.getYearMonth(), x.getCategory(),
                    x.getBudgetAmount()))
            .toList();
    }

    /***
     * 유저의 남은 예산과 averageCategoryRate로 유저에게 적합한 예산액을 설정해줍니다.
     * @return 카테고리별 추천 예산
     */
    public static List<BudgetDetail> generateBudgetDetails(Long leftAmountOfUser,
        Map<Category, Integer> averageCategoryRate) {
        List<BudgetDetail> budgetDetailList = new ArrayList<>();
        averageCategoryRate.forEach(
            (category, ratio) -> {
                BudgetDetail budgetDetail = createBudgetDetail(leftAmountOfUser, category, ratio);
                budgetDetailList.add(budgetDetail);
            }
        );
        return budgetDetailList;
    }

    private static BudgetDetail createBudgetDetail(Long leftAmountOfUser, Category category,
        Integer ratio) {
        return BudgetDetail
            .builder()
            .amount(leftAmountOfUser * ratio / 100)
            .category(category)
            .yearMonth(YearMonth.now()) //이번달 예산
            .build();

    }
}
