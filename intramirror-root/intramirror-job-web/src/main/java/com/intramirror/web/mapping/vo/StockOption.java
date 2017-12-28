package com.intramirror.web.mapping.vo;

import java.util.Date;

/**
 * Created by dingyifan on 2017/9/14.
 * 定时job接口修改库存定义的VO
 */
public class StockOption {
    public String productCode; // 商品code
    public String sku_code;
    public String sizeValue; // sku size
    public String quantity; // 库存
    public String vendor_id; // vendor
    public String price; // 价格
    public String type; // 库存类型
    public Date last_check; // 消息时间
    public boolean modify = false;
    public String updated_by; // 根据product_code,size修改,根据sku_code修改
    public String boutique_sku_id;

    public Date getLast_check() {
        return last_check;
    }

    public void setLast_check(Date last_check) {
        this.last_check = last_check;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSizeValue() {
        return sizeValue;
    }

    public void setSizeValue(String sizeValue) {
        this.sizeValue = sizeValue;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSku_code() {
        return sku_code;
    }

    public void setSku_code(String sku_code) {
        this.sku_code = sku_code;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public boolean isModify() {
        return modify;
    }

    public void setModify(boolean modify) {
        this.modify = modify;
    }

    public String getBoutique_sku_id() {
        return boutique_sku_id;
    }

    public void setBoutique_sku_id(String boutique_sku_id) {
        this.boutique_sku_id = boutique_sku_id;
    }
}
