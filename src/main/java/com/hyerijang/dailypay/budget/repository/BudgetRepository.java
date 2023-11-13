package com.hyerijang.dailypay.budget.repository;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndYearMonthAndCategory(Long id, YearMonth yearMonth,
        Category category);

    @Query("SELECT b.category, SUM(b.budgetAmount) FROM Budget b GROUP BY b.category ORDER BY SUM(b.budgetAmount) DESC")
    List<Object[]> getUserBudgetTotalAmountByCategoryOrderBySumDesc();

}
