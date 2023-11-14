package com.hyerijang.dailypay.expense.service;

import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.expense.dto.CreateExpenseRequest;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.GetAllExpenseParam;
import com.hyerijang.dailypay.expense.dto.UpdateExpenseRequest;
import com.hyerijang.dailypay.expense.repository.ExpenseRepository;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {
    // TODO : 지출 서비스 구현

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    /**
     * 새 지출 내역 (단건) 생성
     */
    public ExpenseDto createExpense(CreateExpenseRequest createExpenseRequest,
        Authentication authentication) {

        User user = findUserByEmail(authentication);
        Expense savedExpense = expenseRepository.save(createExpenseRequest.toEntity(user));

        return ExpenseDto.of(savedExpense);
    }

    private User findUserByEmail(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(
                ExceptionEnum.NOT_EXIST_USER));
        return user;
    }


    /**
     * 유저의 지출 내역 (목록) 조회
     */
    public List<ExpenseDto> getUserAllExpenses(GetAllExpenseParam request,
        Authentication authentication) {
        User user = findUserByEmail(authentication);
        List<Expense> result = findExpenseWithCondition(request, user);
        log.info("result = {}", result.size());
        return ExpenseDto.getExpenseDtoList(result);
    }

    private List<Expense> findExpenseWithCondition(GetAllExpenseParam request, User user) {
        //조건 = {유저의 Expense ,기간 (start ~ end) , 삭제되지 않은 Expense}
        //기간은 시작일의 0시 0분 0초 ~ 종료일의 23시 59분 59초
        return expenseRepository.findByExpenseDateBetweenAndUserAndDeletedIsFalse(
            request.start().atStartOfDay(), request.end().atTime(23, 59, 59),

            user);
    }

    /**
     * 유저의 지출 내역 (단건) 조회
     */
    public ExpenseDto getExpenseById(Long id) {
        return null;

    }

    /**
     * 유저의 지출 내역(단건) 수정
     */
    public ExpenseDto updateExpense(Long id, UpdateExpenseRequest updateExpenseRequest) {
        return null;

    }
}
