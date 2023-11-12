package com.hyerijang.dailypay.config;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString(of = {"code", "title"})
public class EnumMapperValue {

    private String code;
    private String title;

    public EnumMapperValue(EnumMapperType enumMapperType) {
        code = enumMapperType.getCode();
        title = enumMapperType.getTitle();
    }

}
