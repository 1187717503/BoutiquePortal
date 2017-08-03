package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * price_change_rule 实体类
 * Thu Dec 01 18:07:18 CST 2016
 */
@Entity("price_change_rule_season_group")
public class PriceChangeRuleSeasonGroup {
    /**
     * 1:price_change_rule_season_group_id
     */
    @Id
    public Long price_change_rule_season_group_id;
    /**
     * 2:name
     */
    @Column
    public String name;

    /**
     * 3:price_change_rule_id
     */
    @Column
    public Long price_change_rule_id;

    /**
     * 4:season_code
     */
    @Column
    public String season_code;

    @Column
    public Integer enabled;

    @Column	public Date created_at;

    @Column	public Date updated_at;


    public Long getPrice_change_rule_season_group_id() {
        return price_change_rule_season_group_id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice_change_rule_id() {
        return price_change_rule_id;
    }

    public String getSeason_code() {
        return season_code;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }
}

