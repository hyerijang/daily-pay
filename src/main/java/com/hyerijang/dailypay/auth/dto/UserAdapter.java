package com.hyerijang.dailypay.auth.dto;


import com.hyerijang.dailypay.user.domain.User;
import lombok.Getter;

/**
 * CurrentUser 어노테이션으로 User 정보를 가져오기 위한 Adapter
 * @see com.hyerijang.dailypay.auth.CurrentUser
 */
@Getter
public class UserAdapter extends CustomUserDetails {

    public UserAdapter(User user) {
        super(user);
    }
}
