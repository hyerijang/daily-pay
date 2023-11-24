package com.hyerijang.dailypay.expense.repository;

import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import com.querydsl.core.Tuple;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseRepositoryCustom {

    List<ExpenseDto> search(ExpenseSearchCondition condition);

    Page<ExpenseDto> searchPage(ExpenseSearchCondition condition, Pageable pageable);

    Long getTotalExpenseAmount(ExpenseSearchCondition condition);

    List<Tuple> getCategoryWiseExpenseSum(ExpenseSearchCondition condition);
}
