package com.hyerijang.dailypay.member.dto;

import com.hyerijang.dailypay.member.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String account;

    @NotBlank
    private String password;

    public User toEntity() {
        return User.builder()
            .account(this.account)
            .password(this.password)
            .build();
    }
}
