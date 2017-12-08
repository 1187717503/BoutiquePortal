package com.intramirror.product.api.model;

import java.util.List;

/**
 * Created on 2017/10/20.
 *
 * @author YouFeng.Zhu
 */
public class SearchCondition {
    private Integer productStatus;
    private Integer shopProductStatus;
    private Long[] vendorId;
    private String boutiqueId;
    private Long brandId;
    private List<Long> categoryId;
    private String season;
    private String designerId;
    private String colorCode;
    private String image;
    private String modelImage;
    private String streetImage;
    private String stock;
    private Integer start;
    private Integer count;
    private String orderBy;
    private String desc;

    private String exception;

    //content page

    private Float minBoutiqueDiscount;
    private Float maxBoutiqueDiscount;
    private Float minIMDiscount;
    private Float maxIMDiscount;
    private Long minStock;
    private Long maxStock;
    private Float minPrice;
    private Float maxPrice;
    private Long saleAtFrom;
    private Long saleAtTo;
    private Long tagId;
    private List<Long> productIds;

    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    public void setShopProductStatus(Integer shopProductStatus) {
        this.shopProductStatus = shopProductStatus;
    }

    public Integer getShopProductStatus() {
        return shopProductStatus;
    }

    public Long[] getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long[] vendorId) {
        this.vendorId = vendorId;
    }

    public String getBoutiqueId() {
        return boutiqueId;
    }

    public void setBoutiqueId(String boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
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

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Float getMinBoutiqueDiscount() {
        return minBoutiqueDiscount;
    }

    public void setMinBoutiqueDiscount(Float minBoutiqueDiscount) {
        this.minBoutiqueDiscount = minBoutiqueDiscount;
    }

    public Float getMaxBoutiqueDiscount() {
        return maxBoutiqueDiscount;
    }

    public void setMaxBoutiqueDiscount(Float maxBoutiqueDiscount) {
        this.maxBoutiqueDiscount = maxBoutiqueDiscount;
    }

    public Float getMinIMDiscount() {
        return minIMDiscount;
    }

    public void setMinIMDiscount(Float minIMDiscount) {
        this.minIMDiscount = minIMDiscount;
    }

    public Float getMaxIMDiscount() {
        return maxIMDiscount;
    }

    public void setMaxIMDiscount(Float maxIMDiscount) {
        this.maxIMDiscount = maxIMDiscount;
    }

    public Long getMinStock() {
        return minStock;
    }

    public void setMinStock(Long minStock) {
        this.minStock = minStock;
    }

    public Long getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Long maxStock) {
        this.maxStock = maxStock;
    }

    public Long getSaleAtFrom() {
        return saleAtFrom;
    }

    public void setSaleAtFrom(Long saleAtFrom) {
        this.saleAtFrom = saleAtFrom;
    }

    public Long getSaleAtTo() {
        return saleAtTo;
    }

    public void setSaleAtTo(Long saleAtTo) {
        this.saleAtTo = saleAtTo;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

}
