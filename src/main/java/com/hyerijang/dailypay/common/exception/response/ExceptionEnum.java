package com.hyerijang.dailypay.common.exception.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {

    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "E001"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E002"),
    ILLEGAL_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST, "E003", "잘못된 입력입니다."),
    //auth
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "A002", "인증이 필요합니다"),
    WRONG_REQUEST(HttpStatus.BAD_REQUEST, "A003", "잘못된 요청입니다."),

    //user
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 사용자입니다."),
    ALREADY_EXIST_USER(HttpStatus.NOT_FOUND, "U002", "중복 회원입니다.");


    private final HttpStatus status;
    private final String code;
    private String message;


    ExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    ExceptionEnum(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }
}
