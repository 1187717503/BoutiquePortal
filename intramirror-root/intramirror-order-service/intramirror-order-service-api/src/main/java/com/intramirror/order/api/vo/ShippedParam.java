package com.intramirror.order.api.vo;

import java.util.Date;

public class ShippedParam {
    private String orderLineNo;
    private String brandID;
    private String colorCode;
    private Date startDate;
    private Date endDate;
    private String dateType;
    private Integer pageSize;
    private Integer pageNum;
    private String stockLocation;
    private String shipToGeography;
    private String shippedAtOrderBy;
    private String statusOrderBy;

    public String getStockLocation() {
        return stockLocation;
    }

    public void setStockLocation(String stockLocation) {
        this.stockLocation = stockLocation;
    }

    public String getShipToGeography() {
        return shipToGeography;
    }

    public void setShipToGeography(String shipToGeography) {
        this.shipToGeography = shipToGeography;
    }

    public String getShippedAtOrderBy() {
        return shippedAtOrderBy;
    }

    public void setShippedAtOrderBy(String shippedAtOrderBy) {
        this.shippedAtOrderBy = shippedAtOrderBy;
    }

    public String getStatusOrderBy() {
        return statusOrderBy;
    }

    public void setStatusOrderBy(String statusOrderBy) {
        this.statusOrderBy = statusOrderBy;
    }

    public String getOrderLineNo() {
        return orderLineNo;
    }

    public void setOrderLineNo(String orderLineNo) {
        this.orderLineNo = orderLineNo;
    }

    public String getBrandID() {
        return brandID;
    }

    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
