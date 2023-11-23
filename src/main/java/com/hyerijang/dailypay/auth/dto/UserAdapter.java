package com.hyerijang.dailypay.auth.dto;


import com.hyerijang.dailypay.user.domain.User;
import lombok.Getter;

@Getter
public class UserAdapter extends CustomUserDetails {

    public UserAdapter(User user) {
        super(user);
    }
}
