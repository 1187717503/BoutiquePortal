package com.intramirror.product.api.entity.promotion;

/**
 * Created on 2018/1/15.
 * @author 123
 */
public class BrandSort {
    private Long promotionBrandId;
    private Long promotionId;
    private Long brandId;
    private String name;
    private Integer sort;

    public Long getPromotionBrandId() {
        return promotionBrandId;
    }

    public void setPromotionBrandId(Long promotionBrandId) {
        this.promotionBrandId = promotionBrandId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
