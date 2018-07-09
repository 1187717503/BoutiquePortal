package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * price_change_rule_category_brand 实体类
 * Thu Dec 01 18:07:18 CST 2016
 */
@Entity("price_change_rule_category_brand")
public class PriceChangeRuleCategoryBrand {
    /**
     * 1:price_change_rule_category_brand_id
     */
    @Id
    public Long price_change_rule_category_brand_id;
    /**
     * 2:price_change_rule_id
     */
    @Column
    public Long price_change_rule_id;
    /**
     * 3:category_id
     */
    @Column
    public Long category_id;

    /**
     * 4:brand_id
     */
    @Column
    public Long brand_id;

    /**
     * 5:discount_percentage
     */
    @Column
    public Long discount_percentage;


    /**
     * 6:exception_flag
     */
    @Column
    public Integer exception_flag;

    /**
     * 7:level
     */
    @Column
    public Integer level;

    public Integer getLevel() {
        return level;
    }

    public Long getPrice_change_rule_category_brand_id() {
        return price_change_rule_category_brand_id;
    }

    public Long getPrice_change_rule_id() {
        return price_change_rule_id;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public Long getBrand_id() {
        return brand_id;
    }

    public Long getDiscount_percentage() {
        return discount_percentage;
    }

    public Integer getException_flag() {
        return exception_flag;
    }
}

