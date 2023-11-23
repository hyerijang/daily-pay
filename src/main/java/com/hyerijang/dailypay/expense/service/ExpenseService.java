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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ExpenseDto createExpense(CreateExpenseRequest createExpenseRequest, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));
        Expense savedExpense = expenseRepository.save(createExpenseRequest.toEntity(user));
        return ExpenseDto.of(savedExpense);
    }


    /**
     * 유저의 지출 내역 (목록) 조회
     */
    public List<ExpenseDto> getUserAllExpenses(GetAllExpenseParam request, User user) {
        List<Expense> result = findExpenseWithCondition(request, user.getId());
        return ExpenseDto.getExpenseDtoList(result);
    }

    private List<Expense> findExpenseWithCondition(GetAllExpenseParam request, Long userId) {
        //조건 = {유저의 Expense ,기간 (start ~ end) , 삭제되지 않은 Expense}
        //기간은 시작일의 0시 0분 0초 ~ 종료일의 23시 59분 59초
        return expenseRepository.findByExpenseDateBetweenAndUserIdAndDeletedIsFalse(
            request.start().atStartOfDay(), request.end().atTime(23, 59, 59), userId);
    }

    /**
     * 유저의 지출 내역 (단건) 조회 , 삭제된 Expense는 제외
     */
    public ExpenseDto getExpenseById(Long id, Long userId) {
        Expense found = expenseRepository.findByIdAndDeletedIsFalse(id) // 삭제된 Expense 제외
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_EXPENSE));

        validateFound(userId, found);

        return ExpenseDto.of(found);
    }


    Boolean isNotExpenseWriter(Long userId, Long writerOfExpense) {
        return (userId != writerOfExpense);
    }

    /**
     * 유저의 지출 내역(단건) 수정
     */
    @Transactional
    public ExpenseDto updateExpense(Long id, UpdateExpenseRequest request, Long userId) {

        Expense found = expenseRepository.findById(id)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_EXPENSE));

        validateFound(userId, found);

        //updateExpenseRequest의 내용으로 Expense found를 업데이트
        request.updateFoundWithRequest(found);
        return ExpenseDto.of(found);
    }

    /**
     * 유저의 지출 내역(단건) 삭제
     */
    @Transactional
    public ExpenseDto deleteExpense(Long id, Long userId) {

        Expense found = expenseRepository.findById(id) // 삭제된 Expense 제외
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_EXPENSE));

        validateFound(userId, found);

        found.delete();

        return ExpenseDto.of(found);

    }

    /**
     * 유저의 지출 내역(단건)을 합계에서 제외
     */
    @Transactional
    public ExpenseDto excludeFromTotal(Long id, Long userId) {
        Expense found = expenseRepository.findById(id) // 삭제된 Expense 제외
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_EXPENSE));
        validateFound(userId, found);

        found.excludeFromTotal();

        return ExpenseDto.of(found);

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
    private List<Expense> getAllUserExpensesIn(YearMonth yearMonth, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));
        return expenseRepository.findByExpenseDateBetweenAndUserIdAndDeletedIsFalse(
            yearMonth.atDay(1).atStartOfDay(), yearMonth.atEndOfMonth().atTime(23, 59, 59), userId);

    }

    public List<ExpenseDto> getAllUserExpenseDtoListIn(YearMonth yearMonth, Long userId) {
        List<Expense> expenses = getAllUserExpensesIn(yearMonth, userId);
        return ExpenseDto.getExpenseDtoList(expenses);
    }

    /**
     * 유저의 오늘 전체 지출
     */
    public List<ExpenseDto> getAllUserExpenseDtoListIn(LocalDate localDate, Long userId) {
        List<Expense> allUserExpensesIn = expenseRepository.findByExpenseDateBetweenAndUserIdAndDeletedIsFalse(
            localDate.atTime(0, 0, 0), localDate.atTime(23, 59, 59), userId);

        //Dto로 변환
        return ExpenseDto.getExpenseDtoList(allUserExpensesIn);
    }


    public List<ExpenseDto> getAllUserExpenseDtoListIn(LocalDateTime start, LocalDateTime end,
        Long userId) {
        List<Expense> allUserExpensesIn = expenseRepository.findByExpenseDateBetweenAndUserIdAndDeletedIsFalse(
            start, end, userId);
        //Dto로 변환
        return ExpenseDto.getExpenseDtoList(allUserExpensesIn);
    }

    public Long getAverageExpenseAmountOfToday() {

        // FIXME : 오늘 일어났던 모든 소비를 DB에서 가져와서 stream으로 읽기 때문에 매우 비효율 적임. QueryDsl 적용 이후 수정요망.

        //오늘 전체 유저들의 소비액 총합
        List<Expense> allExpenseOfToday = expenseRepository.findByExpenseDateBetweenAndDeletedIsFalse(
            LocalDateTime.now().withHour(0).withMinute(0).withSecond(0), // 오늘 0시 0분 0초부터
            LocalDateTime.now());//현재 시각 까지
        long sum = allExpenseOfToday.stream().mapToLong(x -> x.getAmount()).sum(); //오늘 전체 유저의 지출 총액
        long numOfUserInToday = allExpenseOfToday.stream().map(Expense::getUser).distinct().toList()
            .size(); //오늘 지출한 유저의 수

        return sum / numOfUserInToday;
    }
}
