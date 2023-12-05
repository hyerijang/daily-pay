package com.hyerijang.dailypay.budget.service;


import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.CategoryDto;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("단위테스트 - BudgetService ")
@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BudgetService budgetService;


    @Test
    @DisplayName("카테고리 목록 조회시 모든 카테고리를 가져와야한다.")
    void getCategories() {
        List<CategoryDto> categories = budgetService.getCategories();
        // CategoryEnum과 동일한 길이의 리스트를 받아야한다.
        Assertions.assertThat(categories.size())
            .isEqualTo(Category.values().length);

        //모든 카테고리를 가져와야한다.
        Assertions.assertThat(categories.stream().map(x -> x.title()))
            .contains(Category.FOOD.getTitle())
            .contains(Category.HOUSING.getTitle())
            .contains(Category.TRANSPORTATION.getTitle())
            .contains(Category.MEDICAL.getTitle())
            .contains(Category.SAVING.getTitle())
            .contains(Category.UTILITIES.getTitle())
            .contains(Category.INSURANCE.getTitle())
            .contains(Category.MISCELLANEOUS.getTitle());
    }

    // TODO : 기능 개발 완료 이후 테스트 추가 작성
    @Test
    void update() {
    }

    @Test
    void recommend() {
    }
}