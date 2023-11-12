package com.hyerijang.dailypay.budget.dto;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CreateBudgetListRequest {

    private List<CreateBudgetDetail> data;
    private String yyyyMM;

    @Getter
    private static class CreateBudgetDetail {

        private Category category;
        private Long amount;
    }

    public List<Budget> toEntityList(User user) {
        List<Budget> list = new ArrayList();
        return data
            .stream()
            .map(d -> Budget.builder()
                .category(d.category)
                .budgetAmount(d.amount)
                .yyyyMM(this.yyyyMM)
                .user(user)
                .build())
            .collect(Collectors.toList());

    }
}
