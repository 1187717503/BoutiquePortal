package com.intramirror.product.api.model;

import java.util.List;

/**
 * Created on 2017/10/20.
 *
 * @author YouFeng.Zhu
 */
public class SearchCondition {
    private int status;
    private int shopStatus;
    private String[] vendorId;
    private String boutiqueId;
    private String brandId;
    private List<Long> categoryId;
    private String season;
    private String designerId;
    private String colorCode;
    private String image;
    private String modelImage;
    private String streetImage;
    private String stock;
    private int start;
    private int count;
    private String orderBy;
    private String desc;

    @Override
    public String toString() {
        return "SearchCondition{" + "status=" + status + ", shopStatus=" + shopStatus + ", vendorId='" + vendorId + '\'' + ", boutiqueId='" + boutiqueId + '\''
                + ", brandId='" + brandId + '\'' + ", categoryId='" + categoryId + '\'' + ", season='" + season + '\'' + ", designerId='" + designerId + '\''
                + ", colorCode='" + colorCode + '\'' + ", image='" + image + '\'' + ", modelImage='" + modelImage + '\'' + ", streetImage='" + streetImage
                + '\'' + ", stock='" + stock + '\'' + ", start=" + start + ", count=" + count + '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setShopStatus(int shopStatus) {
        this.shopStatus = shopStatus;
    }

    public int getShopStatus() {
        return shopStatus;
    }

    public String[] getVendorId() {
        return vendorId;
    }

    public void setVendorId(String[] vendorId) {
        this.vendorId = vendorId;
    }

    public String getBoutiqueId() {
        return boutiqueId;
    }

    public void setBoutiqueId(String boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public List<Long> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<Long> categoryId) {
        this.categoryId = categoryId;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getModelImage() {
        return modelImage;
    }

    public void setModelImage(String modelImage) {
        this.modelImage = modelImage;
    }

    public String getStreetImage() {
        return streetImage;
    }

    public void setStreetImage(String streetImage) {
        this.streetImage = streetImage;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
