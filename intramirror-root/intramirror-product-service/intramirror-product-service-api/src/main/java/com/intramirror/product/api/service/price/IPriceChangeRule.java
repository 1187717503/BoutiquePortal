package com.intramirror.product.api.service.price;

/**
 * Created by dingyifan on 2017/7/17.
 */
public interface IPriceChangeRule {
    boolean updateVendorPrice() throws Exception;

    boolean updateShopPrice() throws Exception;

    boolean updateAdminPrice() throws Exception;
}
