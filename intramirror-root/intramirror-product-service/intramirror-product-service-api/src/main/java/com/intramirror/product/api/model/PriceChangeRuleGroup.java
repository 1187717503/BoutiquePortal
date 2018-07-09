package com.intramirror.product.api.model;

public class PriceChangeRuleGroup {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule_group.price_change_rule_group_id
     *
     * @mbggenerated
     */
    private Long priceChangeRuleGroupId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule_group.price_change_rule_id
     *
     * @mbggenerated
     */
    private Long priceChangeRuleId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule_group.product_group_id
     *
     * @mbggenerated
     */
    private Long productGroupId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule_group.discount_percentage
     *
     * @mbggenerated
     */
    private Long discountPercentage;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule_group.price_change_rule_group_id
     *
     * @return the value of price_change_rule_group.price_change_rule_group_id
     *
     * @mbggenerated
     */
    public Long getPriceChangeRuleGroupId() {
        return priceChangeRuleGroupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule_group.price_change_rule_group_id
     *
     * @param priceChangeRuleGroupId the value for price_change_rule_group.price_change_rule_group_id
     *
     * @mbggenerated
     */
    public void setPriceChangeRuleGroupId(Long priceChangeRuleGroupId) {
        this.priceChangeRuleGroupId = priceChangeRuleGroupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule_group.price_change_rule_id
     *
     * @return the value of price_change_rule_group.price_change_rule_id
     *
     * @mbggenerated
     */
    public Long getPriceChangeRuleId() {
        return priceChangeRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule_group.price_change_rule_id
     *
     * @param priceChangeRuleId the value for price_change_rule_group.price_change_rule_id
     *
     * @mbggenerated
     */
    public void setPriceChangeRuleId(Long priceChangeRuleId) {
        this.priceChangeRuleId = priceChangeRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule_group.product_group_id
     *
     * @return the value of price_change_rule_group.product_group_id
     *
     * @mbggenerated
     */
    public Long getProductGroupId() {
        return productGroupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule_group.product_group_id
     *
     * @param productGroupId the value for price_change_rule_group.product_group_id
     *
     * @mbggenerated
     */
    public void setProductGroupId(Long productGroupId) {
        this.productGroupId = productGroupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule_group.discount_percentage
     *
     * @return the value of price_change_rule_group.discount_percentage
     *
     * @mbggenerated
     */
    public Long getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule_group.discount_percentage
     *
     * @param discountPercentage the value for price_change_rule_group.discount_percentage
     *
     * @mbggenerated
     */
    public void setDiscountPercentage(Long discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}