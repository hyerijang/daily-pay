package com.hyerijang.dailypay.expense.dto;

import java.time.LocalDate;

public record GetAllExpenseRequest(LocalDate start, LocalDate end) {

}
