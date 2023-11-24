package com.hyerijang.dailypay.expense.repository;

import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import java.util.List;

public interface ExpenseRepositoryCustom {

    List<ExpenseDto> search(ExpenseSearchCondition condition);

}
