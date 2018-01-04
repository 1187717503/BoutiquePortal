package com.intramirror.product.api.model;

import java.util.Date;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public class PromotionInclude {
    private Long promotionIncludeId;
    private Long promotionIncludeRuleId;
    private Long promotionId;
    private String season_code;
    private Long vendorId;
    private Long categoryId;
    private Long brandId;
    private Date createdAt;
    private Date updatedAt;

    public Long getPromotionIncludeId() {
        return promotionIncludeId;
    }

    public void setPromotionIncludeId(Long promotionIncludeId) {
        this.promotionIncludeId = promotionIncludeId;
    }

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

    public String getSeason_code() {
        return season_code;
    }

    public void setSeason_code(String season_code) {
        this.season_code = season_code;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
