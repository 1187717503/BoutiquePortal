package com.intramirror.web.controller.api.service;

import pk.shoplus.enums.ApiErrorTypeEnum;

public class UpdateException extends RuntimeException {
    private String key ;
    private String value ;
    private ApiErrorTypeEnum.errorType errorType ;

    public UpdateException(String key,String value,ApiErrorTypeEnum.errorType errorType) {
        this.key = key;
        this.value = value;
        this.errorType = errorType;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public ApiErrorTypeEnum.errorType getErrorType() {
        return errorType;
    }
}
