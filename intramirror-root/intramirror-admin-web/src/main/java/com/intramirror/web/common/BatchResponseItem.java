package com.intramirror.web.common;

/**
 * Created on 2017/11/1.
 *
 * @author YouFeng.Zhu
 */
public class BatchResponseItem {
    Long productId;
    Long shopProductId;
    String message;

    public BatchResponseItem(Long productId, Long shopProductId) {
        this.productId = productId;
        this.shopProductId = shopProductId;
    }

    public BatchResponseItem(Long productId, Long shopProductId, String message) {

        this.productId = productId;
        this.shopProductId = shopProductId;
        this.message = message;
    }

    public Long getProductId() {

        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getShopProductId() {
        return shopProductId;
    }

    public void setShopProductId(Long shopProductId) {
        this.shopProductId = shopProductId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
