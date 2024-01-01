package com.hyerijang.dailypay.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyerijang.dailypay.auth.dto.AuthenticationRequest;
import com.hyerijang.dailypay.auth.dto.AuthenticationResponse;
import com.hyerijang.dailypay.auth.dto.RegisterRequest;
import com.hyerijang.dailypay.auth.token.Token;
import com.hyerijang.dailypay.auth.token.TokenRepository;
import com.hyerijang.dailypay.auth.token.TokenType;
import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import com.hyerijang.dailypay.user.domain.User;
import com.hyerijang.dailypay.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원 가입
     *
     * @param request
     * @return
     */
    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password())) //비밀번호는 암호화해서 저장
            .role(request.role())
            .build();
        User savedUser = userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, accessToken);
        return new AuthenticationResponse(accessToken, refreshToken);

    }

    /**
     * 유저 토큰 DB에 저장
     */
    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
        tokenRepository.save(token);
    }

    /**
     * 인증 및 로그인
     *
     * @param request
     * @return
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    /**
     * user의 모든 토큰을 취소, 만료 시킴
     *
     * @param user
     */
    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUserId(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    /**
     * 유저의 토큰 갱신 ,authHeader를 체크해서 유효한 refresh 토큰이면 access토큰 갱신해 준다.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            User user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_EXIST_USER));
            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateAccessToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                AuthenticationResponse authResponse = new AuthenticationResponse(accessToken,
                    refreshToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

}
