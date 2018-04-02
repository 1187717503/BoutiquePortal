package com.intramirror.product.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 123 on 2018/3/22.
 */
public enum CategoryTypeEnum {

    ADULT("1", Arrays.asList(1499L, 1568L), Arrays.asList(1504L, 1505L, 1506L, 1507L, 1569L, 1584L, 1598L, 1608L)),
    KIDS("2", Arrays.asList(1757L, 1758L, 1759L), Arrays.asList(1760L, 1761L, 1762L, 1763L, 1764L, 1765L, 1766L, 1767L, 1768L));

    private String categoryType;
    private List<Long> firstCategoryIds;
    private List<Long> secondCategoryIds;

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public List<Long> getFirstCategoryIds() {
        return firstCategoryIds;
    }

    public void setFirstCategoryIds(List<Long> firstCategoryIds) {
        firstCategoryIds = firstCategoryIds;
    }

    public List<Long> getSecondCategoryIds() {
        return secondCategoryIds;
    }

    public void setSecondCategoryIds(List<Long> secondCategoryIds) {
        secondCategoryIds = secondCategoryIds;
    }

    private CategoryTypeEnum(String categoryType, List<Long> firstCategoryIds, List<Long> secondCategoryIds) {
        this.categoryType = categoryType;
        this.firstCategoryIds = firstCategoryIds;
        this.secondCategoryIds = secondCategoryIds;
    }

    public static CategoryTypeEnum getCategoryByKey(String categoryType) {
        if (categoryType == null) {
            return null;
        }
        for (CategoryTypeEnum categoryTypeEnum : CategoryTypeEnum.values()) {
            if (categoryTypeEnum.getCategoryType().equals(categoryType)) {
                return categoryTypeEnum;
            }
        }
        return null;
    }
}
