package com.foodiary.auth.common;

import java.util.Map;

import lombok.Data;

@Data
public class CommonResponse<T> {
    private int code;
    private String message;
    private Map<String, T> attribute;

    public CommonResponse(CommonCode commonCode, Map<String, T> attribute) {
        this.code = commonCode.getCode();
        this.message = commonCode.getMessage();
        this.attribute = attribute;
    }

    public CommonResponse(CommonCode commonCode) {
        this.code = commonCode.getCode();
        this.message = commonCode.getMessage();
    }

    public CommonResponse(CommonCode commonCode, String message, Map<String, T> attribute) {
        this.code = commonCode.getCode();
        this.message = message;
        this.attribute = attribute;
    }
}
