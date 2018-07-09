package pk.shoplus.model;

import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

/**
 * sku_store 实体类 Thu Dec 01 18:07:18 CST 2016
 */
@Entity("sku_store")
public class SkuStore {
	/**
	 * 1:sku_store_id
	 */
	@Id
	public Long sku_store_id;
	/**
	 * 2:sku_id
	 */
	@Column
	public Long sku_id;
	/**
	 * 3:product_id
	 */
	@Column
	public Long product_id;
	/**
	 * 4:store
	 */
	@Column
	public Long store;
	/**
	 * 5:remind
	 */
	@Column
	public Integer remind;
	/**
	 * 6:ordered
	 */
	@Column
	public Integer ordered;
	/**
	 * 7:confirm
	 */
	@Column
	public Integer confirm;
	/**
	 * 8:ship
	 */
	@Column
	public Integer ship;
	/**
	 * 9:finished
	 */
	@Column
	public Integer finished;
	/**
	 * 10:returned
	 */
	@Column
	public Integer returned;
	/**
	 * 11:changed
	 */
	@Column
	public Integer changed;
	/**
	 * 12:clear
	 */
	@Column
	public Integer clear;
	/**
	 * 13:agree_return_rate
	 */
	@Column
	public BigDecimal agree_return_rate;
	/**
	 * 14:created_at
	 */
	@Column
	public Date created_at;
	/**
	 * 15:updated_at
	 */
	@Column
	public Date updated_at;
	/**
	 * 16:enabled
	 */
	@Column
	public Boolean enabled;

	@Column
	public Long reserved;

	@Column
	public Long confirmed;

	@Column public Date last_check;

	public Long getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Long confirmed) {
		this.confirmed = confirmed;
	}

	public Long getSku_store_id() {
		return sku_store_id;
	}

	public Long getSku_id() {
		return sku_id;
	}

	public Long getProduct_id() {
		return product_id;
	}

	public Long getStore() {
		return store;
	}

	public Integer getRemind() {
		return remind;
	}

	public Integer getOrdered() {
		return ordered;
	}

	public Integer getConfirm() {
		return confirm;
	}

	public Integer getShip() {
		return ship;
	}

	public Integer getFinished() {
		return finished;
	}

	public Integer getReturned() {
		return returned;
	}

	public Integer getChanged() {
		return changed;
	}

	public Integer getClear() {
		return clear;
	}

	public BigDecimal getAgree_return_rate() {
		return agree_return_rate;
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

	public Long getReserved() {
		return reserved;
	}

	public void setReserved(Long reserved) {
		this.reserved = reserved;
	}

	public void setSku_store_id(Long sku_store_id) {
		this.sku_store_id = sku_store_id;
	}

	public void setSku_id(Long sku_id) {
		this.sku_id = sku_id;
	}

	public void setProduct_id(Long product_id) {
		this.product_id = product_id;
	}

	public void setStore(Long store) {
		this.store = store;
	}

	public void setRemind(Integer remind) {
		this.remind = remind;
	}

	public void setOrdered(Integer ordered) {
		this.ordered = ordered;
	}

	public void setConfirm(Integer confirm) {
		this.confirm = confirm;
	}

	public void setShip(Integer ship) {
		this.ship = ship;
	}

	public void setFinished(Integer finished) {
		this.finished = finished;
	}

	public void setReturned(Integer returned) {
		this.returned = returned;
	}

	public void setChanged(Integer changed) {
		this.changed = changed;
	}

	public void setClear(Integer clear) {
		this.clear = clear;
	}

	public void setAgree_return_rate(BigDecimal agree_return_rate) {
		this.agree_return_rate = agree_return_rate;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Date getLast_check() {
		return last_check;
	}

	public void setLast_check(Date last_check) {
		this.last_check = last_check;
	}
}
