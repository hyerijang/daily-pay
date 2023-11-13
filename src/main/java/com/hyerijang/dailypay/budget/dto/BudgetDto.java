package com.hyerijang.dailypay.budget.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudgetDto(Long id,
                        @JsonDeserialize(using = YearMonthDeserializer.class)
                        YearMonth yearMonth,
                        Category category,
                        Long amount) {

    public static List<BudgetDto> getBudgetDetailList(List<Budget> budgets) {
        return budgets.stream().map(
                x -> new BudgetDto(x.getId(), x.getYearMonth(), x.getCategory(),
                    x.getBudgetAmount()))
            .toList();
    }

    /***
     * 유저의 남은 예산과 averageCategoryRate로 유저에게 적합한 예산액을 설정해줍니다.
     * @return 카테고리별 추천 예산
     */
    public static List<BudgetDto> generateBudgetDetails(Long leftAmountOfUser,
        Map<Category, Integer> averageCategoryRate) {
        List<BudgetDto> budgetDtoList = new ArrayList<>();
        averageCategoryRate.forEach(
            (category, ratio) -> {
                BudgetDto budgetDto = createBudgetDetail(leftAmountOfUser, category, ratio);
                budgetDtoList.add(budgetDto);
            }
        );
        return budgetDtoList;
    }

    private static BudgetDto createBudgetDetail(Long leftAmountOfUser, Category category,
        Integer ratio) {
        return BudgetDto
            .builder()
            .amount(leftAmountOfUser * ratio / 100)
            .category(category)
            .yearMonth(YearMonth.now()) //이번달 예산
            .build();

    }
}
