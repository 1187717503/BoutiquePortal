package com.intramirror.product.api.model;

/**
 * Created on 2017/11/15.
 *
 * @author YouFeng.Zhu
 */
public class ContentAdditionalCondition {

    private Float minBoutiqueDiscount;
    private Float maxBoutiqueDiscount;
    private Float minIMDiscount;
    private Float maxIMDiscount;
    private Long minStock;
    private Long maxStock;
    private Long saleAtFrom;
    private Long saleAtTo;
    private String tag;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    @Override
    public String toString() {
        return "ContentAdditionalCondition{" + "minBoutiqueDiscount=" + minBoutiqueDiscount + ", maxBoutiqueDiscount=" + maxBoutiqueDiscount
                + ", minIMDiscount=" + minIMDiscount + ", maxIMDiscount=" + maxIMDiscount + ", minStock=" + minStock + ", maxStock=" + maxStock
                + ", saleAtFrom=" + saleAtFrom + ", saleAtTo=" + saleAtTo + ", tag='" + tag + '\'' + '}';
    }
}
