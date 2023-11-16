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
    ALREADY_EXIST_USER(HttpStatus.NOT_FOUND, "U002", "중복 회원입니다."),

    //budget
    NO_BUDGET_IN_THE_MONTH(HttpStatus.NOT_FOUND, "B001", "해당년월에는 예산이 존재하지 않습니다"),

    //expense
    NOT_EXIST_EXPENSE(HttpStatus.NOT_FOUND, "EXP001", "존재하지 않는 지출입니다."),
    NOT_WRITER_OF_EXPENSE(HttpStatus.FORBIDDEN, "EX002", "조회하는 유저가 지출의 작성자가 아닙니다."),
    ALREADY_DELETED_EXPENSE(HttpStatus.FORBIDDEN, "EX003", "이미 삭제된 지출입니다."),

    //statistics
    NOT_EXIST_OTHER_USER(HttpStatus.NOT_FOUND, "S001", "통계 생성을 위한 다른 유저 데이터가 존재하지 않습니다."),
    NOT_DEV_ENVIRONMENT(HttpStatus.FORBIDDEN, "S002 ", "운영 환경에서 실행할 수 없는 API입니다."),
    WRONG_EXPENSE_COMPARISON_CONDITION(HttpStatus.BAD_REQUEST, "S003", "지출 통계 쿼리 파라미터가 잘못 되었습니다.");


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
