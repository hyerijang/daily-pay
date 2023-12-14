package com.hyerijang.dailypay.expense.service;

import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.expense.dto.CreateExpenseRequest;
import com.hyerijang.dailypay.expense.dto.ExpenseResponse;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import com.hyerijang.dailypay.expense.dto.GetAllExpenseParam;
import com.hyerijang.dailypay.expense.dto.UpdateExpenseRequest;
import com.hyerijang.dailypay.expense.repository.ExpenseRepository;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import com.querydsl.core.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    /**
     * 새 지출 내역 (단건) 생성
     */
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest createExpenseRequest, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));
        Expense savedExpense = expenseRepository.save(createExpenseRequest.toEntity(user));
        return ExpenseResponse.of(savedExpense);
    }


    /**
     * @see : com.hyerijang.dailypay.expense.service.search
     * @deprecated 유저의 지출 내역 (목록) 조회 V1
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
    public List<ExpenseResponse> getUserAllExpenses(GetAllExpenseParam request, User user) {
        List<Expense> result = findExpenseWithCondition(request, user.getId());
        return ExpenseResponse.getExpenseDtoList(result);
    }

    /**
     * @deprecated 유저의 지출 내역 (목록) 조회
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
    private List<Expense> findExpenseWithCondition(GetAllExpenseParam request, Long userId) {
        //기간은 시작일의 0시 0분 0초 ~ 종료일의 23시 59분 59초
        return expenseRepository.findByExpenseDateBetweenAndUserIdAndDeletedIsFalse(
            request.start().atStartOfDay(), request.end().atTime(23, 59, 59), userId);
    }


    /**
     * 유저의 지출 내역 (단건) 조회 , 삭제된 Expense는 제외
     */
    public ExpenseResponse getExpenseById(Long id, Long userId) {
        Expense found = expenseRepository.findByIdAndDeletedIsFalse(id) // 삭제된 Expense 제외
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_EXPENSE));

        validateFound(userId, found);

        return ExpenseResponse.of(found);
    }


    Boolean isNotExpenseWriter(Long userId, Long writerOfExpense) {
        return userId.equals(writerOfExpense);
    }

    /**
     * 유저의 지출 내역(단건) 수정
     */
    @Transactional
    public ExpenseResponse updateExpense(Long id, UpdateExpenseRequest request, Long userId) {

        Expense found = expenseRepository.findById(id)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_EXPENSE));

        validateFound(userId, found);

        //updateExpenseRequest의 내용으로 Expense found를 업데이트
        request.updateFoundWithRequest(found);
        return ExpenseResponse.of(found);
    }

    /**
     * 유저의 지출 내역(단건) 삭제
     */
    @Transactional
    public ExpenseResponse deleteExpense(Long id, Long userId) {

        Expense found = expenseRepository.findById(id) // 삭제된 Expense 제외
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_EXPENSE));

        validateFound(userId, found);

        found.delete();

        return ExpenseResponse.of(found);

    }

    /**
     * 유저의 지출 내역(단건)을 합계에서 제외
     */
    @Transactional
    public ExpenseResponse excludeFromTotal(Long id, Long userId) {
        Expense found = expenseRepository.findById(id) // 삭제된 Expense 제외
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_EXPENSE));
        validateFound(userId, found);

        found.excludeFromTotal();

        return ExpenseResponse.of(found);

    }

    /**
     * user가 crud 가능한 found인지 검증
     */
    private void validateFound(Long userId, Expense found) {
        //작성자인지 체크
        if (isNotExpenseWriter(userId, found.getUser().getId())) {
            throw new ApiException(ExceptionEnum.NOT_WRITER_OF_EXPENSE);
        }

        //삭제 유무 체크
        if (found.getDeleted()) {
            throw new ApiException(ExceptionEnum.ALREADY_DELETED_EXPENSE);
        }
    }

    /**
     * 유저의 특정 년월 전체 지출
     */
    public List<ExpenseResponse> getAllExpenseListBetween(YearMonth yearMonth, Long userId) {
        ExpenseSearchCondition expenseSearchCondition = ExpenseSearchCondition.builder()
            .start(yearMonth.atDay(1).atStartOfDay()) // 1일 0시 0분 0초
            .end(yearMonth.atEndOfMonth().atTime(23, 59, 59)) // 31일 23시 59분 59초
            .userId(userId)
            .build();
        return expenseRepository.search(expenseSearchCondition);
    }

    /**
     * 특정 일의 전체 지출
     */
    public List<ExpenseResponse> getAllExpenseListBetween(LocalDate localDate, Long userId) {
        ExpenseSearchCondition expenseSearchCondition = ExpenseSearchCondition.builder()
            .userId(userId)
            .start(localDate.atStartOfDay()) // localDate일 0시 0분 0초
            .end(localDate.atTime(23, 59, 59)) // localDate일 23시 59분 59초
            .build();
        
        return expenseRepository.search(expenseSearchCondition);
    }


    /**
     * 특정 기간 내의 모든 소비 내역 조회 (excludeFromTotal이 true인 경우 제외)
     */
    public List<ExpenseResponse> getAllExpenseListBetween(LocalDateTime start, LocalDateTime end,
        Long userId) {
        return search(
            ExpenseSearchCondition.builder()
                .start(start)
                .end(end)
                .userId(userId)
                .exclusion(true) // (excludeFromTotal이 true인 경우 제외)
                .build());
    }

    public Long getAverageExpenseAmountOfToday() {
        //오늘 전체 유저들의 소비액 총합
        Tuple tuple = expenseRepository.getTotalExpenseAmountOfAllUser(LocalDate.now());

        Long sum = tuple.get(0,Long.class);
        Long numOfUserInToday = tuple.get(1,Long.class);
        log.debug("전체 유저 소비 총합 = {}", sum);
        log.debug("오늘 소비를 기록한 유저 수 = {}",numOfUserInToday);
        return sum / numOfUserInToday;
    }

    //== QueryDsl==//

    /**
     * 유저의 지출 내역 (목록) 조회 v2 (QueryDSL)
     */
    public List<ExpenseResponse> search(ExpenseSearchCondition condition) {
        return expenseRepository.search(condition);
    }

    /**
     * 유저의 지출 내역 (목록) 조회 v3 (QueryDSL + Paging)
     */
    public Page<ExpenseResponse> searchPage(ExpenseSearchCondition condition, Pageable pageable) {
        return expenseRepository.searchPage(condition, pageable);
    }

    /**
     * 지출 내역 토대로 지출 합계 계산 (excludeFromTotal이 true인 경우 제외)
     */
    public Long getTotalExpenseAmount(YearMonth yearMonth, Long userId) {
        return expenseRepository.getTotalExpenseAmount(ExpenseSearchCondition.builder()
            .start(yearMonth.atDay(1).atStartOfDay())
            .end(yearMonth.atEndOfMonth().atTime(23, 59, 59))
            .userId(userId)
            .build());
    }

    /**
     * 카테고리 별 지출 합계 (excludeFromTotal이 true인 경우 제외)
     */
    public List<Tuple> getCategoryWiseExpenseSum(ExpenseSearchCondition condition) {
        return expenseRepository.getCategoryWiseExpenseSum(condition);
    }
}
