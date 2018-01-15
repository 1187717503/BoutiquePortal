package com.intramirror.product.api.entity.promotion;

/**
 * Created on 2018/1/15.
 * @author 123
 */
public class CategorySort {
    private Long promotionCategoryId;
    private Long promotionId;
    private Long categoryId;
    private String name;
    private Integer sort;
    private String chineseName;
    private Long parentId;
    private Integer level;
    private Long showCodeInt;
    private String coverImg;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getPromotionCategoryId() {
        return promotionCategoryId;
    }

    public void setPromotionCategoryId(Long promotionCategoryId) {
        this.promotionCategoryId = promotionCategoryId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getShowCodeInt() {
        return showCodeInt;
    }

    public void setShowCodeInt(Long showCodeInt) {
        this.showCodeInt = showCodeInt;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }
}
