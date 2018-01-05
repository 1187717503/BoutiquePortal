package com.intramirror.product.api.model;

/**
 * Created on 2018/1/5.
 * @author 123
 */
public class PromotionExclude extends PromotionRuleDetail {
    private Long promotionExcludeId;
    private Long promotionExcludeRuleId;

    public Long getPromotionExcludeId() {
        return promotionExcludeId;
    }

    public void setPromotionExcludeId(Long promotionExcludeId) {
        this.promotionExcludeId = promotionExcludeId;
    }

    public Long getPromotionExcludeRuleId() {
        return promotionExcludeRuleId;
    }

    public void setPromotionExcludeRuleId(Long promotionExcludeRuleId) {
        this.promotionExcludeRuleId = promotionExcludeRuleId;
    }

}
