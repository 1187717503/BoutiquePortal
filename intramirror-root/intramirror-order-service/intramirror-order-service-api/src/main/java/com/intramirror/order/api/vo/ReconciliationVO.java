package com.intramirror.order.api.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by caowei on 2019/2/28.
 */
public class ReconciliationVO {

    private String orderLineNum;
    private String brandName;
    private String designerId;
    private String categoryName;
    private String currency;
    private BigDecimal originalPrice;
    private BigDecimal originalBoutiquePrice;
    private Integer settlementStatus;
    private Long reconciliationStatementId;
    private double discountOff;
    private Integer isDownload;
    private String createDateStart;
    private String createDateEnd;
    private List<Long> vendorIds;

    public List<Long> getVendorIds() {
        return vendorIds;
    }

    public void setVendorIds(List<Long> vendorIds) {
        this.vendorIds = vendorIds;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getOriginalBoutiquePrice() {
        return originalBoutiquePrice;
    }

    public void setOriginalBoutiquePrice(BigDecimal originalBoutiquePrice) {
        this.originalBoutiquePrice = originalBoutiquePrice;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Long getReconciliationStatementId() {
        return reconciliationStatementId;
    }

    public void setReconciliationStatementId(Long reconciliationStatementId) {
        this.reconciliationStatementId = reconciliationStatementId;
    }

    public double getDiscountOff() {
        return discountOff;
    }

    public void setDiscountOff(double discountOff) {
        this.discountOff = discountOff;
    }

    public Integer getIsDownload() {
        return isDownload;
    }

    public void setIsDownload(Integer isDownload) {
        this.isDownload = isDownload;
    }

    public String getCreateDateStart() {
        return createDateStart;
    }

    public void setCreateDateStart(String createDateStart) {
        this.createDateStart = createDateStart;
    }

    public String getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(String createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getOrderLineNum() {
        return orderLineNum;
    }

    public void setOrderLineNum(String orderLineNum) {
        this.orderLineNum = orderLineNum;
    }
}
