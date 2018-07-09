package com.intramirror.web.common.request;

import com.intramirror.product.api.model.Block;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.model.TagProductRel;
import java.util.List;

/**
 * Created on 2017/11/21.
 * @author YouFeng.Zhu
 */
public class Content {
    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public List<TagProductRel> getSort() {
        return sort;
    }

    public void setSort(List<TagProductRel> sort) {
        this.sort = sort;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    private Block block;
    private Tag tag;
    private List<TagProductRel> sort;
    private String isNew;
}


