package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * Created by dingyifan on 2017/7/13.
 */
@Entity("season_mapping")
public class SeasonMapping {
    @Id
    public Long season_mapping_id;

    @Column
    public String season_code;

    @Column
    public String boutique_season_code;

    @Column
    public boolean enabled;

    @Column
    public Date created_at;

    @Column
    public Date updated_at;
    
    @Column
	private Long vendor_id;

    public String getSeason_code() {
        return season_code;
    }

    public void setSeason_code(String season_code) {
        this.season_code = season_code;
    }

    public String getBoutique_season_code() {
        return boutique_season_code;
    }

    public void setBoutique_season_code(String boutique_season_code) {
        this.boutique_season_code = boutique_season_code;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

	public Long getSeason_mapping_id() {
		return season_mapping_id;
	}

	public void setSeason_mapping_id(Long season_mapping_id) {
		this.season_mapping_id = season_mapping_id;
	}

	public Long getVendor_id() {
		return vendor_id;
	}

	public void setVendor_id(Long vendor_id) {
		this.vendor_id = vendor_id;
	}
    
}
