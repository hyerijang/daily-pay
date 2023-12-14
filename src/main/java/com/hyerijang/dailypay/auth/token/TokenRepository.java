package com.hyerijang.dailypay.auth.token;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findAllByToken(String token);

    Optional<Token> findByToken(String jwt);

    List<Token> findAllValidTokenByUserId(Long userId);
}