package com.hyerijang.dailypay.budget.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.budget.dto.BudgetResponse;
import com.hyerijang.dailypay.budget.dto.CategoryResponse;
import com.hyerijang.dailypay.budget.dto.CreateBudgetListRequest;
import com.hyerijang.dailypay.budget.dto.RecommendBudgetRequest;
import com.hyerijang.dailypay.budget.service.BudgetService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("단위테스트 - BudgetController")
@ExtendWith(SpringExtension.class)
public class BudgetControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BudgetService budgetService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BudgetController budgetController;

    String json = """
        {
          "id": 32,
          "yearMonth": "2022-08",
          "category": "FOOD",
          "amount": 60000
        }
        """;

    String json2 = """
        {
          "id": 33,
          "yearMonth": "2022-08",
          "category": "UTILITIES",
          "amount": 40000
        }
        """;


    List<CategoryResponse> categoryResponseList;
    List<BudgetResponse> budgetResponseList;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(budgetController).build();

        // 테스트용 데이터 생성
        ObjectMapper objectMapper = new ObjectMapper();

        budgetResponseList = new ArrayList<>();
        BudgetResponse budgetResponse = objectMapper.readValue(json, BudgetResponse.class);
        BudgetResponse budgetResponse2 = objectMapper.readValue(json2, BudgetResponse.class);
        budgetResponseList.add(budgetResponse);
        budgetResponseList.add(budgetResponse2);

        categoryResponseList = Category
            .toList()
            .stream()
            .map((c) -> new CategoryResponse(c.getCode(), c.getTitle()))
            .toList();
    }


    @DisplayName("카테고리 API 테스트")
    @Test
    void getCategories() throws Exception {
        //given
        when(budgetService.getCategories()).thenReturn(categoryResponseList);

        // when
        mockMvc.perform(get("/api/v1/budgets/categories")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

        // than
        verify(budgetService, times(1)).getCategories();
    }

    @DisplayName("예산 수정 API 테스트")
    @Test
    void testUpdateBudgets() throws Exception {

        // given
        when(budgetService.update(any(CreateBudgetListRequest.class), any()))
            .thenReturn(budgetResponseList);

        String requestBody = """
            {
              "data": [
                {
                  "category": "FOOD",
                  "amount": 50000
                },
                    {
                  "category": "UTILITIES",
                  "amount": 45000
                },
                {
                  "category": "MEDICAL",
                  "amount": 5000
                }
              ],
              "yearMonth": "2023-11"
            }
            """;

        // when
        mockMvc.perform(post("/api/v1/budgets")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

        // than
        verify(budgetService, times(1)).update(any(CreateBudgetListRequest.class),
            any());

    }

    @DisplayName("예산 추천 API 테스트")
    @Test
    void testRecommendBudgets() throws Exception {

        // given
        when(budgetService.recommend(any(RecommendBudgetRequest.class))).thenReturn(
            budgetResponseList);

        String requestBody = """
                {
                    "userBudgetTotalAmount" : 10000
                }
            """;

        // when
        mockMvc.perform(get("/api/v1/budgets")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

        // than
        verify(budgetService, times(1)).recommend(any(RecommendBudgetRequest.class));

    }
}
