package com.intramirror.product.api.entity.promotion;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public class CategoryEntity {
    private Long categoryId;
    private String name;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
