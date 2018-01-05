package com.intramirror.product.api.entity.promotion;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public class BrandEntity {
    private Long brandId;
    private String name;

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
