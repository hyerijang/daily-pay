package com.hyerijang.dailypay.common.exception;

import com.hyerijang.dailypay.common.exception.response.ExceptionEnum;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private ExceptionEnum error;

    public ApiException(ExceptionEnum exceptionEnum) {
        super((exceptionEnum.getMessage()));
        this.error = exceptionEnum;
    }

}
