package com.hyerijang.dailypay.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "인증(로그인) 요청")
public record AuthenticationRequest(
    @Schema(description = "이메일")
    @NotBlank
    @Email
    String email,
    @Schema(description = "비밀번호")
    @NotBlank
    String password) {

}
