package com.intramirror.product.api.model;

/**
 * Created on 2017/11/15.
 *
 * @author YouFeng.Zhu
 */
public class ContentAdditionalCondition {

    private Integer minBoutiqueDiscount;
    private Integer maxBoutiqueDiscount;
    private Integer minIMDiscount;
    private Integer maxIMDiscount;
    private Long minStock;
    private Long maxStock;
    private String tag;

    public Integer getMinBoutiqueDiscount() {
        return minBoutiqueDiscount;
    }

    public void setMinBoutiqueDiscount(Integer minBoutiqueDiscount) {
        this.minBoutiqueDiscount = minBoutiqueDiscount;
    }

    public Integer getMaxBoutiqueDiscount() {
        return maxBoutiqueDiscount;
    }

    public void setMaxBoutiqueDiscount(Integer maxBoutiqueDiscount) {
        this.maxBoutiqueDiscount = maxBoutiqueDiscount;
    }

    public Integer getMinIMDiscount() {
        return minIMDiscount;
    }

    public void setMinIMDiscount(Integer minIMDiscount) {
        this.minIMDiscount = minIMDiscount;
    }

    public Integer getMaxIMDiscount() {
        return maxIMDiscount;
    }

    public void setMaxIMDiscount(Integer maxIMDiscount) {
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

}
