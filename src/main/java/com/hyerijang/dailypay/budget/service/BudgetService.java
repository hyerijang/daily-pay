package com.hyerijang.dailypay.budget.service;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetResponse;
import com.hyerijang.dailypay.budget.dto.CategoryResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public List<CategoryResponse> getCategories() {
        return Category.toList()
            .stream()
            .map(category -> new CategoryResponse(category.getCode(), category.getTitle()))
            .toList();
    }

    @Transactional
    public List<BudgetResponse> update(CreateBudgetListRequest request, Long userId) {
        List<Budget> budgets = budgetRepository.saveAll(getBudgets(request, userId));
        return BudgetResponse.getBudgetDetailList(budgets);
    }

    /**
     * DB 에서 {유저id, 년월, 카테고리}가 일치하는 예산들을 조회하고, 요청에 맞게 수정한다.
     * Budget 조회 과정에서 기존 예산이 없다면 새 예산 생성해서 리스트에 포함한다.
     */
    private List<Budget> getBudgets(CreateBudgetListRequest createBudgetListRequest, Long userId) {
        return createBudgetListRequest.getData().stream()
            .map(d ->
                {
                    //기존 예산 있다면 조회, 없다면 새 예산 생성
                    Budget budget = findExistUser(userId, createBudgetListRequest.getYearMonth(),
                        d.getCategory())
                        .orElse(createNewBudget(userId, createBudgetListRequest.getYearMonth(),
                            d.getCategory()));
                    //업데이트
                    budget.updateBudgetAmount(d.getAmount());
                    return budget;
                }
            )
            .toList();
    }

    private Optional<Budget> findExistUser(Long userId, YearMonth yearMonth, Category category) {
        return budgetRepository.findByUserIdAndYearMonthAndCategory(userId, yearMonth,
            category);
    }

    private Budget createNewBudget(Long userId, YearMonth yearMonth, Category category) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));
        return Budget.builder()
            .category(category)
            .yearMonth(yearMonth)
            .user(user)
            .build();
    }

    /***
     * 예산 추천 기능
     */
    public List<BudgetResponse> recommend(RecommendBudgetRequest request) {

        //1. 카테고리 별  평균 예산 비율 계산
        List<Object[]> result = budgetRepository.getUserBudgetTotalAmountByCategoryOrderBySumDesc();
        long sumBudgetAmount = result.stream().mapToLong(r -> (long) r[1]).sum();
        Map<Category, Integer> averageRatioByCategory = getAverageRatioByCategory(result,
            sumBudgetAmount);

        //로그
        averageRatioByCategory.forEach(
            (category, rate) -> log.debug("{} : {} ", category, rate));

        //2. 카테고리 별  평균 예산 비율 계산을 바탕으로 결과 생성
        return BudgetResponse.generateBudgetDetails(request.userBudgetTotalAmount(),
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
            if (isOtherRatios(ratio, category)) {
                continue;
            }
            //전체 중 10% 인 카테고리는 추천 리스트에 포함한다.
            miscellaneousRatio -= ratio;
            ratioByCategory.put(category, ratio);
        }
        ratioByCategory.put(Category.MISCELLANEOUS, miscellaneousRatio); //기타 비율도 추가
        return ratioByCategory;
    }

    private static boolean isOtherRatios(int ratio, Category category) {
        return category == Category.MISCELLANEOUS || ratio < 10;
    }


    /**
     * 해당 년월의 예산 총액 가져옴
     */
    public Long getTotalAmountOfBudgetIn(YearMonth yearMonth, Long userId) {
        return getBudgetListOfAllCategoryListIn(yearMonth, userId).stream()
            .mapToLong(Budget::getBudgetAmount)
            .sum();
    }

    /**
     * 유저의 해당 년월 예산 전부 반환
     */
    private List<Budget> getBudgetListOfAllCategoryListIn(YearMonth yearMonth, Long userId)
        throws ApiException {
        List<Budget> budgetList = budgetRepository.findByYearMonthAndUserId(yearMonth, userId);

        if (budgetList.isEmpty()) {
            throw new ApiException(ExceptionEnum.NO_BUDGET_IN_THE_MONTH);
        }
        return budgetList;
    }

    /**
     * 유저의 해당 년월 예산 을 DTO로 변환한 뒤 전부 반환
     */
    public List<BudgetResponse> getBudgetDtoListOfAllCategoryListIn(YearMonth yearMonth,
        Long userId) {
        return BudgetResponse.getBudgetDetailList(
            getBudgetListOfAllCategoryListIn(yearMonth, userId));
    }

    public List<BudgetResponse> recommend(Long finalTodayExpenseProposal) {
        return recommend(new RecommendBudgetRequest(finalTodayExpenseProposal));
    }
}
