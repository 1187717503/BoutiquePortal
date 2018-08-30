package com.intramirror.product.api.vo.tag;

import com.intramirror.product.api.model.Tag;

import java.util.List;

/**
 * Created by juzhongzheng on 2018/6/4.
 */
public class VendorTagVO {
    private Long vendorId;
    private String vendorName;
    private List<Tag> tags;
    private List<Long> tagIds;

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
