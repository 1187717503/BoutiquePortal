package pk.shoplus.enums;

/**
 * Created by dingyifan on 2017/6/1.
 */
public enum ProductStatusEnum {
    // vendor
    NEW_PENDING(1,"new_pending"),
    NEW_REJECTED(5,"new_rejected"),
    MODIFY_REJECT(6,"modify_reject"),
    MODIFY_PENDING(2,"modify_pending"),

    // admin
    ADMIN_APPROVED(3,"admin_approved"),
    ADMIN_OFF_SALE(4,"admin_off_sale"),

    // shop
    ADMIN_SHOP_OFF_SALE(4,2,"admin_shop_off_sale"),
    SHOP_OFF_SALE(3,2,"shop_off_sale"),
    SHOP_ON_SALE(3,0,"shop_on_sale"),
    SHOP_SOLD_OUT(4,1,"shop_sold_out"),
    SHOP_REMOVED(101,"shop_removed")
    ;
    private int productStatus = -1;
    private int shopProductStatus = -1;
    private String code = null;

    public int getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public int getShopProductStatus() {
        return shopProductStatus;
    }

    public void setShopProductStatus(int shopProductStatus) {
        this.shopProductStatus = shopProductStatus;
    }

    public String getCode(){return code;}

    public void setCode(){this.code = code;}

    ProductStatusEnum(int productStatus, String code) {
        this.productStatus = productStatus;
        this.code = code;
    }

    ProductStatusEnum(int productStatus, int shopProductStatus, String code) {
        this.productStatus = productStatus;
        this.shopProductStatus = shopProductStatus;
        this.code = code;
    }

}
