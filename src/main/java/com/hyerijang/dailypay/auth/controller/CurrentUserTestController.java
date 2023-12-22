package com.hyerijang.dailypay.auth.controller;

import com.hyerijang.dailypay.auth.CurrentUser;
import com.hyerijang.dailypay.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link com.hyerijang.dailypay.auth.CurrentUser} 테스트를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/test/currentUser")
public class CurrentUserTestController {

    @GetMapping
    public ResponseEntity currentUser(@CurrentUser User user) {
        return ResponseEntity.ok().body(Result.builder().data(user).build());
    }

    @Getter
    @Builder
    static class Result<T> {
        private T data; // 리스트의 값
    }

}
