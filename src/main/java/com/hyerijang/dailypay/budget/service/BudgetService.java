package com.hyerijang.dailypay.budget.service;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetDetail;
import com.hyerijang.dailypay.budget.dto.CategoryDto;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public List<CategoryDto> getCategories() {
        return Category.toList()
            .stream()
            .map((c) -> new CategoryDto(c.getCode(), c.getTitle()))
            .toList();
    }


    @Transactional
    public List<BudgetDetail> createAll(CreateBudgetListRequest request,
        Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
            () -> new ApiException(ExceptionEnum.NOT_EXIST_USER)
        );
        //조회 및 업데이트
        List<Budget> budgets = request.getData().stream()
            .map(d ->
                {
                    Budget savedBudget = findExistUser(user, request.getYearMonth(), d.getCategory())
                        .orElse(createNewBudget(user, request.getYearMonth(), d.getCategory()));
                    //업데이트
                    savedBudget.updateBudgetAmount(d.getAmount());
                    return savedBudget;
                }
            )
            .collect(Collectors.toList());

        budgetRepository.saveAll(budgets);
        return BudgetDetail.getBudgetDetailList(budgets);
    }

    private Optional<Budget> findExistUser(User user, YearMonth yearMonth, Category category) {
        return budgetRepository.findByUserIdAndYearMonthAndCategory(user.getId(), yearMonth,
            category);
    }

    private static Budget createNewBudget(User user, YearMonth yearMonth, Category category) {
        return Budget.builder()
            .category(category)
            .yearMonth(yearMonth)
            .user(user)
            .build();
    }
}
