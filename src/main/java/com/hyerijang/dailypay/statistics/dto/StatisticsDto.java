package com.hyerijang.dailypay.statistics.dto;

import com.hyerijang.dailypay.budget.domain.Category;
import java.util.Map;

public record StatisticsDto(Long totalExpenseComparison,
                            Map<Category, Long> categoryExpenseComparison) {

}
