package com.hyerijang.dailypay.expense.repository;

import com.hyerijang.dailypay.expense.dto.ExpenseResponse;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import com.querydsl.core.Tuple;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseRepositoryCustom {

    List<ExpenseResponse> search(ExpenseSearchCondition condition);

    Page<ExpenseResponse> searchPage(ExpenseSearchCondition condition, Pageable pageable);

    Long getTotalExpenseAmount(ExpenseSearchCondition condition);

    List<Tuple> getCategoryWiseExpenseSum(ExpenseSearchCondition condition);
}
