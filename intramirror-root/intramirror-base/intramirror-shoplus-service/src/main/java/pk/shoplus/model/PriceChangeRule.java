package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * price_change_rule 实体类
 * Thu Dec 01 18:07:18 CST 2016
 */
@Entity("price_change_rule")
public class PriceChangeRule {
    /**
     * 1:price_change_rule_id
     */
    @Id
    public Long price_change_rule_id;
    /**
     * 2:name
     */
    @Column
    public String name;

    /**
     * 3:price_type
     */
    @Column
    public Integer price_type;

    /**
     * 5:valid_from
     */
    @Column
    public Date valid_from;

    /**
     * 6:status
     */
    @Column
    public Integer status;

    @Column
    public Long vendor_id;

    @Column
    public Long shop_id;

    @Column
    public Long user_id;

    public String season_code;


    public Long getPrice_change_rule_id() {
        return price_change_rule_id;
    }

    public Long getVendor_id() {
        return vendor_id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice_type() {
        return price_type;
    }

    public Date getValid_from() {
        return valid_from;
    }

    public Integer getStatus() {
        return status;
    }

    public Long getShop_id() {
        return shop_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getSeason_code() {
        return season_code;
    }
}

