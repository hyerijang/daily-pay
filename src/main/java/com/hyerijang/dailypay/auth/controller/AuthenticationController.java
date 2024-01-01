package com.hyerijang.dailypay.auth.controller;

import com.hyerijang.dailypay.auth.dto.AuthenticationRequest;
import com.hyerijang.dailypay.auth.dto.AuthenticationResponse;
import com.hyerijang.dailypay.auth.dto.RegisterRequest;
import com.hyerijang.dailypay.auth.service.AuthenticationService;
import com.hyerijang.dailypay.common.aop.ExeTimer;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @ExeTimer
    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody @Validated RegisterRequest request
    ) {

        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
        @RequestBody @Validated AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Access token 갱신", description = "refresh token을 필요로 합니다.")
    @PostMapping("/refresh")
    public void refreshAccessToken(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    @Operation(summary = "로그아웃")

    @PostMapping("/logout")
    public void logout() {
        //SpringSecurity에서 로그아웃 처리해야함. 여기까지 넘어오면 X
        //SecurityConfiguration 참조
        throw new ApiException(ExceptionEnum.LOGOUT_EXCEPTION);
    }
}
