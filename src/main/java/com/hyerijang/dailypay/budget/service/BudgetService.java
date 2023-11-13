package com.hyerijang.dailypay.budget.service;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetDetail;
import com.hyerijang.dailypay.budget.dto.CategoryDto;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest;
import com.hyerijang.dailypay.budget.dto.PlanBudgetRequest;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        List<Budget> budgets = budgetRepository.saveAll(getBudgets(request, user));
        return BudgetDetail.getBudgetDetailList(budgets);
    }

    /***
     * DB 에서 예산 리스트를 가져온다, Budget 조회 과정에서 기존 예산 있다면 조회, 없다면 새 예산 생성
     */

    private List<Budget> getBudgets(CreateBudgetListRequest request, User user) {
        List<Budget> budgets = request.getData().stream()
            .map(d ->
                {
                    //기존 예산 있다면 조회, 없다면 새 예산 생성
                    Budget savedBudget = findExistUser(user, request.getYearMonth(), d.getCategory())
                        .orElse(createNewBudget(user, request.getYearMonth(), d.getCategory()));
                    //업데이트
                    savedBudget.updateBudgetAmount(d.getAmount());
                    return savedBudget;
                }
            )
            .collect(Collectors.toList());
        return budgets;
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

    public List<BudgetDetail> recommendBudget(PlanBudgetRequest request) {

        List<Object[]> result = budgetRepository.getUserBudgetTotalAmountByCategoryOrderBySumDesc();
        long sumBudgetAmount = result.stream().mapToLong(r -> (long) r[1]).sum();
        Map<Category, Integer> averageRatioByCategory = getAverageRatioByCategory(result,
            sumBudgetAmount);

        averageRatioByCategory.forEach(
            (category, rate) -> log.info("{} : {} ", category, rate)); //로그

        return BudgetDetail.generateBudgetDetails(request.userBudgetTotalAmount(),
            averageRatioByCategory);
    }

    private static Map<Category, Integer> getAverageRatioByCategory(List<Object[]> result,
        long sumBudgetAmount) {
        int miscellaneousRatio = 100; //기타 비율
        Map<Category, Integer> ratioByCategory = new HashMap<>();
        for (Object[] r : result) {
            int ratio = (int) (((Long) r[1] * 100.0) / sumBudgetAmount);
            Category category = (Category) r[0];
            // 비중이 10% 미만인 카테고리는 기타로 포함
            if (IsOtherRatios(ratio, category)) {
                continue;
            }
            //전체 중 10% 인 카테고리는 추천 리스트에 포함한다.
            miscellaneousRatio -= ratio;
            ratioByCategory.put(category, ratio);
        }
        ratioByCategory.put(Category.MISCELLANEOUS, miscellaneousRatio); //기타 비율도 추가
        return ratioByCategory;
    }

    private static boolean IsOtherRatios(int ratio, Category category) {
        return category == Category.MISCELLANEOUS || ratio < 10;
    }


}
