package com.hyerijang.dailypay.expense.repository;

import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.expense.dto.ExpenseSearchCondition;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, ExpenseRepositoryCustom {

    /**
     * @deprecated
     * {@link ExpenseRepositoryCustom#search(ExpenseSearchCondition)}} 사용을 권장합니다.
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
    List<Expense> findByExpenseDateBetweenAndUserIdAndDeletedIsFalse(LocalDateTime startDateTime,
        LocalDateTime endDateTime, Long userId);

    Optional<Expense> findByIdAndDeletedIsFalse(Long id);

    List<Expense> findByExpenseDateBetweenAndDeletedIsFalse(LocalDateTime startDateTime,
        LocalDateTime endDateTime);
}
