package com.intramirror.product.api.model;

import java.util.List;

/**
 * Created by dingyifan on 2018/1/5.
 */
public class PromotionExcludeProduct {
    private Long promotionExcludeProductId;
    private Long promotionId;
    private Long productId;
    private String name;
    private String designerId;
    private String colorCode;
    private String productCode;
    private Long vendorId;
    private List<Long> productIds;
    private List<Long> ruleIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPromotionExcludeProductId() {
        return promotionExcludeProductId;
    }

    public void setPromotionExcludeProductId(Long promotionExcludeProductId) {
        this.promotionExcludeProductId = promotionExcludeProductId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public PromotionExcludeProduct(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public PromotionExcludeProduct() {
    }
}
