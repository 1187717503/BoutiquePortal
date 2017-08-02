package com.intramirror.product.api.vo.sku;


/**
 * Created by dingyifan on 2017/8/2.
 * 这个是提供给接口用的sku vo,用于创建商品，修改商品，或者修改库存
 */
public class SkuOptions{
    public String size;

    public String stock;

    public String barcodes;

    public String sizeid; // filippo

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(String barcodes) {
        this.barcodes = barcodes;
    }

    public String getSizeid() {
        return sizeid;
    }

    public void setSizeid(String sizeid) {
        this.sizeid = sizeid;
    }
}
