package com.hyerijang.dailypay.budget.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetResponse;
import com.hyerijang.dailypay.budget.dto.CategoryResponse;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest.CreateBudgetDetail;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.budget.service.BudgetService;
import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@DisplayName("단위테스트 - BudgetController")
@WebMvcTest(controllers = BudgetController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // === DI === //
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BudgetService budgetService;

    @MockBean
    private BudgetRepository budgetRepository;

    @MockBean
    private JwtAuthenticationFilter filter;



    // === Request 초기화 === //

    User user;
    CreateBudgetListRequest createBudgetListRequest;

    // === Response 초기화 === //

    BudgetResponse budgetResponse = BudgetResponse.builder().build();
    List<CategoryResponse> categoryResponseList = Category
        .toList()
        .stream()
        .map((c) -> new CategoryResponse(c.getCode(), c.getTitle()))
        .toList();

    List<BudgetResponse> updatedBudgetResponseList = new ArrayList<>() {{
        add(budgetResponse);
    }};

    @BeforeEach
    void setUp() throws Exception {
        //=== user ===//
        user = User.builder().email("test@email").password("password").build();
        ReflectionTestUtils.setField(user, "id", 11111L);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // == Requset == //
        List<CreateBudgetDetail> budgetDetails = new ArrayList<>() {{
            add(new CreateBudgetDetail(Category.FOOD, 300000L));
            add(new CreateBudgetDetail(Category.HOUSING, 500000L));
        }};
        createBudgetListRequest = CreateBudgetListRequest.builder()
            .data(budgetDetails)
            .yearMonth(YearMonth.now())
            .build();
    }

    // === Test ===//
    @WithMockUser
    @DisplayName("카테고리 API는 OK를 리턴한다")
    @Test
    void getCategories() throws Exception {
        //given
        given(budgetService.getCategories()).willReturn(categoryResponseList);

        // when
        ResultActions response = mockMvc.perform(get("/api/v1/budgets/categories")
            .contentType(MediaType.APPLICATION_JSON));

        // than
        response.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count",
                CoreMatchers.is(categoryResponseList.size())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title",
                CoreMatchers.is(categoryResponseList.get(0).title())))
            .andDo(print());
    }

    @WithMockUser
    @DisplayName("예산 설정 API는 requset body 가 null일 시 400을 리턴한다")
    @Test
    void updateBudgets_will_return400() throws Exception {
        // when
        ResultActions response = mockMvc.perform(post("/api/v1/budgets")
            .contentType(MediaType.APPLICATION_JSON)); //request body empty

        // than
        response.andExpect(status().is4xxClientError())
            .andDo(print());
    }


    @WithMockUser
    @DisplayName("--------------------------------")
    @Test
    void updateBudgets() throws Exception {
        // when
        ResultActions response = mockMvc.perform(post("/api/v1/budgets"));

        // than
        response.andExpect(status().is4xxClientError())
            .andDo(print());
    }


//
//    @DisplayName("예산 수정 API 테스트")
//    @Test
//    void testUpdateBudgets() throws Exception {
//
//        // given
//        when(budgetService.update(any(CreateBudgetListRequest.class), any()))
//            .thenReturn(budgetResponseList);
//
//        String requestBody = """
//            {
//              "data": [
//                {
//                  "category": "FOOD",
//                  "amount": 50000
//                },
//                    {
//                  "category": "UTILITIES",
//                  "amount": 45000
//                },
//                {
//                  "category": "MEDICAL",
//                  "amount": 5000
//                }
//              ],
//              "yearMonth": "2023-11"
//            }
//            """;
//
//        // when
//        mockMvc.perform(post("/api/v1/budgets")
//                .content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andDo(print());
//
//        // than
//        verify(budgetService, times(1)).update(any(CreateBudgetListRequest.class),
//            any());
//
//    }
//
//    @DisplayName("예산 추천 API 테스트")
//    @Test
//    void testRecommendBudgets() throws Exception {
//
//        // given
//        when(budgetService.recommend(any(RecommendBudgetRequest.class))).thenReturn(
//            budgetResponseList);
//
//        String requestBody = """
//                {
//                    "userBudgetTotalAmount" : 10000
//                }
//            """;
//
//        // when
//        mockMvc.perform(get("/api/v1/budgets")
//                .content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andDo(print());
//
//        // than
//        verify(budgetService, times(1)).recommend(any(RecommendBudgetRequest.class));
//
//    }
}
