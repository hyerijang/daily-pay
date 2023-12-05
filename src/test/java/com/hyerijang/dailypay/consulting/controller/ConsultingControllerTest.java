package com.hyerijang.dailypay.consulting.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.budget.dto.BudgetDto;
import com.hyerijang.dailypay.consulting.service.ConsultingService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("단위테스트 - ConsultingController")
@ExtendWith(SpringExtension.class)
class ConsultingControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ConsultingService consultingService;

    @InjectMocks
    private ConsultingController consultingController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(consultingController).build();
    }

    private List<BudgetDto> createBudgetDtoList() throws JsonProcessingException {
        String json = """
                    {
                        "yearMonth": "2023-11",
                        "category": "FOOD",
                        "amount": 6100
                    }
            """;
        String json2 = """
                    {
                        "yearMonth": "2023-11",
                        "category": "UTILITIES",
                        "amount": 2600
                    }
            """;

        BudgetDto budgetDto = objectMapper.readValue(json, BudgetDto.class);
        BudgetDto budgetDto2 = objectMapper.readValue(json2, BudgetDto.class);

        List<BudgetDto> list = new ArrayList<>();
        list.add(budgetDto);
        list.add(budgetDto2);
        return list;
    }

    @Test
    @DisplayName("성공 : [D-1] 오늘 지출 추천 API 테스트 ")
    void getTodayExpenses() throws Exception {

        // given
        when(consultingService.getBudgetRemainingForThisMonth(any()))
            .thenReturn(350000L);
        when(consultingService.getProposalInfo(any())).thenReturn(createBudgetDtoList());

        // when
        mockMvc.perform(get("/api/v1/consulting/proposal-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.size()").value(2))
            .andDo(print());
        // then
        verify(consultingService, times(1)).getBudgetRemainingForThisMonth(any());
        verify(consultingService, times(1)).getProposalInfo(any());

    }


    @Test
    @DisplayName("성공 : [D-2] 오늘 지출 안내 API")
    void testGetTodayExpenses() throws Exception {

        when(consultingService.getBudgetThisMonth(any())).thenReturn(300000L);
        when(consultingService.getAmountSpentThisMonth(any())).thenReturn(170000L);
        when(consultingService.getExpenseStatisticsByCategory(any())).thenReturn(
            new LinkedHashMap<>());

        when(consultingService.getBudgetsByCategoryInThisMonth(any())).thenReturn(
            new ArrayList<>());

        mockMvc.perform(get("/api/v1/consulting/today-expenses")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

        verify(consultingService, times(1)).getBudgetThisMonth(any());


    }
}