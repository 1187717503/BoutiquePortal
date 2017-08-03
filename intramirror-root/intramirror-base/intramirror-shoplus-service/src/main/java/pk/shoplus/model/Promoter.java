package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

@Entity("promoter")
public class Promoter {
	@Id
	public Long promoter_id;

	@Column
	public String name;

	@Column
	public String email;

	@Column
	public String telephone;

	@Column
	public Date created_at;

	@Column
	public Date updated_at;

	@Column
	public Boolean enabled;

	/**
	 * 根据 promoter_id 计算出来的代码，数据库没有该字段
	 */
	public String code;

	public Long getPromoter_id() {
		return promoter_id;
	}

	public void setPromoter_id(Long promoter_id) {
		this.promoter_id = promoter_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}

