package com.hyerijang.dailypay.budget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyerijang.dailypay.budget.domain.Category;
import java.time.YearMonth;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateBudgetListRequest {

    private List<CreateBudgetDetail> data;
    @JsonFormat(pattern = "yyyy-MM") //형식 : yyyy-MM
    private YearMonth yearMonth;

    @Getter
    public static class CreateBudgetDetail {

        private Category category;
        private Long amount;
    }

}
