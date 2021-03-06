package com.intramirror.product.api.model;

import java.math.BigDecimal;
import java.util.Date;

public class SnapshotPriceDetail {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.snapshot_price_detail_id
     *
     * @mbggenerated
     */
    private Long snapshotPriceDetailId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.snapshot_price_rule_id
     *
     * @mbggenerated
     */
    private Long snapshotPriceRuleId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.product_id
     *
     * @mbggenerated
     */
    private Long productId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.retail_price
     *
     * @mbggenerated
     */
    private BigDecimal retailPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.boutique_price
     *
     * @mbggenerated
     */
    private BigDecimal boutiquePrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.im_price
     *
     * @mbggenerated
     */
    private BigDecimal imPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.boutique_discount_off
     *
     * @mbggenerated
     */
    private Integer boutiqueDiscountOff;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.im_discount_off
     *
     * @mbggenerated
     */
    private Integer imDiscountOff;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.type
     *
     * @mbggenerated
     */
    private Byte type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.updated_at
     *
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column snapshot_price_detail.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.snapshot_price_detail_id
     *
     * @return the value of snapshot_price_detail.snapshot_price_detail_id
     *
     * @mbggenerated
     */
    public Long getSnapshotPriceDetailId() {
        return snapshotPriceDetailId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.snapshot_price_detail_id
     *
     * @param snapshotPriceDetailId the value for snapshot_price_detail.snapshot_price_detail_id
     *
     * @mbggenerated
     */
    public void setSnapshotPriceDetailId(Long snapshotPriceDetailId) {
        this.snapshotPriceDetailId = snapshotPriceDetailId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.snapshot_price_rule_id
     *
     * @return the value of snapshot_price_detail.snapshot_price_rule_id
     *
     * @mbggenerated
     */
    public Long getSnapshotPriceRuleId() {
        return snapshotPriceRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.snapshot_price_rule_id
     *
     * @param snapshotPriceRuleId the value for snapshot_price_detail.snapshot_price_rule_id
     *
     * @mbggenerated
     */
    public void setSnapshotPriceRuleId(Long snapshotPriceRuleId) {
        this.snapshotPriceRuleId = snapshotPriceRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.product_id
     *
     * @return the value of snapshot_price_detail.product_id
     *
     * @mbggenerated
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.product_id
     *
     * @param productId the value for snapshot_price_detail.product_id
     *
     * @mbggenerated
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.retail_price
     *
     * @return the value of snapshot_price_detail.retail_price
     *
     * @mbggenerated
     */
    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.retail_price
     *
     * @param retailPrice the value for snapshot_price_detail.retail_price
     *
     * @mbggenerated
     */
    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.boutique_price
     *
     * @return the value of snapshot_price_detail.boutique_price
     *
     * @mbggenerated
     */
    public BigDecimal getBoutiquePrice() {
        return boutiquePrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.boutique_price
     *
     * @param boutiquePrice the value for snapshot_price_detail.boutique_price
     *
     * @mbggenerated
     */
    public void setBoutiquePrice(BigDecimal boutiquePrice) {
        this.boutiquePrice = boutiquePrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.im_price
     *
     * @return the value of snapshot_price_detail.im_price
     *
     * @mbggenerated
     */
    public BigDecimal getImPrice() {
        return imPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.im_price
     *
     * @param imPrice the value for snapshot_price_detail.im_price
     *
     * @mbggenerated
     */
    public void setImPrice(BigDecimal imPrice) {
        this.imPrice = imPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.boutique_discount_off
     *
     * @return the value of snapshot_price_detail.boutique_discount_off
     *
     * @mbggenerated
     */
    public Integer getBoutiqueDiscountOff() {
        return boutiqueDiscountOff;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.boutique_discount_off
     *
     * @param boutiqueDiscountOff the value for snapshot_price_detail.boutique_discount_off
     *
     * @mbggenerated
     */
    public void setBoutiqueDiscountOff(Integer boutiqueDiscountOff) {
        this.boutiqueDiscountOff = boutiqueDiscountOff;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.im_discount_off
     *
     * @return the value of snapshot_price_detail.im_discount_off
     *
     * @mbggenerated
     */
    public Integer getImDiscountOff() {
        return imDiscountOff;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.im_discount_off
     *
     * @param imDiscountOff the value for snapshot_price_detail.im_discount_off
     *
     * @mbggenerated
     */
    public void setImDiscountOff(Integer imDiscountOff) {
        this.imDiscountOff = imDiscountOff;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.type
     *
     * @return the value of snapshot_price_detail.type
     *
     * @mbggenerated
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.type
     *
     * @param type the value for snapshot_price_detail.type
     *
     * @mbggenerated
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.created_at
     *
     * @return the value of snapshot_price_detail.created_at
     *
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.created_at
     *
     * @param createdAt the value for snapshot_price_detail.created_at
     *
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.updated_at
     *
     * @return the value of snapshot_price_detail.updated_at
     *
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.updated_at
     *
     * @param updatedAt the value for snapshot_price_detail.updated_at
     *
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column snapshot_price_detail.enabled
     *
     * @return the value of snapshot_price_detail.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column snapshot_price_detail.enabled
     *
     * @param enabled the value for snapshot_price_detail.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}