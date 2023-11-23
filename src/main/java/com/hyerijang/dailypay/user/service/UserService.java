package com.hyerijang.dailypay.user.service;

import com.hyerijang.dailypay.auth.dto.RegisterRequest;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public Long register(RegisterRequest registerRequest) {
        User user = userRepository.save(registerRequest.toEntity());
        return user.getId();
    }


}
