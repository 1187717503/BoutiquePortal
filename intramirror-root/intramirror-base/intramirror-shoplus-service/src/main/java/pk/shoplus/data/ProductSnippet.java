package pk.shoplus.data;

import pk.shoplus.model.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chone on 2017/4/12.
 */
public class ProductSnippet {


    private String title;


    private ThumbnailDetails thumbnails;


    private String categoryId;


    private String categoryTitle;


    private String parentCategoryTitle;


    private String ancestorCategoryTitle;


    private String brandId;


    private String brandTitle;


    private String vendorId;


    private String description;


    private Date publishedAt;


    private Date updatedAt;


    private BigDecimal retailPrice;


    private BigDecimal price;


    private BigDecimal salePrice;


    private BigDecimal supplyPrice;


    private BigDecimal stock;


    private BigDecimal discount;


    private String remark;


    private Date createdAt;


    private Date validAt;


    private int feature;


    public BigDecimal getSupplyPrice() {
        return supplyPrice;
    }


    public ProductSnippet setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
        return this;
    }


    public String getBrandTitle() {
        return brandTitle;
    }


    public ProductSnippet setBrandTitle(String brandTitle) {
        this.brandTitle = brandTitle;
        return this;
    }


    public String getCategoryTitle() {
        return categoryTitle;
    }


    public ProductSnippet setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
        return this;
    }


    public String getParentCategoryTitle() {
        return parentCategoryTitle;
    }


    public ProductSnippet setParentCategoryTitle(String parentCategoryTitle) {
        this.parentCategoryTitle = parentCategoryTitle;
        return this;
    }


    public String getAncestorCategoryTitle() {
        return ancestorCategoryTitle;
    }


    public ProductSnippet setAncestorCategoryTitle(String ancestorCategoryTitle) {
        this.ancestorCategoryTitle = ancestorCategoryTitle;
        return this;
    }


    public BigDecimal getDiscount() {
        return discount;
    }


    public ProductSnippet setDiscount(BigDecimal discount) {
        this.discount = discount;
        return this;
    }


    public BigDecimal getStock() {
        return stock;
    }


    public ProductSnippet setStock(BigDecimal stock) {
        this.stock = stock;
        return this;
    }


    public BigDecimal getSalePrice() {
        return salePrice;
    }


    public ProductSnippet setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
        return this;
    }


    public BigDecimal getPrice() {
        return price;
    }


    public ProductSnippet setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }


    public BigDecimal getRetailPrice() {
        return retailPrice;
    }


    public ProductSnippet setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
        return this;
    }


    public int getFeature() {
        return feature;
    }


    public ProductSnippet setFeature(int feature) 
            throws ProductSnippet.InvalidFeatureException {
        if (feature == ProductFeature.FEATURED ||
                feature == ProductFeature.NORMAL ||
                feature == ProductFeature.HOT) {
            this.feature = feature;
        } else {
            throw new ProductSnippet.InvalidFeatureException();
        }
        return this;
    }


    public Date getValidAt() {
        return validAt;
    }


    public ProductSnippet setValidAt(Date validAt) {
        this.validAt = validAt;
        return this;
    }


    public Date getCreatedAt() {
        return createdAt;
    }


    public ProductSnippet setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }


    public String getRemark() {
        return remark;
    }


    public ProductSnippet setRemark(String remark){
        this.remark = remark;
        return this;
    }


    public String getVendorId() {
        return vendorId;
    }


    public ProductSnippet setVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }


    public ProductSnippet setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }


    public Date getPublishedAt() {
        return publishedAt;
    }


    public ProductSnippet setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }


    public String getDescription() {
        return description;
    }


    public ProductSnippet setDescription(String description) {
        this.description = description;
        return this;
    }


    public String getBrandId() {
        return brandId;
    }


    public ProductSnippet setBrandId(String brandId) {
        this.brandId = brandId;
        return this;
    }


    public String getCategoryId() {
        return categoryId;
    }


    public ProductSnippet setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }


    public ThumbnailDetails getThumbnails() {
        return thumbnails;
    }


    public ProductSnippet setThumbnails(ThumbnailDetails thumbnails) {
        this.thumbnails = thumbnails;
        return this;
    }


    public String getTitle() {
        return title;
    }


    public ProductSnippet setTitle(String title) {
        this.title = title;
        return this;
    }



    public class InvalidFeatureException extends Exception {

    }


}
