package com.hyerijang.dailypay.consulting.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.WithMockCurrentUser;
import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.config.SecurityConfiguration;
import com.hyerijang.dailypay.consulting.service.ConsultingService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
@DisplayName("단위테스트 - ConsultingController")
@WithMockCurrentUser
@WebMvcTest(
    value = {ConsultingController.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class})
    }
)
@AutoConfigureMockMvc(addFilters = false)
class ConsultingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // === DI === //
    @MockBean
    private ConsultingService consultingService;

    // === 오늘 지출 추천 API  === //
    @Test
    @DisplayName("성공 : 오늘 지출 추천 API는 성공시 200을 리턴한다 ")
    void getTodayExpenses() throws Exception {
        // given
        given(consultingService.getBudgetRemainingForThisMonth(any()))
            .willReturn(350000L);
        given(consultingService.getProposalInfo(any())).willReturn( new ArrayList<>());

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/consulting/proposal-info")
            .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

    }

    // === 오늘 지출 안내 API  === //
    @Test
    @DisplayName("성공 : 오늘 지출 안내 API는 성공시 200을 리턴한다")
    void testGetTodayExpenses() throws Exception {
        //given
        given(consultingService.getBudgetThisMonth(any())).willReturn(300000L);
        given(consultingService.getAmountSpentThisMonth(any())).willReturn(170000L);
        given(consultingService.getExpenseStatisticsByCategory(any())).willReturn(
            new LinkedHashMap<>());
        given(consultingService.getBudgetsByCategoryInThisMonth(any())).willReturn(
            new ArrayList<>());

        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/consulting/today-expenses")
            .contentType(MediaType.APPLICATION_JSON));
        //than
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());
    }
}