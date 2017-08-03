package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * price_change_rule_product 实体类
 * Thu Dec 01 18:07:18 CST 2016
 */
@Entity("price_change_rule_product")
public class PriceChangeRuleProduct {
    /**
     * 1:price_change_rule_product_id
     */
    @Id
    public Long price_change_rule_product_id;
    /**
     * 2:price_change_rule_id
     */
    @Column
    public Long price_change_rule_id;
    /**
     * 3:product_id
     */
    @Column
    public Long product_id;

    /**
     * 4:boutique_id
     */
    @Column
    public String boutique_id;

    /**
     * 5:product_name
     */
    @Column
    public String product_name;


    /**
     * 6:discount_percentage
     */
    @Column
    public Long discount_percentage;


    public Long getPrice_change_rule_product_id() {
        return price_change_rule_product_id;
    }

    public Long getPrice_change_rule_id() {
        return price_change_rule_id;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public String getBoutique_id() {
        return boutique_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public Long getDiscount_percentage() {
        return discount_percentage;
    }
}

