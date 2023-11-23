package com.hyerijang.dailypay.auth.dto;

import com.hyerijang.dailypay.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "회원 가입 요청")
public record RegisterRequest(
    @Schema(description = "이메일")
    @Email
    @NotBlank
    String email,
    @NotBlank
    String password
) {

    public User toEntity() {
        return User.builder()
            .email(this.email)
            .password(this.password)
            .build();
    }
}
