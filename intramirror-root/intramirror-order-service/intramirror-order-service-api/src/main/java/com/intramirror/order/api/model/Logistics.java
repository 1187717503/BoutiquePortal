package com.intramirror.order.api.model;

import java.math.BigDecimal;
import java.util.Date;

public class Logistics {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.logistics_id
     *
     * @mbggenerated
     */
    private Long logisticsId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.company
     *
     * @mbggenerated
     */
    private String company;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.order_logistics_id
     *
     * @mbggenerated
     */
    private Long orderLogisticsId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.user_rec_name
     *
     * @mbggenerated
     */
    private String userRecName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.user_rec_province
     *
     * @mbggenerated
     */
    private String userRecProvince;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.user_rec_city
     *
     * @mbggenerated
     */
    private String userRecCity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.user_rec_area
     *
     * @mbggenerated
     */
    private String userRecArea;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.user_rec_addr
     *
     * @mbggenerated
     */
    private String userRecAddr;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.user_rec_mobile
     *
     * @mbggenerated
     */
    private String userRecMobile;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.rec_name
     *
     * @mbggenerated
     */
    private String recName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.rec_province
     *
     * @mbggenerated
     */
    private String recProvince;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.rec_city
     *
     * @mbggenerated
     */
    private String recCity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.rec_area
     *
     * @mbggenerated
     */
    private String recArea;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.rec_addr
     *
     * @mbggenerated
     */
    private String recAddr;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.rec_mobile
     *
     * @mbggenerated
     */
    private String recMobile;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.logistics_fee
     *
     * @mbggenerated
     */
    private BigDecimal logisticsFee;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.weight
     *
     * @mbggenerated
     */
    private Float weight;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.begin_time
     *
     * @mbggenerated
     */
    private Date beginTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.receive_time
     *
     * @mbggenerated
     */
    private Date receiveTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.insure_fee
     *
     * @mbggenerated
     */
    private BigDecimal insureFee;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.logistics_staff
     *
     * @mbggenerated
     */
    private String logisticsStaff;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.logistics_url
     *
     * @mbggenerated
     */
    private String logisticsUrl;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.status
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.updated_at
     *
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.geography_id
     *
     * @mbggenerated
     */
    private Long geographyId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.user_rec_country
     *
     * @mbggenerated
     */
    private String userRecCountry;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column logistics.user_rec_code
     *
     * @mbggenerated
     */
    private String userRecCode;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.logistics_id
     *
     * @return the value of logistics.logistics_id
     *
     * @mbggenerated
     */
    public Long getLogisticsId() {
        return logisticsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.logistics_id
     *
     * @param logisticsId the value for logistics.logistics_id
     *
     * @mbggenerated
     */
    public void setLogisticsId(Long logisticsId) {
        this.logisticsId = logisticsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.company
     *
     * @return the value of logistics.company
     *
     * @mbggenerated
     */
    public String getCompany() {
        return company;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.company
     *
     * @param company the value for logistics.company
     *
     * @mbggenerated
     */
    public void setCompany(String company) {
        this.company = company == null ? null : company.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.order_logistics_id
     *
     * @return the value of logistics.order_logistics_id
     *
     * @mbggenerated
     */
    public Long getOrderLogisticsId() {
        return orderLogisticsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.order_logistics_id
     *
     * @param orderLogisticsId the value for logistics.order_logistics_id
     *
     * @mbggenerated
     */
    public void setOrderLogisticsId(Long orderLogisticsId) {
        this.orderLogisticsId = orderLogisticsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.user_rec_name
     *
     * @return the value of logistics.user_rec_name
     *
     * @mbggenerated
     */
    public String getUserRecName() {
        return userRecName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.user_rec_name
     *
     * @param userRecName the value for logistics.user_rec_name
     *
     * @mbggenerated
     */
    public void setUserRecName(String userRecName) {
        this.userRecName = userRecName == null ? null : userRecName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.user_rec_province
     *
     * @return the value of logistics.user_rec_province
     *
     * @mbggenerated
     */
    public String getUserRecProvince() {
        return userRecProvince;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.user_rec_province
     *
     * @param userRecProvince the value for logistics.user_rec_province
     *
     * @mbggenerated
     */
    public void setUserRecProvince(String userRecProvince) {
        this.userRecProvince = userRecProvince == null ? null : userRecProvince.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.user_rec_city
     *
     * @return the value of logistics.user_rec_city
     *
     * @mbggenerated
     */
    public String getUserRecCity() {
        return userRecCity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.user_rec_city
     *
     * @param userRecCity the value for logistics.user_rec_city
     *
     * @mbggenerated
     */
    public void setUserRecCity(String userRecCity) {
        this.userRecCity = userRecCity == null ? null : userRecCity.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.user_rec_area
     *
     * @return the value of logistics.user_rec_area
     *
     * @mbggenerated
     */
    public String getUserRecArea() {
        return userRecArea;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.user_rec_area
     *
     * @param userRecArea the value for logistics.user_rec_area
     *
     * @mbggenerated
     */
    public void setUserRecArea(String userRecArea) {
        this.userRecArea = userRecArea == null ? null : userRecArea.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.user_rec_addr
     *
     * @return the value of logistics.user_rec_addr
     *
     * @mbggenerated
     */
    public String getUserRecAddr() {
        return userRecAddr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.user_rec_addr
     *
     * @param userRecAddr the value for logistics.user_rec_addr
     *
     * @mbggenerated
     */
    public void setUserRecAddr(String userRecAddr) {
        this.userRecAddr = userRecAddr == null ? null : userRecAddr.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.user_rec_mobile
     *
     * @return the value of logistics.user_rec_mobile
     *
     * @mbggenerated
     */
    public String getUserRecMobile() {
        return userRecMobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.user_rec_mobile
     *
     * @param userRecMobile the value for logistics.user_rec_mobile
     *
     * @mbggenerated
     */
    public void setUserRecMobile(String userRecMobile) {
        this.userRecMobile = userRecMobile == null ? null : userRecMobile.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.rec_name
     *
     * @return the value of logistics.rec_name
     *
     * @mbggenerated
     */
    public String getRecName() {
        return recName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.rec_name
     *
     * @param recName the value for logistics.rec_name
     *
     * @mbggenerated
     */
    public void setRecName(String recName) {
        this.recName = recName == null ? null : recName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.rec_province
     *
     * @return the value of logistics.rec_province
     *
     * @mbggenerated
     */
    public String getRecProvince() {
        return recProvince;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.rec_province
     *
     * @param recProvince the value for logistics.rec_province
     *
     * @mbggenerated
     */
    public void setRecProvince(String recProvince) {
        this.recProvince = recProvince == null ? null : recProvince.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.rec_city
     *
     * @return the value of logistics.rec_city
     *
     * @mbggenerated
     */
    public String getRecCity() {
        return recCity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.rec_city
     *
     * @param recCity the value for logistics.rec_city
     *
     * @mbggenerated
     */
    public void setRecCity(String recCity) {
        this.recCity = recCity == null ? null : recCity.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.rec_area
     *
     * @return the value of logistics.rec_area
     *
     * @mbggenerated
     */
    public String getRecArea() {
        return recArea;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.rec_area
     *
     * @param recArea the value for logistics.rec_area
     *
     * @mbggenerated
     */
    public void setRecArea(String recArea) {
        this.recArea = recArea == null ? null : recArea.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.rec_addr
     *
     * @return the value of logistics.rec_addr
     *
     * @mbggenerated
     */
    public String getRecAddr() {
        return recAddr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.rec_addr
     *
     * @param recAddr the value for logistics.rec_addr
     *
     * @mbggenerated
     */
    public void setRecAddr(String recAddr) {
        this.recAddr = recAddr == null ? null : recAddr.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.rec_mobile
     *
     * @return the value of logistics.rec_mobile
     *
     * @mbggenerated
     */
    public String getRecMobile() {
        return recMobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.rec_mobile
     *
     * @param recMobile the value for logistics.rec_mobile
     *
     * @mbggenerated
     */
    public void setRecMobile(String recMobile) {
        this.recMobile = recMobile == null ? null : recMobile.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.logistics_fee
     *
     * @return the value of logistics.logistics_fee
     *
     * @mbggenerated
     */
    public BigDecimal getLogisticsFee() {
        return logisticsFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.logistics_fee
     *
     * @param logisticsFee the value for logistics.logistics_fee
     *
     * @mbggenerated
     */
    public void setLogisticsFee(BigDecimal logisticsFee) {
        this.logisticsFee = logisticsFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.weight
     *
     * @return the value of logistics.weight
     *
     * @mbggenerated
     */
    public Float getWeight() {
        return weight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.weight
     *
     * @param weight the value for logistics.weight
     *
     * @mbggenerated
     */
    public void setWeight(Float weight) {
        this.weight = weight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.begin_time
     *
     * @return the value of logistics.begin_time
     *
     * @mbggenerated
     */
    public Date getBeginTime() {
        return beginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.begin_time
     *
     * @param beginTime the value for logistics.begin_time
     *
     * @mbggenerated
     */
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.receive_time
     *
     * @return the value of logistics.receive_time
     *
     * @mbggenerated
     */
    public Date getReceiveTime() {
        return receiveTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.receive_time
     *
     * @param receiveTime the value for logistics.receive_time
     *
     * @mbggenerated
     */
    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.insure_fee
     *
     * @return the value of logistics.insure_fee
     *
     * @mbggenerated
     */
    public BigDecimal getInsureFee() {
        return insureFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.insure_fee
     *
     * @param insureFee the value for logistics.insure_fee
     *
     * @mbggenerated
     */
    public void setInsureFee(BigDecimal insureFee) {
        this.insureFee = insureFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.logistics_staff
     *
     * @return the value of logistics.logistics_staff
     *
     * @mbggenerated
     */
    public String getLogisticsStaff() {
        return logisticsStaff;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.logistics_staff
     *
     * @param logisticsStaff the value for logistics.logistics_staff
     *
     * @mbggenerated
     */
    public void setLogisticsStaff(String logisticsStaff) {
        this.logisticsStaff = logisticsStaff == null ? null : logisticsStaff.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.logistics_url
     *
     * @return the value of logistics.logistics_url
     *
     * @mbggenerated
     */
    public String getLogisticsUrl() {
        return logisticsUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.logistics_url
     *
     * @param logisticsUrl the value for logistics.logistics_url
     *
     * @mbggenerated
     */
    public void setLogisticsUrl(String logisticsUrl) {
        this.logisticsUrl = logisticsUrl == null ? null : logisticsUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.status
     *
     * @return the value of logistics.status
     *
     * @mbggenerated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.status
     *
     * @param status the value for logistics.status
     *
     * @mbggenerated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.created_at
     *
     * @return the value of logistics.created_at
     *
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.created_at
     *
     * @param createdAt the value for logistics.created_at
     *
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.updated_at
     *
     * @return the value of logistics.updated_at
     *
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.updated_at
     *
     * @param updatedAt the value for logistics.updated_at
     *
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.enabled
     *
     * @return the value of logistics.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.enabled
     *
     * @param enabled the value for logistics.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.geography_id
     *
     * @return the value of logistics.geography_id
     *
     * @mbggenerated
     */
    public Long getGeographyId() {
        return geographyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.geography_id
     *
     * @param geographyId the value for logistics.geography_id
     *
     * @mbggenerated
     */
    public void setGeographyId(Long geographyId) {
        this.geographyId = geographyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.user_rec_country
     *
     * @return the value of logistics.user_rec_country
     *
     * @mbggenerated
     */
    public String getUserRecCountry() {
        return userRecCountry;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.user_rec_country
     *
     * @param userRecCountry the value for logistics.user_rec_country
     *
     * @mbggenerated
     */
    public void setUserRecCountry(String userRecCountry) {
        this.userRecCountry = userRecCountry == null ? null : userRecCountry.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column logistics.user_rec_code
     *
     * @return the value of logistics.user_rec_code
     *
     * @mbggenerated
     */
    public String getUserRecCode() {
        return userRecCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column logistics.user_rec_code
     *
     * @param userRecCode the value for logistics.user_rec_code
     *
     * @mbggenerated
     */
    public void setUserRecCode(String userRecCode) {
        this.userRecCode = userRecCode == null ? null : userRecCode.trim();
    }
}