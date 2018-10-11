package com.intramirror.product.api.model;

import java.util.Date;

public class BoutiqueException {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boutique_exception.id
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boutique_exception.product_id
     *
     * @mbggenerated
     */
    private Long productId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boutique_exception.origin_data
     *
     * @mbggenerated
     */
    private String originData;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boutique_exception.target_data
     *
     * @mbggenerated
     */
    private String targetData;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boutique_exception.type
     *
     * @mbggenerated
     */
    private Byte type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boutique_exception.updated_at
     *
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boutique_exception.id
     *
     * @return the value of boutique_exception.id
     *
     * @mbggenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boutique_exception.id
     *
     * @param id the value for boutique_exception.id
     *
     * @mbggenerated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boutique_exception.product_id
     *
     * @return the value of boutique_exception.product_id
     *
     * @mbggenerated
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boutique_exception.product_id
     *
     * @param productId the value for boutique_exception.product_id
     *
     * @mbggenerated
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boutique_exception.origin_data
     *
     * @return the value of boutique_exception.origin_data
     *
     * @mbggenerated
     */
    public String getOriginData() {
        return originData;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boutique_exception.origin_data
     *
     * @param originData the value for boutique_exception.origin_data
     *
     * @mbggenerated
     */
    public void setOriginData(String originData) {
        this.originData = originData == null ? null : originData.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boutique_exception.target_data
     *
     * @return the value of boutique_exception.target_data
     *
     * @mbggenerated
     */
    public String getTargetData() {
        return targetData;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boutique_exception.target_data
     *
     * @param targetData the value for boutique_exception.target_data
     *
     * @mbggenerated
     */
    public void setTargetData(String targetData) {
        this.targetData = targetData == null ? null : targetData.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boutique_exception.type
     *
     * @return the value of boutique_exception.type
     *
     * @mbggenerated
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boutique_exception.type
     *
     * @param type the value for boutique_exception.type
     *
     * @mbggenerated
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boutique_exception.updated_at
     *
     * @return the value of boutique_exception.updated_at
     *
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boutique_exception.updated_at
     *
     * @param updatedAt the value for boutique_exception.updated_at
     *
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}