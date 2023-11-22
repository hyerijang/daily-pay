package com.hyerijang.dailypay.common.exception.advice;

import com.hyerijang.dailypay.common.exception.ApiException;
import com.hyerijang.dailypay.common.exception.response.ApiExceptionResponse;
import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    // @Validated 시 발생하는 Exception Handling
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String processValidationError(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }

        return builder.toString();
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
