package com.hyerijang.dailypay.auth.service;

import com.hyerijang.dailypay.auth.dto.UserAdapter;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * username이 DB에 존재하는지 확인
     **/
    @Override
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {
        log.info("DB 확인 : {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(
            () -> new ApiException(ExceptionEnum.NOT_EXIST_USER));

        /** 시큐리티 세션에 유저 정보 저장**/
        return new UserAdapter(user);
    }
}
