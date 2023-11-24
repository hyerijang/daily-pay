package com.hyerijang.dailypay.expense.repository;

import static com.hyerijang.dailypay.expense.domain.QExpense.expense;

import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.domain.QExpense;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ExpenseDto> search(ExpenseSearchCondition condition) {
        return queryFactory.select(
                Projections.constructor(ExpenseDto.class, expense.id, expense.user.id, expense.category,
                    expense.amount, expense.memo, expense.excludeFromTotal, expense.expenseDate))
            .from(expense)
            .where(
                userIdEq(condition.userId()),
                startAfter(condition.start()),
                endBefore(condition.end()),
                categoryEq(condition.category()),
                minAmountGoe(condition.minAmount()),
                maxAmountLoe(condition.maxAmount()),
                isNotDeleted()
            )
            .fetch();
    }

    @Override
    public Page<ExpenseDto> searchPage(ExpenseSearchCondition condition, Pageable pageable) {
        List<ExpenseDto> content = getExpenseList(condition, pageable);
        JPAQuery<Long> countQuery = getCount(condition);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Long getTotalExpenseAmount(ExpenseSearchCondition condition) {
        return getSumOfAmount(condition).fetchOne();
    }

    @Override
    public List<Tuple> getCategoryWiseExpenseSum(ExpenseSearchCondition condition) {
        return getCategorySumGroupByCategory(condition);
    }


    private List<ExpenseDto> getExpenseList(ExpenseSearchCondition condition, Pageable pageable) {
        JPAQuery<ExpenseDto> query = queryFactory.select(
                Projections.constructor(ExpenseDto.class, expense.id, expense.user.id, expense.category,
                    expense.amount, expense.memo, expense.excludeFromTotal, expense.expenseDate))
            .from(expense)
            .where(
                userIdEq(condition.userId()),
                startAfter(condition.start()),
                endBefore(condition.end()),
                categoryEq(condition.category()),
                minAmountGoe(condition.minAmount()),
                maxAmountLoe(condition.maxAmount()),
                isNotDeleted())
            .offset(pageable.getOffset())   // (2) 페이지 번호
            .limit(pageable.getPageSize())
            .fetch();
    }

    private JPAQuery<Long> getCount(ExpenseSearchCondition condition) {
        return queryFactory.select(expense.count())
            .from(expense)
            .where(
                userIdEq(condition.userId()),
                startAfter(condition.start()),
                endBefore(condition.end()),
                categoryEq(condition.category()),
                minAmountGoe(condition.minAmount()),
                maxAmountLoe(condition.maxAmount()),
                isNotDeleted());
    }

    private JPAQuery<Long> getSumOfAmount(ExpenseSearchCondition condition) {
        return queryFactory.select(expense.amount.sum())
            .from(expense)
            .where(
                userIdEq(condition.userId()),
                startAfter(condition.start()),
                endBefore(condition.end()),
                categoryEq(condition.category()),
                minAmountGoe(condition.minAmount()),
                maxAmountLoe(condition.maxAmount()),
                isNotDeleted(),
                notExcludeFromTotal());
    }

    private List<Tuple> getCategorySumGroupByCategory(ExpenseSearchCondition condition) {
        return queryFactory.select(expense.category, expense.amount.sum())
            .from(expense)
            .where(
                userIdEq(condition.userId()),
                startAfter(condition.start()),
                endBefore(condition.end()),
                categoryEq(condition.category()),
                minAmountGoe(condition.minAmount()),
                maxAmountLoe(condition.maxAmount()),
                isNotDeleted(),
                notExcludeFromTotal())
            .groupBy(expense.category)
            .fetch();
    }

    private static BooleanExpression userIdEq(Long userId) {
        return userId != null ? expense.user.id.eq(userId) : null;
    }

    private static BooleanExpression startAfter(LocalDateTime start) {
        return start != null ? expense.expenseDate.after(start) : null;
    }

    private static BooleanExpression endBefore(LocalDateTime end) {
        return end != null ? expense.expenseDate.before(end) : null;
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? expense.category.eq(category) : null;
    }

    private BooleanExpression minAmountGoe(Long minAmount) {
        // expense.amount >= minAmount
        return minAmount != null ? expense.amount.goe(minAmount) : null;
    }

    private BooleanExpression maxAmountLoe(Long maxAmount) {
        // expense.amount >= maxAmount
        return maxAmount != null ? expense.amount.loe(maxAmount) : null;
    }

    private static BooleanExpression isNotDeleted() {
        return expense.deleted.eq(false);
    }

    private BooleanExpression notExcludeFromTotal() {
        //제외되지 않았으면 true리턴
        return expense.excludeFromTotal.eq(false);
    }


}
