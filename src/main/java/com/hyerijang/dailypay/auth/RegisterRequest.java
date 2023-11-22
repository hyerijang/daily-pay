package com.hyerijang.dailypay.auth;

import com.hyerijang.dailypay.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "회원 가입 요청")
public record RegisterRequest(
    @Schema(description = "이메일")
    @NotBlank String email,
    @Schema(description = "비밀번호")
    @NotBlank String password
) {

    public User toEntity() {
        return User.builder()
            .email(this.email)
            .password(this.password)
            .build();
    }
}
