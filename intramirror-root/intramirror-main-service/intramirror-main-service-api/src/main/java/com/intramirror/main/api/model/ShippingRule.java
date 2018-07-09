package com.intramirror.main.api.model;

import java.math.BigDecimal;

public class ShippingRule {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.shipping_rule_id
     *
     * @mbggenerated
     */
    private Long shippingRuleId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.category_id
     *
     * @mbggenerated
     */
    private Long categoryId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.from_country_id
     *
     * @mbggenerated
     */
    private Long fromCountryId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.to_country_id
     *
     * @mbggenerated
     */
    private Long toCountryId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.shipping_provider_id
     *
     * @mbggenerated
     */
    private Long shippingProviderId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.threshold
     *
     * @mbggenerated
     */
    private Integer threshold;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.rate
     *
     * @mbggenerated
     */
    private BigDecimal rate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.min_number
     *
     * @mbggenerated
     */
    private BigDecimal minNumber;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.max_number
     *
     * @mbggenerated
     */
    private BigDecimal maxNumber;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.deduction
     *
     * @mbggenerated
     */
    private Integer deduction;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_rule.base_fee
     *
     * @mbggenerated
     */
    private Integer baseFee;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.shipping_rule_id
     *
     * @return the value of shipping_rule.shipping_rule_id
     *
     * @mbggenerated
     */
    public Long getShippingRuleId() {
        return shippingRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.shipping_rule_id
     *
     * @param shippingRuleId the value for shipping_rule.shipping_rule_id
     *
     * @mbggenerated
     */
    public void setShippingRuleId(Long shippingRuleId) {
        this.shippingRuleId = shippingRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.category_id
     *
     * @return the value of shipping_rule.category_id
     *
     * @mbggenerated
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.category_id
     *
     * @param categoryId the value for shipping_rule.category_id
     *
     * @mbggenerated
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.from_country_id
     *
     * @return the value of shipping_rule.from_country_id
     *
     * @mbggenerated
     */
    public Long getFromCountryId() {
        return fromCountryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.from_country_id
     *
     * @param fromCountryId the value for shipping_rule.from_country_id
     *
     * @mbggenerated
     */
    public void setFromCountryId(Long fromCountryId) {
        this.fromCountryId = fromCountryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.to_country_id
     *
     * @return the value of shipping_rule.to_country_id
     *
     * @mbggenerated
     */
    public Long getToCountryId() {
        return toCountryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.to_country_id
     *
     * @param toCountryId the value for shipping_rule.to_country_id
     *
     * @mbggenerated
     */
    public void setToCountryId(Long toCountryId) {
        this.toCountryId = toCountryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.shipping_provider_id
     *
     * @return the value of shipping_rule.shipping_provider_id
     *
     * @mbggenerated
     */
    public Long getShippingProviderId() {
        return shippingProviderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.shipping_provider_id
     *
     * @param shippingProviderId the value for shipping_rule.shipping_provider_id
     *
     * @mbggenerated
     */
    public void setShippingProviderId(Long shippingProviderId) {
        this.shippingProviderId = shippingProviderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.threshold
     *
     * @return the value of shipping_rule.threshold
     *
     * @mbggenerated
     */
    public Integer getThreshold() {
        return threshold;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.threshold
     *
     * @param threshold the value for shipping_rule.threshold
     *
     * @mbggenerated
     */
    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.rate
     *
     * @return the value of shipping_rule.rate
     *
     * @mbggenerated
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.rate
     *
     * @param rate the value for shipping_rule.rate
     *
     * @mbggenerated
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.min_number
     *
     * @return the value of shipping_rule.min_number
     *
     * @mbggenerated
     */
    public BigDecimal getMinNumber() {
        return minNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.min_number
     *
     * @param minNumber the value for shipping_rule.min_number
     *
     * @mbggenerated
     */
    public void setMinNumber(BigDecimal minNumber) {
        this.minNumber = minNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.max_number
     *
     * @return the value of shipping_rule.max_number
     *
     * @mbggenerated
     */
    public BigDecimal getMaxNumber() {
        return maxNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.max_number
     *
     * @param maxNumber the value for shipping_rule.max_number
     *
     * @mbggenerated
     */
    public void setMaxNumber(BigDecimal maxNumber) {
        this.maxNumber = maxNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.deduction
     *
     * @return the value of shipping_rule.deduction
     *
     * @mbggenerated
     */
    public Integer getDeduction() {
        return deduction;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.deduction
     *
     * @param deduction the value for shipping_rule.deduction
     *
     * @mbggenerated
     */
    public void setDeduction(Integer deduction) {
        this.deduction = deduction;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_rule.base_fee
     *
     * @return the value of shipping_rule.base_fee
     *
     * @mbggenerated
     */
    public Integer getBaseFee() {
        return baseFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_rule.base_fee
     *
     * @param baseFee the value for shipping_rule.base_fee
     *
     * @mbggenerated
     */
    public void setBaseFee(Integer baseFee) {
        this.baseFee = baseFee;
    }
}