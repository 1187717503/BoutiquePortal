package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * price_change_rule_product 实体类
 * Thu Dec 01 18:07:18 CST 2016
 */
@Entity("price_change_rule_group")
public class PriceChangeRuleGroup {
    /**
     * 1:price_change_rule_product_id
     */
    @Id
    public Long price_change_rule_group_id;
    /**
     * 2:price_change_rule_id
     */
    @Column
    public Long price_change_rule_id;
    /**
     * 3:product_id
     */
    @Column
    public Long product_group_id;


    /**
     * 6:discount_percentage
     */
    @Column
    public Long discount_percentage;


    public Long getPrice_change_rule_group_id() {
        return price_change_rule_group_id;
    }

    public Long getPrice_change_rule_id() {
        return price_change_rule_id;
    }

    public Long getProduct_group_id() {
        return product_group_id;
    }

    public Long getDiscount_percentage() {
        return discount_percentage;
    }
}

