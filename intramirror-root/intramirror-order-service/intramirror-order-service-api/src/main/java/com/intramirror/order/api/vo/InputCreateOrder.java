package com.intramirror.order.api.vo;

import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class InputCreateOrder {

    public String buyerName;
    public String buyerPhone;
    public String buyerWechat;

    public Long geographyId;

    public Map<String, Object> shipToMap;

    public String productId;
    public List<JsonObject> checkoutListStr;

    public BigDecimal totalFee;

    public BigDecimal totalShipFee;

    public BigDecimal subtotalPrice;

    public String shippingFee;
    public String vatFee;
    public String addressCountryId;

    public BigDecimal totalTaxFee;


    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerWechat() {
        return buyerWechat;
    }

    public void setBuyerWechat(String buyerWechat) {
        this.buyerWechat = buyerWechat;
    }

    public Long getGeographyId() {
        return geographyId;
    }

    public void setGeographyId(Long geographyId) {
        this.geographyId = geographyId;
    }

    public Map<String, Object> getShipToMap() {
        return shipToMap;
    }

    public void setShipToMap(Map<String, Object> shipToMap) {
        this.shipToMap = shipToMap;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<JsonObject> getCheckoutListStr() {
        return checkoutListStr;
    }

    public void setCheckoutListStr(List<JsonObject> checkoutListStr) {
        this.checkoutListStr = checkoutListStr;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getTotalShipFee() {
        return totalShipFee;
    }

    public void setTotalShipFee(BigDecimal totalShipFee) {
        this.totalShipFee = totalShipFee;
    }

    public BigDecimal getSubtotalPrice() {
        return subtotalPrice;
    }

    public void setSubtotalPrice(BigDecimal subtotalPrice) {
        this.subtotalPrice = subtotalPrice;
    }

    public String getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(String shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getVatFee() {
        return vatFee;
    }

    public void setVatFee(String vatFee) {
        this.vatFee = vatFee;
    }

    public String getAddressCountryId() {
        return addressCountryId;
    }

    public void setAddressCountryId(String addressCountryId) {
        this.addressCountryId = addressCountryId;
    }
}
