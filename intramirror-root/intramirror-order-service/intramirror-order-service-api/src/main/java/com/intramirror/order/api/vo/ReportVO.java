package com.intramirror.order.api.vo;

import java.math.BigDecimal;

public class ReportVO {
    private Long skuId;
    private String converpic;
    private String designerId;
    private String colorCode;
    private BigDecimal retailPrice; // min_retail_price
    private BigDecimal boutiquePrice; // min_boutique_price
    private Integer boutiqueDiscount;
    private String size;
    private String categoryName;
    private String brandName;
    private String seasonCode;
    private Integer stock;
    private String boutiqueId; // product->productCode
    private String boutique; // 买手店名称 vendor_name

    public String getConverpic() {
        return converpic;
    }

    public void setConverpic(String converpic) {
        this.converpic = converpic;
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

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSeasonCode() {
        return seasonCode;
    }

    public void setSeasonCode(String seasonCode) {
        this.seasonCode = seasonCode;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getBoutiquePrice() {
        return boutiquePrice;
    }

    public void setBoutiquePrice(BigDecimal boutiquePrice) {
        this.boutiquePrice = boutiquePrice;
    }

    public String getBoutiqueId() {
        return boutiqueId;
    }

    public void setBoutiqueId(String boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public Integer getBoutiqueDiscount() {
        return boutiqueDiscount;
    }

    public void setBoutiqueDiscount(Integer boutiqueDiscount) {
        this.boutiqueDiscount = boutiqueDiscount;
    }

    public String getBoutique() {
        return boutique;
    }

    public void setBoutique(String boutique) {
        this.boutique = boutique;
    }
}
