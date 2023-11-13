package com.hyerijang.dailypay.budget.repository;

import com.hyerijang.dailypay.budget.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

}
