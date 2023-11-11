package com.hyerijang.dailypay.auth;

import com.hyerijang.dailypay.member.entity.User;
import jakarta.validation.constraints.NotBlank;


public record RegisterRequest(
    @NotBlank String email,
    @NotBlank String password
) {

    public User toEntity() {
        return User.builder()
            .email(this.email)
            .password(this.password)
            .build();
    }
}
