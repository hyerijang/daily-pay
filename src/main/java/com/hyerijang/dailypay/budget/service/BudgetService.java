package com.hyerijang.dailypay.budget.service;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetDto;
import com.hyerijang.dailypay.budget.dto.CategoryDto;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest;
import com.hyerijang.dailypay.budget.dto.RecommendBudgetRequest;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.time.YearMonth;
import java.util.LinkedHashMap;
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
    public List<BudgetDto> update(CreateBudgetListRequest request,
        Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
            () -> new ApiException(ExceptionEnum.NOT_EXIST_USER)
        );

        List<Budget> budgets = budgetRepository.saveAll(getBudgets(request, user));
        return BudgetDto.getBudgetDetailList(budgets);
    }

    /***
     * DB 에서 {유저id, 년월, 카테고리}가 일치하는 예산들을 조회한다. Budget 조회 과정에서 기존 예산이 없다면 새 예산 생성해서 리스트에 포함한다.
     */
    private List<Budget> getBudgets(CreateBudgetListRequest request, User user) {
        List<Budget> budgets = request.getData().stream()
            .map(d ->
                {
                    //기존 예산 있다면 조회, 없다면 새 예산 생성
                    Budget budget = findExistUser(user, request.getYearMonth(), d.getCategory())
                        .orElse(createNewBudget(user, request.getYearMonth(), d.getCategory()));
                    //업데이트
                    budget.updateBudgetAmount(d.getAmount());
                    return budget;
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

    /***
     * 예산 추천 기능
     */
    public List<BudgetDto> recommend(RecommendBudgetRequest request) {

        //1. 카테고리 별  평균 예산 비율 계산
        List<Object[]> result = budgetRepository.getUserBudgetTotalAmountByCategoryOrderBySumDesc();
        long sumBudgetAmount = result.stream().mapToLong(r -> (long) r[1]).sum();
        Map<Category, Integer> averageRatioByCategory = getAverageRatioByCategory(result,
            sumBudgetAmount);

        //로그
        averageRatioByCategory.forEach(
            (category, rate) -> log.debug("{} : {} ", category, rate));

        //2. 카테고리 별  평균 예산 비율 계산을 바탕으로 결과 생성
        return BudgetDto.generateBudgetDetails(request.userBudgetTotalAmount(),
            averageRatioByCategory);
    }

    /***
     * 카테고리 별  평균 예산 비율 계산
     */
    private static Map<Category, Integer> getAverageRatioByCategory(List<Object[]> result,
        long sumBudgetAmount) {
        int miscellaneousRatio = 100; //기타 비율
        Map<Category, Integer> ratioByCategory = new LinkedHashMap<>();
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


    /**
     * 해당 년월의 예산 총액 가져옴
     *
     * @param yearMonth
     * @param userId
     * @return
     */
    public Long getTotalAmountOfBudgetIn(YearMonth yearMonth, Long userId) {
        return getBudgetListOfAllCategoryListIn(yearMonth, userId).stream()
            .mapToLong(x -> x.getBudgetAmount())
            .sum();
    }

    public List<Budget> getBudgetListOfAllCategoryListIn(YearMonth this_month, Long userId) {
        List<Budget> budgetList = budgetRepository.findByYearMonthAndUserId(this_month, userId);

        if (budgetList.size() == 0) {
            throw new ApiException(ExceptionEnum.NO_BUDGET_IN_THE_MONTH);
        }
        return budgetList;
    }

    public List<BudgetDto> recommend(Long finalTodayExpenseProposal) {
        return recommend(new RecommendBudgetRequest(finalTodayExpenseProposal));
    }
}
