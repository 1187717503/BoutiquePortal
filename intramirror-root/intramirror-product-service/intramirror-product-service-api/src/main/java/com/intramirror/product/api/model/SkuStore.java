package com.intramirror.product.api.model;

import java.math.BigDecimal;
import java.util.Date;

public class SkuStore {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.sku_store_id
     *
     * @mbggenerated
     */
    private Long skuStoreId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.sku_id
     *
     * @mbggenerated
     */
    private Long skuId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.product_id
     *
     * @mbggenerated
     */
    private Long productId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.store
     *
     * @mbggenerated
     */
    private Long store;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.remind
     *
     * @mbggenerated
     */
    private Integer remind;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.ordered
     *
     * @mbggenerated
     */
    private Integer ordered;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.confirm
     *
     * @mbggenerated
     */
    private Integer confirm;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.ship
     *
     * @mbggenerated
     */
    private Integer ship;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.finished
     *
     * @mbggenerated
     */
    private Integer finished;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.returned
     *
     * @mbggenerated
     */
    private Integer returned;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.changed
     *
     * @mbggenerated
     */
    private Integer changed;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.clear
     *
     * @mbggenerated
     */
    private Integer clear;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.agree_return_rate
     *
     * @mbggenerated
     */
    private BigDecimal agreeReturnRate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.update_at
     *
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.updated_at
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.enabled
     *
     * @mbggenerated
     */
    private Long reserved;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sku_store.confir_store
     *
     * @mbggenerated
     */
    private Long confirmed;

    public Long getSkuStoreId() {
        return skuStoreId;
    }

    public void setSkuStoreId(Long skuStoreId) {
        this.skuStoreId = skuStoreId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getStore() {
        return store;
    }

    public void setStore(Long store) {
        this.store = store;
    }

    public Integer getRemind() {
        return remind;
    }

    public void setRemind(Integer remind) {
        this.remind = remind;
    }

    public Integer getOrdered() {
        return ordered;
    }

    public void setOrdered(Integer ordered) {
        this.ordered = ordered;
    }

    public Integer getConfirm() {
        return confirm;
    }

    public void setConfirm(Integer confirm) {
        this.confirm = confirm;
    }

    public Integer getShip() {
        return ship;
    }

    public void setShip(Integer ship) {
        this.ship = ship;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public Integer getReturned() {
        return returned;
    }

    public void setReturned(Integer returned) {
        this.returned = returned;
    }

    public Integer getChanged() {
        return changed;
    }

    public void setChanged(Integer changed) {
        this.changed = changed;
    }

    public Integer getClear() {
        return clear;
    }

    public void setClear(Integer clear) {
        this.clear = clear;
    }

    public BigDecimal getAgreeReturnRate() {
        return agreeReturnRate;
    }

    public void setAgreeReturnRate(BigDecimal agreeReturnRate) {
        this.agreeReturnRate = agreeReturnRate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getReserved() {
        return reserved;
    }

    public void setReserved(Long reserved) {
        this.reserved = reserved;
    }

    public Long getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Long confirmed) {
        this.confirmed = confirmed;
    }
}