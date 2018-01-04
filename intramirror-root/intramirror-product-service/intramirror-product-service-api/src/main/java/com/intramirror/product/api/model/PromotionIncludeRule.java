package com.intramirror.product.api.model;

import java.util.Date;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public class PromotionIncludeRule {
    private Long promotionIncludeRuleId;
    private Long promotionId;
    private Long vendorId;
    private String seasonCode;
    private String brands;
    private String categorys;
    private Date createdAt;

    public Long getPromotionIncludeRuleId() {
        return promotionIncludeRuleId;
    }

    public void setPromotionIncludeRuleId(Long promotionIncludeRuleId) {
        this.promotionIncludeRuleId = promotionIncludeRuleId;
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

    public String getSeasonCode() {
        return seasonCode;
    }

    public void setSeasonCode(String seasonCode) {
        this.seasonCode = seasonCode;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getCategorys() {
        return categorys;
    }

    public void setCategorys(String categorys) {
        this.categorys = categorys;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
