package com.hyerijang.dailypay.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증(로그인) 요청")
public record AuthenticationRequest(
    @Schema(description = "이메일")
    String email,
    @Schema(description = "비밀번호")
    String password) {

}
