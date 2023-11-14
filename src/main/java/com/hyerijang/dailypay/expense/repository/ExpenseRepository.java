package com.hyerijang.dailypay.expense.repository;

import com.hyerijang.dailypay.expense.domain.Expense;
import com.hyerijang.dailypay.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByExpenseDateBetweenAndUserAndDeletedIsFalse(LocalDateTime startDateTime,
        LocalDateTime endDateTime, User user);
}
