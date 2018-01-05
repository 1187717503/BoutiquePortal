package com.intramirror.product.api.model;

/**
 * Created by dingyifan on 2018/1/5.
 */
public class PromotionExcludeProduct {
    private Long promotionExcludeProductId;
    private Long promotionId;
    private Long productId;

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

    public PromotionExcludeProduct() {
    }
}
