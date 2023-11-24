package com.hyerijang.dailypay.expense.repository;

import static com.hyerijang.dailypay.expense.domain.QExpense.expense;

import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                isNotDeleted()
            )
            .fetch();
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? expense.category.eq(category) : null;
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

    private static BooleanExpression isNotDeleted() {
        return expense.deleted.eq(false);
    }

//    public List<ExpenseDto> search(ExpenseSearchCondition condition) {
//        return queryFactory.select(
//                Projections.constructor(ExpenseDto.class, expense.id, expense.user.id, expense.category,
//                    expense.amount, expense.memo, expense.excludeFromTotal, expense.expenseDate))
//            .from(expense)
//            .fetch();
//    }
}
