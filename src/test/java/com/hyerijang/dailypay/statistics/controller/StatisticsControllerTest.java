package com.hyerijang.dailypay.statistics.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.WithMockCurrentUser;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.config.SecurityConfiguration;
import com.hyerijang.dailypay.statistics.dto.StatisticsDto;
import com.hyerijang.dailypay.statistics.service.StatisticsDummyDataGenerator;
import com.hyerijang.dailypay.statistics.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@DisplayName("단위테스트 - StatisticsController")
@WithMockCurrentUser
@WebMvcTest(
    value = {StatisticsController.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class})
    }
)
@AutoConfigureMockMvc(addFilters = false) //MockMvc를 자동으로 설정 (@Autowired)
class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // === DI === //
    @Mock
    private  Environment environment;

    @MockBean
    private  StatisticsDummyDataGenerator dummyDataGenerator;

    @MockBean
    private  StatisticsService statisticsService;

    // === 통계 API ===//
    @Test
    @DisplayName("실패 : 통계 API 는 잘못된 파라미터 값이 들어오면 400을 리턴한다.")
    void getExpenseComparison_with_wrong_param() throws Exception {
        //given
        String condition = "wrong";
        //when
        ResultActions perform = mockMvc
            .perform(get("/api/v1/statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .param("condition",condition));
        //than
        perform.andExpect(status().is4xxClientError()) //status
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("errorCode").value(ExceptionEnum.WRONG_EXPENSE_COMPARISON_CONDITION.getCode())) //잘못된 파라미터 에러 코드
            .andDo(print());
    }

    @Test
    @DisplayName("성공 : 통계 API는 성공시 200을 리턴한다.")
    void getExpenseComparison_return_ok() throws Exception {
        //given
        String condition = "last-month";
        when(statisticsService.getExpenseComparisonLastMonth(anyLong())).thenReturn(new StatisticsDto(null,null));
        //when
        ResultActions perform = mockMvc
            .perform(get("/api/v1/statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .param("condition",condition));
        //than
        perform.andExpect(status().isOk()) //status
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());
    }
}