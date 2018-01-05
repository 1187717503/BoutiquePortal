package com.intramirror.product.api.model;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public class PromotionInclude extends PromotionRuleDetail {
    private Long promotionIncludeId;
    private Long promotionIncludeRuleId;

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

}
