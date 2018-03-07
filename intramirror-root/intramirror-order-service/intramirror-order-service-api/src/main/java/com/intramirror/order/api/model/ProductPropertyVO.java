package com.intramirror.order.api.model;

/**
 * Created by caowei on 2018/3/6.
 */
public class ProductPropertyVO {
    private Long productId;
    private String keyName;
    private String value;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
