package pk.shoplus.data;

import java.math.BigDecimal;

/**
 * Created by chone on 2017/4/13.
 */
public class ProductContentRating {


    private BigDecimal customerRating;


    private BigDecimal shopRating;


    public BigDecimal getCustomerRating() {
        return customerRating;
    }


    public ProductContentRating setCustomerRating(BigDecimal customerRating) {
        this.customerRating = customerRating;
        return this;
    }


    public BigDecimal getShopRating() {
        return shopRating;
    }


    public ProductContentRating setShopRating(BigDecimal shopRating) {
        this.shopRating = shopRating;
        return this;
    }


}
