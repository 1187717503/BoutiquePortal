package com.intramirror.web.controller.product;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
public enum StateEnum {
    //1-NEW_PENDING,2-MODIFY_PENDING,3-ON_SALE,4-OFF,5-NEW_REJECTED,6-MODIFY_REJECTED,7-WAITING_SALE,8-UNAVAILABLE
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

    ALL(-1,-1);


// @formatter:on

    private int productStatus;
    private int shopProductStatus;
    private static Set<StateEnum> stateEnumSet = new HashSet<>();

    static {

        for (StateEnum e : StateEnum.values()) {
            stateEnumSet.add(e);
        }
    }

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
