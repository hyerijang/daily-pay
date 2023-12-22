package com.hyerijang.dailypay.budget.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.WithMockCurrentUser;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetResponse;
import com.hyerijang.dailypay.budget.dto.CategoryResponse;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest.CreateBudgetDetail;
import com.hyerijang.dailypay.budget.dto.RecommendBudgetRequest;
import com.hyerijang.dailypay.budget.repository.BudgetRepository;
import com.hyerijang.dailypay.budget.service.BudgetService;
import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.config.SecurityConfiguration;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Slf4j
@DisplayName("단위테스트 - BudgetController")
@WithMockCurrentUser
@WebMvcTest(
    value = {BudgetController.class}, // 특정 controller만 로딩하여 테스트
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class})
        //Spring, jwt 필터 제외
    }
)
@AutoConfigureMockMvc(addFilters = false)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // === DI === //
    @MockBean
    private BudgetService budgetService;
    @MockBean
    private BudgetRepository budgetRepository;

    // === 카테고리 API  ===//
    @DisplayName("성공 : 카테고리 API는 성공시 200을 리턴한다")
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

    // === 예산 변경 API === //

    // == Request == //
    List<CreateBudgetDetail> budgetDetails = new ArrayList<>() {{
        add(new CreateBudgetDetail(Category.FOOD, 300000L));
        add(new CreateBudgetDetail(Category.HOUSING, 500000L));
    }};

    CreateBudgetListRequest createBudgetListRequest = CreateBudgetListRequest.builder()
        .data(budgetDetails)
        .yearMonth(YearMonth.now())
        .build();

    // == Response == //
    List<CategoryResponse> categoryResponseList = Category
        .toList()
        .stream()
        .map((c) -> new CategoryResponse(c.getCode(), c.getTitle()))
        .toList();
    BudgetResponse budgetResponse = BudgetResponse.builder().category(Category.FOOD).amount(300000L)
        .build();
    BudgetResponse budgetResponse2 = BudgetResponse.builder().category(Category.HOUSING)
        .amount(500000L).build();


    List<BudgetResponse> updatedBudgetResponseList = new ArrayList<>() {{
        add(budgetResponse);
        add(budgetResponse2);
    }};


    @DisplayName("실패 : 예산 변경 API는 requset body가 null일 시 400을 리턴한다")
    @Test
    void updateBudgets_will_return400() throws Exception {
        // when
        ResultActions response = mockMvc.perform(post("/api/v1/budgets")
            .contentType(MediaType.APPLICATION_JSON)); //request body empty

        // than
        response.andExpect(status().is4xxClientError())
            .andDo(print());
    }


    @DisplayName("성공 : 예산 변경 API는 성공시 200을 리턴한다.")
    @Test
    void updateBudgets_will_return_ok() throws Exception {
        //given
        given(budgetService.update(any(), any())).willReturn(updatedBudgetResponseList);

        // when
        ResultActions response = mockMvc.perform(post("/api/v1/budgets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createBudgetListRequest)
            )
        );

        // than
        response.andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("성공 : 예산 변경 API는 성공시 body에 변경 결과를 리턴한다.")
    @Test
    void updateBudgets() throws Exception {
        //given
        given(budgetService.update(any(), any())).willReturn(updatedBudgetResponseList);

        // when
        ResultActions response = mockMvc.perform(post("/api/v1/budgets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createBudgetListRequest)
            )
        );

        // than
        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.count").value(updatedBudgetResponseList.size()))
            .andDo(print());
    }


    // === 예산 추천 API  === //
    @DisplayName("실패 : 예산 추천 API는 requset body가 null일 시 400을 리턴한다")
    void recommendBudgets_with_null_request() throws Exception {

        // given
        given(budgetService.recommend(any(RecommendBudgetRequest.class))).willReturn(
            updatedBudgetResponseList);

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/budgets")
            .contentType(MediaType.APPLICATION_JSON));

        // than
        perform.andExpect(status().is4xxClientError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

    }

    @DisplayName("성공 : 예산 추천 API는 성공시 200을 리턴한다.")
    @Test
    void recommendBudgets() throws Exception {

        // given
        given(budgetService.recommend(any(RecommendBudgetRequest.class))).willReturn(
            updatedBudgetResponseList);

        String requestBody = """
                {
                    "userBudgetTotalAmount" : 10000
                }
            """;

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/budgets")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON));

        // than
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

    }
}
