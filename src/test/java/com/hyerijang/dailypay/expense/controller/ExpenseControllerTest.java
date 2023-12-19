package com.hyerijang.dailypay.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.WithMockCurrentUser;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.config.SecurityConfiguration;
import com.hyerijang.dailypay.expense.dto.ExpenseResponse;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@DisplayName("단위테스트 - ExpenseController")
@WithMockCurrentUser //테스트 시 @WithMockUser 사용 불가 (커스텀 auth 저장) ->  @WithMockCurrentUser 사용해야함
@WebMvcTest(
    value = {ExpenseController.class}, // 특정 Controller만 로딩하여 테스트
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class}) //스캔 대상에서 제외
    }
)
@AutoConfigureMockMvc(addFilters = false) //MockMvc를 자동으로 설정 (@Autowired)
class ExpenseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // === DI === //
    @MockBean
    private ExpenseService expenseService;
    
    
    // === 지출 생성 API ===//
    private ExpenseResponse createSampleExpenseDto() {
        return ExpenseResponse.builder()
            .id(1L)
            .userId(1L)
            .category(Category.FOOD)
            .amount(50000L)
            .memo("Lunch")
            .excludeFromTotal(false)
            .expenseDate(LocalDateTime.parse("2023-11-14T12:30:00"))
            .build();
    }

    private List<ExpenseResponse> createSampleExpenseDtoList() {
        ExpenseResponse expense1 = createSampleExpenseDto();
        ExpenseResponse expense2 = ExpenseResponse.builder()
            .id(2L)
            .userId(1L)
            .category(Category.UTILITIES)
            .amount(40000L)
            .memo("Utilities")
            .excludeFromTotal(false)
            .expenseDate(LocalDateTime.parse("2023-11-15T15:45:00"))
            .build();
        return Stream.of(expense1, expense2).collect(Collectors.toList());
    }

    @Test
    @DisplayName("성공 : 지출 내역 생성 API 테스트 ")
    void createExpense() throws Exception {
        // given
        ExpenseResponse createdExpenseResponse = createSampleExpenseDto();

        when(expenseService.createExpense(any(), any()))
            .thenReturn(createdExpenseResponse);

        String json = """
                {
                  "category": "FOOD",
                  "amount": 50000,
                  "memo": "test_8ef6b77ce9cb",
                  "excludeFromTotal": false,
                  "expenseDate": "2023-11-14 12:30:00"
                }
            """;

        // when
        mockMvc.perform(post("/api/v1/expenses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"))
            .andDo(print());
        // then
        verify(expenseService, times(1)).createExpense(any(), any());
    }


    // === 지출 조회 (단건) API ===//
    @Test
    @DisplayName("성공 :  유저의 지출 내역(단건) 조회 API 테스트 ")
    void getExpenseById() throws Exception {

        when(expenseService.getExpenseById(any(), any()))
            .thenReturn(createSampleExpenseDto());

        //when
        mockMvc.perform(get("/api/v1/expenses/{id}", 1000)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"))
            .andDo(print());
        //then

        verify(expenseService, times(1)).getExpenseById(any(), any());

    }


    // === 지출 수정 (단건) API ===//
    @Test
    @DisplayName("실패 : 유저의 지출 내역(단건) 수정 API 시 requestBody 누락")
    void updateExpenseNoRequestBody() throws Exception {

        when(expenseService.updateExpense(any(), any(), any())).thenReturn(
            createSampleExpenseDto());

        mockMvc.perform(patch("/api/v1/expenses/{id}", 1000)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
        verify(expenseService, times(0)).updateExpense(any(), any(), any());
    }

    @Test
    @DisplayName("성공 : 유저의 지출 내역(단건) 수정 API ")
    void updateExpense() throws Exception {

        when(expenseService.updateExpense(any(), any(), any())).thenReturn(
            createSampleExpenseDto());

        String json = """
                {
                  "category": "FOOD",
                  "amount": 50000,
                  "memo": "test_8ef6b77ce9cb",
                  "excludeFromTotal": false,
                  "expenseDate": "2023-11-14 12:30:00"
                }
            """;

        mockMvc.perform(patch("/api/v1/expenses/{id}", 1000)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"));
        verify(expenseService, times(1)).updateExpense(any(), any(), any());
    }


    // === 지출 삭제 (단건) API ===//
    @Test
    @DisplayName("성공 : 유저의 지출 내역(단건) 삭제 API")
    void deleteExpense() throws Exception {

        when(expenseService.deleteExpense(any(), any())).thenReturn(
            createSampleExpenseDto());

        mockMvc.perform(delete("/api/v1/expenses/{id}", 1000)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"));
        verify(expenseService, times(1)).deleteExpense(any(), any());
    }

    @Test
    @DisplayName("성공 : 유저의 지출 내역(단건)을 합계에서 제외하는 API")
    void excludeFromTotal() throws Exception {
        when(expenseService.excludeFromTotal(any(), any())).thenReturn(
            createSampleExpenseDto());

        mockMvc.perform(patch("/api/v1/expenses/{id}/exclude-total-sum", 1000)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"));
        verify(expenseService, times(1)).excludeFromTotal(any(), any());
    }
}
