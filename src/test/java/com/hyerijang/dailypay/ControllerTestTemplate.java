package com.hyerijang.dailypay;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.auth.controller.CurrentUserTestController;
import com.hyerijang.dailypay.budget.service.BudgetService;
import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.config.SecurityConfiguration;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

/***
 * API 컨트롤러 테스트 템플릿
 */
@Disabled // 해제 후 사용
@Slf4j
@DisplayName("단위테스트 - ControllerTestTemplate")
@WithMockCurrentUser //테스트 시 @WithMockUser 사용 불가 (커스텀 auth 저장) ->  @WithMockCurrentUser 사용해야함
@WebMvcTest(
    value = {CurrentUserTestController.class}, // 특정 Controller만 로딩하여 테스트
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class}) //스캔 대상에서 제외
    }
)
@AutoConfigureMockMvc(addFilters = false) //MockMvc를 자동으로 설정 (@Autowired)
class ControllerTestTemplate {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    // === DI === //
    @MockBean
    private BudgetService budgetService; // 예시를 위해 넣은 Service.

    @BeforeAll
    static void beforeAll() throws Exception {
        log.info("@BeforeAll");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        log.info("@BeforeEach");
    }

    // === 테스트 API  === //
    @DisplayName("테스트 API는 OK를 리턴한다.")
    @Test
    void ok() throws Exception {
        //given
        given(budgetService.update(any(), any())).willReturn(new ArrayList<>());
        PostRequset postRequset = PostRequset.builder().title("제목").content("내용").build();
        //when
        ResultActions perform = mockMvc
            .perform(get("/api/test/currentUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequset))); //Request body

        //than
        perform.andExpect(status().isOk()) //status
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.id").value(12345)) // Response body
            .andExpect(jsonPath("$.data.email").value("dailypay@gmail.com"))
            .andDo(print());
    }

    // == Request == //
    @Builder
    @Getter
    static class PostRequset {

        String title;
        String content;
    }

    // == Response == //
    @Builder
    @Getter
    static class PostResponse {

        String title;
        String content;
    }


}