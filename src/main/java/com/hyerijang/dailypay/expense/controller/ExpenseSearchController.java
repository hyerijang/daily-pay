package com.hyerijang.dailypay.expense.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyerijang.dailypay.auth.CurrentUser;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.expense.controller.ExpenseController.Result;
import com.hyerijang.dailypay.expense.dto.ExpenseResponse;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import com.hyerijang.dailypay.expense.dto.GetAllExpenseParam;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import com.hyerijang.dailypay.user.domain.User;
import com.querydsl.core.Tuple;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "expenses", description = "지출 API")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ExpenseSearchController {

    private final ExpenseService expenseService;

    /**
     * v3으로 대체되었습니다. {@link #searchV3(GetAllExpenseParam, User, Pageable)}
     */
    @Deprecated
    @ExeTimer
    @Operation(summary = "유저의 지출 내역 (목록) 조회 V1", description = "본인의 지출 내역만 조회 가능")
    @GetMapping("/api/v1/expenses")
    public ResponseEntity<Result> searchV1(GetAllExpenseParam getAllExpenseParam,
        @CurrentUser User user) {

        ExpenseSearchCondition condition = ExpenseSearchCondition.of(getAllExpenseParam,
            user.getId()); //본인 지출내역만 조회 가능

        //1. 기간 별 지출 내역 조회
        List<ExpenseResponse> userAllExpenses = expenseService.getUserAllExpenses(
            getAllExpenseParam,
            user);

        //2. 지출 내역 토대로 지출 합계 계산 (excludeFromTotal이 true인 경우 제외)
        Long totalExpense = getTotalExpenseFrom(userAllExpenses);

        //3. 카테고리 별 지출 합계 (excludeFromTotal이 true인 경우 제외)
        Map<Category, BigDecimal> categoryWiseExpenseSum = getCategoryWiseSumMap(
            userAllExpenses);

        return ResponseEntity.ok()
            .body(Result.builder().data(userAllExpenses).count(userAllExpenses.size())
                .totalExpense(totalExpense)
                .CategoryWiseExpenseSum(categoryWiseExpenseSum).build());
    }


    /**
     * v3으로 대체되었습니다. {@link #searchV3(GetAllExpenseParam, User, Pageable)}
     */
    @Deprecated
    @ExeTimer
    @Operation(summary = "유저의 지출 내역 (목록) 조회 V2 (QueryDsl)", description = "본인의 지출 내역만 조회 가능")
    @GetMapping("/api/v2/expenses")
    public ResponseEntity<Result> searchV2(GetAllExpenseParam getAllExpenseParam,
        @CurrentUser User user) {
        ExpenseSearchCondition condition = ExpenseSearchCondition.of(getAllExpenseParam,
            user.getId()); //본인 지출내역만 조회 가능

        //1. 기간 별 지출 내역 조회 (QueryDsl)
        List<ExpenseResponse> userAllExpenses = expenseService.search(condition);

        //2. 지출 내역 토대로 지출 합계 계산 (excludeFromTotal이 true인 경우 제외)
        Long totalExpense = getTotalExpenseFrom(userAllExpenses);

        //3. 카테고리 별 지출 합계 (excludeFromTotal이 true인 경우 제외)
        Map<Category, BigDecimal> categoryWiseExpenseSum = getCategoryWiseSumMap(
            userAllExpenses);

        return ResponseEntity.ok()
            .body(Result.builder().data(userAllExpenses).count(userAllExpenses.size())
                .totalExpense(totalExpense)
                .CategoryWiseExpenseSum(categoryWiseExpenseSum).build());
    }

    /**
     * v3으로 대체 된 후,  더는 사용되지 않습니다.  {@link #searchV3(GetAllExpenseParam, User, Pageable)}
     */
    @Deprecated
    private static Map<Category, BigDecimal> getCategoryWiseSumMap(
        List<ExpenseResponse> userAllExpenses) {
        Map<Category, BigDecimal> categoryWiseExpenseSum = userAllExpenses.stream()
            .filter(exDto -> !exDto.excludeFromTotal())
            .collect(Collectors.groupingBy(ExpenseResponse::category,
                Collectors.reducing(BigDecimal.ZERO,
                    exDto -> BigDecimal.valueOf(exDto.amount()), BigDecimal::add)
            ));
        return categoryWiseExpenseSum;
    }

    /**
     * v3으로 대체 된 후,  더는 사용되지 않습니다. {@link #searchV3(GetAllExpenseParam, User, Pageable)}
     */
    @Deprecated
    private static Long getTotalExpenseFrom(List<ExpenseResponse> userAllExpenses) {
        Long totalExpense = userAllExpenses.stream()
            .filter(exDto -> !exDto.excludeFromTotal())
            .sorted(Comparator.comparing(ExpenseResponse::expenseDate)) //  expenseDate 순으로 정렬
            .mapToLong(exDto -> exDto.amount()).sum();
        return totalExpense;
    }


    @ExeTimer
    @Operation(summary = "유저의 지출 내역 (목록) 조회 V3 (QueryDsl + 페이징)", description = "본인의 지출 내역만 조회 가능")
    @GetMapping("/api/v3/expenses")
    public ResponseEntity<ResultV3> searchV3(GetAllExpenseParam getAllExpenseParam,
        @CurrentUser User user, Pageable pageable
    ) {

        ExpenseSearchCondition condition = ExpenseSearchCondition.of(getAllExpenseParam,
            user.getId()); //본인 지출내역만 조회 가능

        //1. 기간 별 지출 내역 조회 (QueryDsl, page)
        Page<ExpenseResponse> userExpensesPage = expenseService.searchPage(condition, pageable);

        //3. 카테고리 별 지출 합계 (excludeFromTotal이 true인 경우 제외)
        List<Tuple> categoryWiseExpenseSum = expenseService.getCategoryWiseExpenseSum(condition);

        //2. 지출 내역 토대로 지출 합계 계산
        Long totalExpense = categoryWiseExpenseSum.stream().mapToLong(tuple -> {
            return tuple.get(1, Long.class); // totalAmount
        }).sum();

        return ResponseEntity.ok()
            .body(ResultV3.builder()
                .userExpensesPage(userExpensesPage)
                .totalExpense(totalExpense)
                .CategoryWiseExpenseSum(categoryWiseExpenseSum)
                .build());
    }

    @Schema(description = "지출 응답 V3")
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class ResultV3<T> {

        @Schema(description = "총 지출액")
        private Long totalExpense;
        @Schema(description = "기간 별 지출 내역 조회 (Paging)")
        private T userExpensesPage; // 리스트의 값
        @Schema(description = "지출 목록 API의 경우 카테고리 별 지출 합계를 포함")
        private T CategoryWiseExpenseSum;
    }


}
