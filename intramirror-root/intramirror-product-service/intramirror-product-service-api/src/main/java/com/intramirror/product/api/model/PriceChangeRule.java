package com.intramirror.product.api.model;

import java.util.Date;

public class PriceChangeRule {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule.price_change_rule_id
     *
     * @mbggenerated
     */
    private Long priceChangeRuleId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule.price_type
     *
     * @mbggenerated
     */
    private Byte priceType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule.valid_from
     *
     * @mbggenerated
     */
    private Date validFrom;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule.status
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule.vendor_id
     *
     * @mbggenerated
     */
    private Long vendorId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column price_change_rule.shop_id
     *
     * @mbggenerated
     */
    private Long shopId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule.price_change_rule_id
     *
     * @return the value of price_change_rule.price_change_rule_id
     *
     * @mbggenerated
     */
    public Long getPriceChangeRuleId() {
        return priceChangeRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule.price_change_rule_id
     *
     * @param priceChangeRuleId the value for price_change_rule.price_change_rule_id
     *
     * @mbggenerated
     */
    public void setPriceChangeRuleId(Long priceChangeRuleId) {
        this.priceChangeRuleId = priceChangeRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule.name
     *
     * @return the value of price_change_rule.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule.name
     *
     * @param name the value for price_change_rule.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule.price_type
     *
     * @return the value of price_change_rule.price_type
     *
     * @mbggenerated
     */
    public Byte getPriceType() {
        return priceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule.price_type
     *
     * @param priceType the value for price_change_rule.price_type
     *
     * @mbggenerated
     */
    public void setPriceType(Byte priceType) {
        this.priceType = priceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule.valid_from
     *
     * @return the value of price_change_rule.valid_from
     *
     * @mbggenerated
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule.valid_from
     *
     * @param validFrom the value for price_change_rule.valid_from
     *
     * @mbggenerated
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule.status
     *
     * @return the value of price_change_rule.status
     *
     * @mbggenerated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule.status
     *
     * @param status the value for price_change_rule.status
     *
     * @mbggenerated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule.vendor_id
     *
     * @return the value of price_change_rule.vendor_id
     *
     * @mbggenerated
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule.vendor_id
     *
     * @param vendorId the value for price_change_rule.vendor_id
     *
     * @mbggenerated
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column price_change_rule.shop_id
     *
     * @return the value of price_change_rule.shop_id
     *
     * @mbggenerated
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column price_change_rule.shop_id
     *
     * @param shopId the value for price_change_rule.shop_id
     *
     * @mbggenerated
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}