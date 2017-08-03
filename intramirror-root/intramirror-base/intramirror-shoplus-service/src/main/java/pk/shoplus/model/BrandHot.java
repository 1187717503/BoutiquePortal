package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.math.BigDecimal;

/**
 * brand_hot 实体类
 * Thu Dec 01 18:07:14 CST 2016
 */
@Entity("brand_hot")
public class BrandHot {
    /**
     * 1:brand_hot_id
     */
    @Id
    public Long brand_hot_id;
    /**
     * 2:brand_id
     */
    @Column
    public Long brand_id;
    /**
     * 3:type
     */
    @Column
    public Integer type;
    /**
     * 4:weight
     */
    @Column
    public BigDecimal weight;


    public Long getBrand_hot_id() {
        return brand_hot_id;
    }

    public Long getBrand_id() {
        return brand_id;
    }

    public Integer getType() {
        return type;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}

