package pk.shoplus.vo;

/**
 * Created by dingyifan on 2017/6/1.
 */
public class StatusMachineVO {

    // required
    private Long productId = null;

    // admin approved -> shop off sale requied!!!
    private Long userId = null;

    // new pending -> new Rejected
    private String rejectedReason = null;

    private Long shopProductId = null;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public Long getShopProductId() {
        return shopProductId;
    }

    public void setShopProductId(Long shopProductId) {
        this.shopProductId = shopProductId;
    }
}
