package com.intramirror.product.api.enums;

/**
 * Created by dingyifan on 2017/8/2.
 */
public enum ApiErrorTypeEnum {
    BRAND_NOT_EXIST("-1001","brand not exist"),
    CATEGORY_NOT_EXIST("-1002","category not exist"),
    SEASON_NOT_EXIST("-1003","season not exist"),
    BOUTIQUE_ID_NOT_EXIST("-1004","boutique id not exist"),
    BOUTIQUE_ID_ALREADY_EXIST("-1005","boutique id already exist"),
    RETAIL_PRICE_IS_ZERO("-1006","retail price is zero or null"),
    BOUTIQUE_NAME_NOT_EXIST("-1007","boutique name not exist"),
    BRANDID_NOT_EXIST("-1008","BrandID not exist"),
    BRANDNAME_NOT_EXIST("-1009","brandName not exist"),
    COLORCODE_NOT_EXIST("-1010","colorCode not exist"),
    IMG_NOT_EXIST("-1011","img not exist"),
    WEIGHT_NOT_EXIST("-1012","weight not exist"),
    SKU_NOT_EXIST("-1013","sku not exist"),
    SKU_SIZT_NOT_EXIST("-1014","sku size not exist");

    private String code;
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    ApiErrorTypeEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
