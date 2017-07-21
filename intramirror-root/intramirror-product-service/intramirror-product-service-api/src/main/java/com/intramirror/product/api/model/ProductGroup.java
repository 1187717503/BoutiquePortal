package com.intramirror.product.api.model;

import java.util.Date;

public class ProductGroup {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_group.product_group_id
     *
     * @mbggenerated
     */
    private Long productGroupId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_group.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_group.group_type
     *
     * @mbggenerated
     */
    private Integer groupType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_group.vendor_id
     *
     * @mbggenerated
     */
    private Long vendorId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_group.shop_id
     *
     * @mbggenerated
     */
    private Long shopId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_group.user_id
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_group.valid_from
     *
     * @mbggenerated
     */
    private Date validFrom;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_group.product_group_id
     *
     * @return the value of product_group.product_group_id
     *
     * @mbggenerated
     */
    public Long getProductGroupId() {
        return productGroupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_group.product_group_id
     *
     * @param productGroupId the value for product_group.product_group_id
     *
     * @mbggenerated
     */
    public void setProductGroupId(Long productGroupId) {
        this.productGroupId = productGroupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_group.name
     *
     * @return the value of product_group.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_group.name
     *
     * @param name the value for product_group.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_group.group_type
     *
     * @return the value of product_group.group_type
     *
     * @mbggenerated
     */
    public Integer getGroupType() {
        return groupType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_group.group_type
     *
     * @param groupType the value for product_group.group_type
     *
     * @mbggenerated
     */
    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_group.vendor_id
     *
     * @return the value of product_group.vendor_id
     *
     * @mbggenerated
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_group.vendor_id
     *
     * @param vendorId the value for product_group.vendor_id
     *
     * @mbggenerated
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_group.shop_id
     *
     * @return the value of product_group.shop_id
     *
     * @mbggenerated
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_group.shop_id
     *
     * @param shopId the value for product_group.shop_id
     *
     * @mbggenerated
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_group.user_id
     *
     * @return the value of product_group.user_id
     *
     * @mbggenerated
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_group.user_id
     *
     * @param userId the value for product_group.user_id
     *
     * @mbggenerated
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_group.valid_from
     *
     * @return the value of product_group.valid_from
     *
     * @mbggenerated
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_group.valid_from
     *
     * @param validFrom the value for product_group.valid_from
     *
     * @mbggenerated
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
}