package com.hyerijang.dailypay.member.dto;

import com.hyerijang.dailypay.member.entity.User;
import jakarta.validation.constraints.NotBlank;


public record RegisterRequest(
    @NotBlank String account,
    @NotBlank String password
) {

    public User toEntity() {
        return User.builder()
            .account(this.account)
            .password(this.password)
            .build();
    }
}
