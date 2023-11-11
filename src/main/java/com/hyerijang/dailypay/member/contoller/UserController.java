package com.hyerijang.dailypay.member.contoller;

import com.hyerijang.dailypay.member.dto.RegisterRequest;
import com.hyerijang.dailypay.member.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.noContent().build();
    }

}
