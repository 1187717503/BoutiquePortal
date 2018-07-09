package com.intramirror.product.api.entity.promotion;

/**
 * Created on 2018/1/12.
 * @author 123
 */
public class SortPromotion {
    private Long promotionSeqId;
    private Long promotionId;
    private String columnName;
    private Integer sort;
    private Integer seqType;
    private Boolean enabled;
    private String description;

    public Long getPromotionSeqId() {
        return promotionSeqId;
    }

    public void setPromotionSeqId(Long promotionSeqId) {
        this.promotionSeqId = promotionSeqId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSeqType() {
        return seqType;
    }

    public void setSeqType(Integer seqType) {
        this.seqType = seqType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
