package com.hyerijang.dailypay.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증(로그인) 응답")
public record AuthenticationResponse(
    @Schema(description = "access 토큰")
    @JsonProperty("access_token") String accessToken,
    @Schema(description = "refresh 토큰")
    @JsonProperty("refresh_token") String refreshToken) {

}
