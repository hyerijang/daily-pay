package com.hyerijang.dailypay.auth.dto;

import com.hyerijang.dailypay.config.JwtAuthenticationFilter;
import com.hyerijang.dailypay.config.SecurityConfiguration;
import com.hyerijang.dailypay.user.domain.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@Slf4j
@DisplayName("단위테스트 - RegisterRequest")
@WebMvcTest(
    value = Null.class, //테스트에 컨트롤러 미포함
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class}) //스캔 대상에서 제외
    }
)
class RegisterRequestTest {

    @Autowired
    MessageSource messageSource;
    //=== 올바른 Email, Password ===//
    String VALID_EMAIL = "test@email.com";
    String VALID_PASSWORD = "password1234!!";

    @Autowired
    private Validator validatorInjected;

    //=== 이메일 검증 ===//
    @DisplayName("이메일 : 이메일 형식이 아닌 경우 검증 실패")
    @ParameterizedTest
    @ValueSource(strings = {"notEmail", "abc@", "@gmail.com", "이메일아님"})
    void email_patter_validate(String wrongEmail) {
        //given
        String MESSAGE_CODE = "email.not_email"; // message code
        RegisterRequest registerRequest = new RegisterRequest(wrongEmail, VALID_PASSWORD, Role.USER);
        // when
        Set<ConstraintViolation<RegisterRequest>> validate = validatorInjected.validate(
            registerRequest); //검증

        // then
        Iterator<ConstraintViolation<RegisterRequest>> iterator = validate.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<RegisterRequest> next = iterator.next();
            messages.add(next.getMessage());
        }

        Assertions.assertThat(messages)
            .contains(messageSource.getMessage(MESSAGE_CODE, null, Locale.getDefault()));
    }

    @DisplayName("이메일 : 공백인 경우 검증 실패")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void email_empty(String wrongEmail) {
        //given
        String MESSAGE_CODE = "email.not_empty"; // message code

        // when
        RegisterRequest registerRequest = new RegisterRequest(wrongEmail, VALID_PASSWORD,Role.USER);
        Set<ConstraintViolation<RegisterRequest>> validate = validatorInjected.validate(
            registerRequest); //검증

        // then
        Iterator<ConstraintViolation<RegisterRequest>> iterator = validate.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<RegisterRequest> next = iterator.next();
            messages.add(next.getMessage());
        }

        Assertions.assertThat(messages)
            .contains(messageSource.getMessage(MESSAGE_CODE, null, Locale.getDefault()));
    }

    @DisplayName("이메일 : null인 경우 검증 실패")
    @Test
    void email_empty() {
        //given
        String MESSAGE_CODE = "email.not_null"; // message code

        // when
        RegisterRequest registerRequest = new RegisterRequest(null, VALID_PASSWORD,Role.USER); // 이메일 : null
        Set<ConstraintViolation<RegisterRequest>> validate = validatorInjected.validate(
            registerRequest); //검증

        // then
        Iterator<ConstraintViolation<RegisterRequest>> iterator = validate.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<RegisterRequest> next = iterator.next();
            messages.add(next.getMessage());
        }

        Assertions.assertThat(messages)
            .contains(messageSource.getMessage(MESSAGE_CODE, null, Locale.getDefault()));
    }

    // === 비밀번호 검증 ===//
    @DisplayName("비밀번호 : 비밀번호 형식에 맞지 않은 경우 검증 실패")
    @ParameterizedTest
    @ValueSource(strings = {"a!1", "too_long_password1111", "12345678", "abcdefgh", "!@#$%^&*",
        "abcde123", "aveds!!!!!", "12345!!!!!"})
    void password_pattern_validate(String wrongPassword) {
        //give
        String MESSAGE_CODE = "password.policy_violation"; // message code
        RegisterRequest registerRequest = new RegisterRequest(VALID_EMAIL, wrongPassword,Role.USER);

        // when
        Set<ConstraintViolation<RegisterRequest>> validate = validatorInjected.validate(
            registerRequest);//검증

        // then
        Iterator<ConstraintViolation<RegisterRequest>> iterator = validate.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<RegisterRequest> next = iterator.next();
            messages.add(next.getMessage());
        }

        Assertions.assertThat(messages)
            .contains(messageSource.getMessage(MESSAGE_CODE, null, Locale.getDefault()));
    }

    @DisplayName("비밀번호 : 공백인 경우 검증 실패")
    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "   ", ""})
    void password_empty(String wrongPassword) {
        //give
        String MESSAGE_CODE = "password.not_empty"; // message code
        RegisterRequest registerRequest = new RegisterRequest(VALID_EMAIL, wrongPassword,Role.USER);

        // when
        Set<ConstraintViolation<RegisterRequest>> validate = validatorInjected.validate(
            registerRequest);//검증

        // then
        Iterator<ConstraintViolation<RegisterRequest>> iterator = validate.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<RegisterRequest> next = iterator.next();
            messages.add(next.getMessage());
        }

        Assertions.assertThat(messages)
            .contains(messageSource.getMessage(MESSAGE_CODE, null, Locale.getDefault()));
    }

    @DisplayName("비밀번호 : null인 경우 검증 실패")
    @Test
    void password_null() {
        //give
        String MESSAGE_CODE = "password.not_null"; // message code
        RegisterRequest registerRequest = new RegisterRequest(VALID_EMAIL, null,Role.USER); // 비밀번호 : null

        // when
        Set<ConstraintViolation<RegisterRequest>> validate = validatorInjected.validate(
            registerRequest);//검증

        // then
        Iterator<ConstraintViolation<RegisterRequest>> iterator = validate.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<RegisterRequest> next = iterator.next();
            messages.add(next.getMessage());
        }

        Assertions.assertThat(messages)
            .contains(messageSource.getMessage(MESSAGE_CODE, null, Locale.getDefault()));
    }

    // === Role 검증 === /

    @DisplayName("역할 : Role이 null인 경우 검증 실패")
    @Test
    void role_validate() {
        //given
        String MESSAGE_CODE = "role.not_null"; // message code
        RegisterRequest registerRequest = new RegisterRequest(VALID_EMAIL, VALID_PASSWORD, null);
        // when
        Set<ConstraintViolation<RegisterRequest>> validate = validatorInjected.validate(
            registerRequest); //검증

        // then
        Iterator<ConstraintViolation<RegisterRequest>> iterator = validate.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<RegisterRequest> next = iterator.next();
            messages.add(next.getMessage());
        }

        Assertions.assertThat(messages)
            .contains(messageSource.getMessage(MESSAGE_CODE, null, Locale.getDefault()));
    }
}