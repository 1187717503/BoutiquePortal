package com.intramirror.product.api.model;

import java.util.Date;
import java.util.List;

/**
 * Created on 2018/1/5.
 * @author 123
 */
public class PromotionRule {
    private Long ruleId;
    private Long promotionId;
    private Long vendorId;
    private String seasonCode;
    private String brands;
    private String categorys;
    private Date createdAt;
    private List<String> seasonCodes;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
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

    public List<String> getSeasonCodes() {
        return seasonCodes;
    }

    public void setSeasonCodes(List<String> seasonCodes) {
        this.seasonCodes = seasonCodes;
    }

    @Override
    public String toString() {
        return "PromotionRule{" +
                "ruleId=" + ruleId +
                ", promotionId=" + promotionId +
                ", vendorId=" + vendorId +
                ", seasonCode='" + seasonCode + '\'' +
                ", brands='" + brands + '\'' +
                ", categorys='" + categorys + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
