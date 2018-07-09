package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * product_group 实体类
 * Thu Dec 01 18:07:18 CST 2017
 */
@Entity("product_group")
public class ProductGroup {
    /**
     * 1:product_group_id
     */
    @Id
    public Long product_group_id;
    /**
     * 2:name
     */
    @Column
    public String name;

    /**
     * 3:group_type
     */
    @Column
    public Integer group_type;

    @Column
    public Long vendor_id;

    @Column
    public Long shop_id;

    @Column
    public Long user_id;

    @Column
    public Date valid_from;


    public Long getProduct_group_id() {
        return product_group_id;
    }

    public String getName() {
        return name;
    }

    public Integer getGroup_type() {
        return group_type;
    }

    public Long getVendor_id() {
        return vendor_id;
    }

    public Long getShop_id() {
        return shop_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Date getValid_from() {
        return valid_from;
    }
}

