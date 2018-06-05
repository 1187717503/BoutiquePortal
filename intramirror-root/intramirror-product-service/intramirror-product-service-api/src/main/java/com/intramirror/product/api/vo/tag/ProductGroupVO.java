package com.intramirror.product.api.vo.tag;

import com.intramirror.product.api.model.Tag;

import java.util.List;

/**
 * Created by juzhongzheng on 2018/6/4.
 */
public class ProductGroupVO {
    private Tag hot;// 爆款
    private List<VendorTagVO> vendorTagVOs;

    public Tag getHot() {
        return hot;
    }

    public void setHot(Tag hot) {
        this.hot = hot;
    }

    public List<VendorTagVO> getVendorTagVOs() {
        return vendorTagVOs;
    }

    public void setVendorTagVOs(List<VendorTagVO> vendorTagVOs) {
        this.vendorTagVOs = vendorTagVOs;
    }
}
