package com.hyerijang.dailypay.common.exception.advice;

import com.hyerijang.dailypay.common.exception.response.ApiExceptionResponse;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("com.hyerijang.dailypay.auth") //auth 패키지에만 적용
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityExceptionAdvice {

    //인증 실패
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(final AccessDeniedException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity
            .status(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getStatus())
            .body(ApiExceptionResponse.builder()
                .errorCode(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getCode())
                .errorMessage(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getMessage())
                .build());
    }

    //잘못된 인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(final IllegalArgumentException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity
            .status(ExceptionEnum.WRONG_REQUEST.getStatus())
            .body(ApiExceptionResponse.builder()
                .errorCode(ExceptionEnum.WRONG_REQUEST.getCode())
                .errorMessage(ExceptionEnum.WRONG_REQUEST.getMessage())
                .build());
    }

    //중복 저장 시도
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(
        final DataIntegrityViolationException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity
            .status(ExceptionEnum.ALREADY_EXIST_USER.getStatus())
            .body(ApiExceptionResponse.builder()
                .errorCode(ExceptionEnum.ALREADY_EXIST_USER.getCode())
                .errorMessage(ExceptionEnum.ALREADY_EXIST_USER.getMessage())
                .build());
    }

}
