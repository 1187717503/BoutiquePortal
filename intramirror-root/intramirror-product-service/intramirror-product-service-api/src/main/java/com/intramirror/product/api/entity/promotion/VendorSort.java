package com.intramirror.product.api.entity.promotion;

/**
 * Created on 2018/1/15.
 * @author 123
 */
public class VendorSort {
    private Long promotionVendorId;
    private Long promotionId;
    private Long vendorId;
    private String name;
    private Integer sort;

    public Long getPromotionVendorId() {
        return promotionVendorId;
    }

    public void setPromotionVendorId(Long promotionVendorId) {
        this.promotionVendorId = promotionVendorId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
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
