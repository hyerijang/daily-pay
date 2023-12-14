package com.hyerijang.dailypay.budget.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudgetResponse(Long id,
                             @JsonDeserialize(using = YearMonthDeserializer.class)
                             @Schema(description = "예산 년월")
                             YearMonth yearMonth,
                             @Schema(description = "카테고리")
                             Category category,
                             @Schema(description = "카테고리 별 예산액")
                             Long amount) {

    public static List<BudgetResponse> getBudgetDetailList(List<Budget> budgets) {
        return budgets.stream().map(
                x -> new BudgetResponse(x.getId(), x.getYearMonth(), x.getCategory(),
                    x.getBudgetAmount()))
            .toList();
    }

    /***
     * 유저의 남은 예산과 averageCategoryRate로 유저에게 적합한 예산액을 설정해줍니다.
     * @return 카테고리별 추천 예산
     */
    public static List<BudgetResponse> generateBudgetDetails(Long leftAmountOfUser,
        Map<Category, Integer> averageCategoryRate) {
        List<BudgetResponse> budgetResponseList = new ArrayList<>();
        averageCategoryRate.forEach(
            (category, ratio) -> {
                BudgetResponse budgetResponse = createBudgetDetail(leftAmountOfUser, category,
                    ratio);
                budgetResponseList.add(budgetResponse);
            }
        );
        return budgetResponseList;
    }


    private static BudgetResponse createBudgetDetail(
        Long leftAmountOfUser,
        Category category,
        Integer ratio) {
        return BudgetResponse
            .builder()
            .amount(leftAmountOfUser * ratio / 100)
            .category(category)
            .yearMonth(YearMonth.now()) //이번달 예산
            .build();

    }
}
