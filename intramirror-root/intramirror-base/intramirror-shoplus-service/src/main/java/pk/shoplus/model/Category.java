package pk.shoplus.model;

import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

/**
 * category 实体类 Thu Dec 01 18:07:14 CST 2016
 */
@Entity("category")
public class Category {
	/**
	 * 1:category_id
	 */
	@Id
	public Long category_id;
	/**
	 * 2:show_code
	 */
	@Column
	public String show_code;
	/**
	 * 3:name
	 */
	@Column
	public String name;
	/**
	 * 4:chinese_name
	 */
	@Column
	public String chinese_name;
	/**
	 * 5:parent_id
	 */
	@Column
	public Long parent_id;
	/**
	 * 6:level
	 */
	@Column
	public Integer level;
	/**
	 * 7:sort_order
	 */
	@Column
	public Integer sort_order;
	/**
	 * 8:remark
	 */
	@Column
	public String remark;
	/**
	 * 9:created_at
	 */
	@Column
	public Date created_at;
	/**
	 * 10:updated_at
	 */
	@Column
	public Date updated_at;
	/**
	 * 11:enabled
	 */
	@Column
	public Boolean enabled;
	/**
	 * 12:show_code_int
	 */
	@Column
	public Long show_code_int;
	
	/**
	* 13:cover_img
	*/
	@Column	public String cover_img;
	
	public List<Category> children;
	public Long value;
	public String text;

	public Long getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	public Long getCategory_id() {
		return category_id;
	}

	public String getShow_code() {
		return show_code;
	}

	public String getName() {
		return name;
	}

	public String getChinese_name() {
		return chinese_name;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public Integer getLevel() {
		return level;
	}

	public Integer getSort_order() {
		return sort_order;
	}

	public String getRemark() {
		return remark;
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

	public Long getShow_code_int() {
		return show_code_int;
	}

	public List<Category> getChildren() {
		return children;
	}

	public void setCategory_id(Long category_id) {
		this.category_id = category_id;
	}

	public void setShow_code(String show_code) {
		this.show_code = show_code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setChinese_name(String chinese_name) {
		this.chinese_name = chinese_name;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setSort_order(Integer sort_order) {
		this.sort_order = sort_order;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public void setShow_code_int(Long show_code_int) {
		this.show_code_int = show_code_int;
	}
	
	public String getCover_img(){
		return cover_img;
	}

	public void setChildren(List<Category> children) {
		this.children = children;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public void setText(String text) {
		this.text = text;
	}
	public void addChild(Category category){
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(category);
	}
}
