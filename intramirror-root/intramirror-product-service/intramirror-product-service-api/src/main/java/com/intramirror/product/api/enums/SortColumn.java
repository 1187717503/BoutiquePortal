package com.intramirror.product.api.enums;

/**
 * Created on 2018/1/12.
 * @author 123
 */
public enum SortColumn {
    VENDOR("vendor_id"), CATEGORY("category_id"), BRAND("brand_id"), DISCOUNT("discount"), SEASON("season");

    private String value;

    SortColumn(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static SortColumn fromString(String text) {
        for (SortColumn b : SortColumn.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
