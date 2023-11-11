package com.hyerijang.dailypay.member.service;

import com.hyerijang.dailypay.member.dto.RegisterRequest;
import com.hyerijang.dailypay.member.entity.User;
import com.hyerijang.dailypay.member.repository.UserRepository;
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
