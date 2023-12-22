package com.hyerijang.dailypay.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.WithMockCurrentUser;
import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.config.SecurityConfiguration;
import com.hyerijang.dailypay.expense.dto.ExpenseResponse;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@DisplayName("단위테스트 - ExpenseSearchController")
@WithMockCurrentUser //테스트 시 @WithMockUser 사용 불가 (커스텀 auth 저장) ->  @WithMockCurrentUser 사용해야함
@WebMvcTest(
    value = {ExpenseSearchController.class}, // 특정 Controller만 로딩하여 테스트
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class}) //스캔 대상에서 제외
    }
)
@AutoConfigureMockMvc(addFilters = false) //MockMvc를 자동으로 설정 (@Autowired)
class ExpenseSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // === DI === //
    @MockBean
    private ExpenseService expenseService;

    @Test
    @DisplayName("조회 조건을 지정하지 않아도 200을 리턴한다")
    void getAllExpenses_no_param() throws Exception {
        // given
        Page<ExpenseResponse> userExpensesPage = Page.empty();
        given(expenseService.searchPage(any(), any()))
            .willReturn(userExpensesPage);

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/expenses")
            .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.categoryWiseExpenseSum").isEmpty())
            .andDo(print());

    }


    @Test
    @DisplayName("지출 내역 조회 성공시 200을 리턴한다")
    void getAllExpenses() throws Exception {
        // given
        Page<ExpenseResponse> userExpensesPage = Page.empty();
        given(expenseService.searchPage(any(), any()))
            .willReturn(userExpensesPage);

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/expenses")
            .param("start", "2023-11-01")
            .param("end", "2023-11-30")
            .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.categoryWiseExpenseSum").isEmpty())
            .andDo(print());

    }
}