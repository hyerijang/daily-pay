package com.hyerijang.dailypay.common.exception.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiExceptionResponse {

    private String errorCode;
    private String errorMessage;

    @Builder
    public ApiExceptionResponse(HttpStatus status, String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
