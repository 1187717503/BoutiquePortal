package com.intramirror.product.api.model;

import java.math.BigDecimal;
import java.util.Date;

public class Sku {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.sku_id
     *
     * @mbggenerated
     */
    private Long skuId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.product_id
     *
     * @mbggenerated
     */
    private Long productId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.sku_code
     *
     * @mbggenerated
     */
    private String skuCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.in_price
     *
     * @mbggenerated
     */
    private BigDecimal inPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.price
     *
     * @mbggenerated
     */
    private BigDecimal price;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.updated_at
     *
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.retail_price
     *
     * @mbggenerated
     */
    private BigDecimal retailPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.im_price
     *
     * @mbggenerated
     */
    private BigDecimal imPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.full_modify_date
     *
     * @mbggenerated
     */
    private Date fullModifyDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.last_check
     *
     * @mbggenerated
     */
    private Date lastCheck;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.size
     *
     * @mbggenerated
     */
    private String size;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.coverpic
     *
     * @mbggenerated
     */
    private String coverpic;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku.introduction
     *
     * @mbggenerated
     */
    private String introduction;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.sku_id
     *
     * @return the value of sku.sku_id
     * @mbggenerated
     */
    public Long getSkuId() {
        return skuId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.sku_id
     *
     * @param skuId
     *         the value for sku.sku_id
     * @mbggenerated
     */
    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.product_id
     *
     * @return the value of sku.product_id
     * @mbggenerated
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.product_id
     *
     * @param productId
     *         the value for sku.product_id
     * @mbggenerated
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.sku_code
     *
     * @return the value of sku.sku_code
     * @mbggenerated
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.sku_code
     *
     * @param skuCode
     *         the value for sku.sku_code
     * @mbggenerated
     */
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.name
     *
     * @return the value of sku.name
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.name
     *
     * @param name
     *         the value for sku.name
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.in_price
     *
     * @return the value of sku.in_price
     * @mbggenerated
     */
    public BigDecimal getInPrice() {
        return inPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.in_price
     *
     * @param inPrice
     *         the value for sku.in_price
     * @mbggenerated
     */
    public void setInPrice(BigDecimal inPrice) {
        this.inPrice = inPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.price
     *
     * @return the value of sku.price
     * @mbggenerated
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.price
     *
     * @param price
     *         the value for sku.price
     * @mbggenerated
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.created_at
     *
     * @return the value of sku.created_at
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.created_at
     *
     * @param createdAt
     *         the value for sku.created_at
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.updated_at
     *
     * @return the value of sku.updated_at
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.updated_at
     *
     * @param updatedAt
     *         the value for sku.updated_at
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.enabled
     *
     * @return the value of sku.enabled
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.enabled
     *
     * @param enabled
     *         the value for sku.enabled
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.retail_price
     *
     * @return the value of sku.retail_price
     * @mbggenerated
     */
    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.retail_price
     *
     * @param retailPrice
     *         the value for sku.retail_price
     * @mbggenerated
     */
    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.im_price
     *
     * @return the value of sku.im_price
     * @mbggenerated
     */
    public BigDecimal getImPrice() {
        return imPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.im_price
     *
     * @param imPrice
     *         the value for sku.im_price
     * @mbggenerated
     */
    public void setImPrice(BigDecimal imPrice) {
        this.imPrice = imPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.full_modify_date
     *
     * @return the value of sku.full_modify_date
     * @mbggenerated
     */
    public Date getFullModifyDate() {
        return fullModifyDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.full_modify_date
     *
     * @param fullModifyDate
     *         the value for sku.full_modify_date
     * @mbggenerated
     */
    public void setFullModifyDate(Date fullModifyDate) {
        this.fullModifyDate = fullModifyDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.last_check
     *
     * @return the value of sku.last_check
     * @mbggenerated
     */
    public Date getLastCheck() {
        return lastCheck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.last_check
     *
     * @param lastCheck
     *         the value for sku.last_check
     * @mbggenerated
     */
    public void setLastCheck(Date lastCheck) {
        this.lastCheck = lastCheck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.size
     *
     * @return the value of sku.size
     * @mbggenerated
     */
    public String getSize() {
        return size;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.size
     *
     * @param size
     *         the value for sku.size
     * @mbggenerated
     */
    public void setSize(String size) {
        this.size = size == null ? null : size.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.coverpic
     *
     * @return the value of sku.coverpic
     * @mbggenerated
     */
    public String getCoverpic() {
        return coverpic;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.coverpic
     *
     * @param coverpic
     *         the value for sku.coverpic
     * @mbggenerated
     */
    public void setCoverpic(String coverpic) {
        this.coverpic = coverpic == null ? null : coverpic.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sku.introduction
     *
     * @return the value of sku.introduction
     * @mbggenerated
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sku.introduction
     *
     * @param introduction
     *         the value for sku.introduction
     * @mbggenerated
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }
}