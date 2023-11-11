package com.hyerijang.dailypay.budget.domain;

import com.hyerijang.dailypay.config.EnumMapperType;
import lombok.Getter;

public enum Category implements EnumMapperType {
    FOOD("음식"),
    HOUSING("주거"),
    TRANSPORTATION("대중교통"),
    MEDICAL("의료"),
    SAVING("저축"),
    UTILITIES("생활용품"),
    INSURANCE("보험"),
    MISCELLANEOUS("기타");

    @Getter
    private String title;

    Category(String title) {
        this.title = title;
    }

    @Override
    public String getCode() {
        return name();
    }
}
