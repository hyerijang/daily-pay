package com.hyerijang.dailypay.auth.dto;

import com.hyerijang.dailypay.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Schema(description = "회원 가입 요청")
public record RegisterRequest(
    @Schema(description = "이메일")
    @NotNull(message = "{email.not_null}")
    @NotBlank(message = "{email.not_empty}")
    @Email(message = "{email.not_email}")
    String email,

    @NotNull(message = "{password.not_null}")
    @NotBlank(message = "{password.not_empty}")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$", message = "{password.policy_violation}")
    String password
) {

    public User toEntity() {
        return User.builder()
            .email(this.email)
            .password(this.password)
            .build();
    }
}
