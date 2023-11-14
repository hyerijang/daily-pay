package com.hyerijang.dailypay.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.expense.dto.ExpenseDto;
import com.hyerijang.dailypay.expense.service.ExpenseService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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


@DisplayName("단위테스트 - ExpenseController")
@ExtendWith(SpringExtension.class)
class ExpenseControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ExpenseController expenseController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    }


    private ExpenseDto createSampleExpenseDto() {
        return ExpenseDto.builder()
            .id(1L)
            .userId(1L)
            .category(Category.FOOD)
            .amount(50000L)
            .memo("Lunch")
            .excludeFromTotal(false)
            .expenseDate(LocalDateTime.parse("2023-11-14T12:30:00"))
            .build();
    }

    private List<ExpenseDto> createSampleExpenseDtoList() {
        ExpenseDto expense1 = createSampleExpenseDto();
        ExpenseDto expense2 = ExpenseDto.builder()
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
        ExpenseDto createdExpenseDto = createSampleExpenseDto();

        when(expenseService.createExpense(any(), any()))
            .thenReturn(createdExpenseDto);

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

    @Test
    @DisplayName("성공 : 지출 내역 조회 API 테스트 ")
    void getAllExpenses() throws Exception {
        // given
        List<ExpenseDto> sampleExpenseDtoList = createSampleExpenseDtoList();

        when(expenseService.getUserAllExpenses(any(), any()))
            .thenReturn(sampleExpenseDtoList);

        // when
        mockMvc.perform(get("/api/v1/expenses")
                .param("start", "2023-11-01")
                .param("end", "2023-11-30")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(2))
            .andExpect(jsonPath("$.data[0].amount").value(50000))
            .andExpect(jsonPath("$.data[0].memo").value("Lunch"))
            .andExpect(jsonPath("$.data[0].excludeFromTotal").value(false))
            .andDo(print());
        // then
        verify(expenseService, times(1)).getUserAllExpenses(any(), any());

    }

    @Test
    @DisplayName("성공 :  유저의 지출 내역(단건) 조회 API 테스트 ")
    void getExpenseById() throws Exception {

        when(expenseService.getExpenseById(any(), any()))
            .thenReturn(createSampleExpenseDto());

        //when
        mockMvc.perform(get("/api/v1/expenses/1000")
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
}
