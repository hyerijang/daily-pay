package com.hyerijang.dailypay.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hyerijang.dailypay.WithMockCurrentUser;
import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.config.SecurityConfiguration;
import com.hyerijang.dailypay.user.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("단위테스트 - CurrentUserTestController")
@WebMvcTest(
    value = {CurrentUserTestController.class}, // 특정 Controller만 로딩하여 테스트
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class}) //스캔 대상에서 제외
    }
)
@AutoConfigureMockMvc //MockMvc를 자동으로 설정 (@Autowired)
@WithMockCurrentUser
class CurrentUserTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // === CurrentUser 테스트 API ===//
    @WithMockCurrentUser(role = Role.MANAGER)
    @DisplayName("성공 : 현재 유저의 유저 정보를 응답한다.")
    @Test
    void currentUser() throws Exception {
        //given

        //when
        ResultActions perform = mockMvc.perform(get("/api/test/currentUser"));

        //than
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.id").value(12345))
            .andExpect(jsonPath("$.data.email").value("dailypay@gmail.com"))
            .andDo(print());
    }
}