package com.hyerijang.dailypay.expense.service;

import com.hyerijang.dailypay.expense.dto.CreateExpenseRequest;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.UpdateExpenseRequest;
import com.hyerijang.dailypay.expense.repository.ExpenseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    // TODO : 지출 서비스 구현

    private final ExpenseRepository expenseRepository;

    public List<ExpenseDto> getUserAllExpenses() {
        return null;
    }

    public ExpenseDto getExpenseById(Long id) {
        return null;

    }

    public ExpenseDto createExpense(CreateExpenseRequest createExpenseRequest) {
        return null;

    }

    public ExpenseDto updateExpense(Long id, UpdateExpenseRequest updateExpenseRequest) {
        return null;

    }
}
