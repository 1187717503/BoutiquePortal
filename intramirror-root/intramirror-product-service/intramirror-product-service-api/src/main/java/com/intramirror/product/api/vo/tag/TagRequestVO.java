package com.intramirror.product.api.vo.tag;

/**
 * Created by juzhongzheng on 2018/6/4.
 */
public class TagRequestVO {
    private Long tagId;
    private Long vendorId;
    private Integer tagType; // tag类型 1: tag 2:product group 3:爆款（im product group)
    private String tagName;
    private String orderBy = "2"; // 1: tagName desc 2: create time desc

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
