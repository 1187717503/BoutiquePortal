package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * Created by dingyifan on 2017/7/13.
 */
@Entity("season")
public class Season {
    @Id
    public String season_code;

    @Column
    public String chinese_desc;

    @Column
    public boolean enabled;

    @Column
    public Date created_at;

    @Column
    public Date updated_at;

    public String getSeason_code() {
        return season_code;
    }

    public void setSeason_code(String season_code) {
        this.season_code = season_code;
    }

    public String getChinese_desc() {
        return chinese_desc;
    }

    public void setChinese_desc(String chinese_desc) {
        this.chinese_desc = chinese_desc;
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
}
