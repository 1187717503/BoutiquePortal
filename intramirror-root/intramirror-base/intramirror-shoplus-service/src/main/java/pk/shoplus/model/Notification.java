package pk.shoplus.model;

import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

/**
 * notification 实体类 Thu Dec 01 18:07:15 CST 2016
 */
@Entity("notification")
public class Notification {
	/**
	 * 1:notification_id
	 */
	@Id
	public Long notification_id;
	/**
	 * 2:pic
	 */
	@Column
	public String pic;
	/**
	 * 3:title
	 */
	@Column
	public String title;
	/**
	 * 4:content
	 */
	@Column
	public String content;
	/**
	 * 5:type
	 */
	@Column
	public Integer type;
	/**
	 * 6:send_time
	 */
	@Column
	public Date send_time;
	/**
	 * 7:created_at
	 */
	@Column
	public Date created_at;
	/**
	 * 8:updated_at
	 */
	@Column
	public Date updated_at;
	/**
	 * 9:enabled
	 */
	@Column
	public Boolean enabled;
	/**
	 * 10:url
	 */
	@Column
	public String url;
	/**
	 * 11:role
	 */
	@Column
	public Integer role;
	/**
	 * 12:user_id
	 */
	@Column
	public Long user_id;
	/**
	 * 13:is_system_send
	 */
	@Column
	public Boolean is_system_send;
	
	@Column
	public Long category_id;

	public Long getCategory_id() {
		return category_id;
	}


	public Long getNotification_id() {
		return notification_id;
	}

	public String getPic() {
		return pic;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public Integer getType() {
		return type;
	}

	public Date getSend_time() {
		return send_time;
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

	public String getUrl() {
		return url;
	}

	public Integer getRole() {
		return role;
	}

	public Long getUser_id() {
		return user_id;
	}

	public Boolean getIs_system_send() {
		return is_system_send;
	}
}
