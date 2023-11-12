package com.hyerijang.dailypay.common.exception.advice;

import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ApiExceptionResponse;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {

    // API Exception 처리
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(final ApiException e) {
        log.error("[ApiExceptionResponse] {}", e.getMessage());
        return ResponseEntity
            .status(e.getError().getStatus())
            .body(ApiExceptionResponse.builder()
                .errorCode(e.getError().getCode())
                .errorMessage(e.getError().getMessage())
                .build());
    }

    //400 : 기타 RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(final RuntimeException e) {
        log.error("[RuntimeException] {}", e.getMessage());
        return ResponseEntity
            .status(ExceptionEnum.RUNTIME_EXCEPTION.getStatus())
            .body(ApiExceptionResponse.builder()
                .errorCode(ExceptionEnum.RUNTIME_EXCEPTION.getCode())
                .errorMessage(e.getMessage())
                .build());
    }

    //500: 기타 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(final Exception e) {
        log.error("[Exception] {}", e.getMessage());
        return ResponseEntity
            .status(ExceptionEnum.INTERNAL_SERVER_ERROR.getStatus())
            .body(ApiExceptionResponse.builder()
                .errorCode(ExceptionEnum.INTERNAL_SERVER_ERROR.getCode())
                .errorMessage(e.getMessage())
                .build());
    }

}
