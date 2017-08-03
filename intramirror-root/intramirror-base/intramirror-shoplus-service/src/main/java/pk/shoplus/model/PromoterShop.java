package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

@Entity("promoter_shop")
public class PromoterShop {
	@Id
	public Long promoter_shop_id;

	@Column
	public Long promoter_id;

	@Column
	public Long shop_id;

	@Column
	public Date created_at;

	@Column
	public Date updated_at;

	@Column
	public Boolean enabled;

	public Long getPromoter_shop_id() {
		return promoter_shop_id;
	}

	public Long getPromoter_id() {
		return promoter_id;
	}

	public Long getShop_id() {
		return shop_id;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public Boolean getEnabled() {
		return enabled;
	}

}
