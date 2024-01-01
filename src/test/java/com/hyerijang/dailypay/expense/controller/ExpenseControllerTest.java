package com.hyerijang.dailypay.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import org.springframework.test.web.servlet.ResultActions;

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
    @DisplayName("성공 : 지출 내역 생성 API는 성공시 200을 리턴한다")
    void createExpense() throws Exception {
        // given
        ExpenseResponse createdExpenseResponse = createSampleExpenseDto();

        given(expenseService.createExpense(any(), any()))
            .willReturn(createdExpenseResponse);

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
        ResultActions perform = mockMvc.perform(post("/api/v1/expenses")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"))
            .andDo(print());
    }


    // === 지출 조회 (단건) API ===//
    @Test
    @DisplayName("성공 :  유저의 지출 내역(단건) 조회 API는 성공시 200을 리턴한다")
    void getExpenseById() throws Exception {
        //given
        given(expenseService.getExpenseById(any(), any()))
            .willReturn(createSampleExpenseDto());

        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/expenses/{id}", 1000)
            .contentType(MediaType.APPLICATION_JSON));
        //than
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"))
            .andDo(print());

    }


    // === 지출 수정 (단건) API ===//
    @Test
    @DisplayName("실패 : 유저의 지출 내역(단건) 수정 API 는 request가 null일 시 400을 리턴한다")
    void updateExpenseNoRequestBody() throws Exception {
        //given
        given(expenseService.updateExpense(any(), any(), any())).willReturn(
            createSampleExpenseDto());

        //when
        ResultActions perform = mockMvc.perform(patch("/api/v1/expenses/{id}", 1000)
            .contentType(MediaType.APPLICATION_JSON));

        //than
        perform.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("성공 : 유저의 지출 내역(단건) 수정 API는 성공시 200을 리턴한다")
    void updateExpense() throws Exception {
        //given
        given(expenseService.updateExpense(any(), any(), any())).willReturn(
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

        //when
        ResultActions perform = mockMvc.perform(patch("/api/v1/expenses/{id}", 1000)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON));
        //than
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"));
    }


    // === 지출 삭제 (단건) API ===//
    @Test
    @DisplayName("성공 : 유저의 지출 내역(단건) 삭제 API는 성공시 200을 리턴한다")
    void deleteExpense() throws Exception {
        //given
        given(expenseService.deleteExpense(any(), any())).willReturn(
            createSampleExpenseDto());

        //when
        ResultActions perform = mockMvc.perform(delete("/api/v1/expenses/{id}", 1000)
            .contentType(MediaType.APPLICATION_JSON));

        //than
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"));
    }

    @Test
    @DisplayName("성공 : 유저의 지출 내역(단건)을 합계에서 제외하는 API는 성공시 200을 리턴한다")
    void excludeFromTotal() throws Exception {
        //given
        given(expenseService.excludeFromTotal(any(), any())).willReturn(
            createSampleExpenseDto());

        //when
        ResultActions perform = mockMvc.perform(
            patch("/api/v1/expenses/{id}/exclude-total-sum", 1000)
                .contentType(MediaType.APPLICATION_JSON));

        //than
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.amount").value(50000))
            .andExpect(jsonPath("$.data.memo").value("Lunch"))
            .andExpect(jsonPath("$.data.excludeFromTotal").value(false))
            .andExpect(
                jsonPath("$.data.expenseDate").value("2023-11-14 12:30:00"));
    }
}
