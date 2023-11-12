package com.hyerijang.dailypay.budget.service;

import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.CategoryDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class BudgetService {

    public List<CategoryDto> getCategories() {
        return Category.toList()
            .stream()
            .map((c) -> new CategoryDto(c.getCode(), c.getTitle()))
            .toList();
    }

}
