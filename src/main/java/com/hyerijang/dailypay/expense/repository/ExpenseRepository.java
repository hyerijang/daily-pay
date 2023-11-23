package com.hyerijang.dailypay.expense.repository;

import com.hyerijang.dailypay.expense.domain.Expense;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByExpenseDateBetweenAndUserIdAndDeletedIsFalse(LocalDateTime startDateTime,
        LocalDateTime endDateTime, Long userId);

    Optional<Expense> findByIdAndDeletedIsFalse(Long id);

    List<Expense> findByExpenseDateBetweenAndDeletedIsFalse(LocalDateTime startDateTime,
        LocalDateTime endDateTime);
}
