package com.intramirror.web.controller.product;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
public enum StateEnum {
    // @formatter:off
    NEW(1),
    PROCESSING(4),
    TRASH(5),
    READY_TO_SELL(3),

    @Deprecated
    OLD_PROCESSING(2),

    SHOP_PROCESSING(4,2),
    SHOP_SOLD_OUT(3,1),
    SHOP_READY_TO_SELL(3,2),
    SHOP_ON_SALE(3,0),
    SHOP_REMOVED(5,2),

    @Deprecated
    OLD_SHOP_PROCESSING(2,2),


    ALL(-1,-1);


// @formatter:on

    private int productStatus;
    private int shopProductStatus;

    StateEnum(int productStatus) {
        this.productStatus = productStatus;
        this.shopProductStatus = -1;
    }

    StateEnum(int productStatus, int shopProductStatus) {
        this.productStatus = productStatus;
        this.shopProductStatus = shopProductStatus;
    }

    public int getProductStatus() {
        return this.productStatus;
    }

    public int getShopProductStatus() {
        return this.shopProductStatus;
    }

    @Override
    public String toString() {
        return "StateEnum{" + "productStatus=" + productStatus + ", shopProductStatus=" + (shopProductStatus == -1 ? null : shopProductStatus) + '}';
    }
}
