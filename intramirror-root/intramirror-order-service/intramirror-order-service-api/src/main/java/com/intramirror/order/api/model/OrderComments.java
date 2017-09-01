/**
 * 
 */
package com.intramirror.order.api.model;

import java.util.Date;

/**
 * @author 123
 *
 */
public class OrderComments {
	
	/**
	 * @Files 订单留言ID
	 */
	private Long order_comments_id;
	
	/**
	 * @Files 物流订单的商品记录ID
	 */
	private Long logistics_product_id;
	
	/**
	 * @Files 评论内容
	 */
	private String comments;
	
	/**
	 * @Files 原因
	 */
	private String reason;
	
	/**
	 * @Files 状态 1待审核，2，审核中，3审核通过，4审核失败
	 */
	private int status;
	
	/**
	 * @Files 创建时间
	 */
	private String created_at;
	
	/**
	 * @Files 修改时间
	 */
	private Date updated_at;

	public Long getOrder_comments_id() {
		return order_comments_id;
	}

	public void setOrder_comments_id(Long order_comments_id) {
		this.order_comments_id = order_comments_id;
	}

	public Long getLogistics_product_id() {
		return logistics_product_id;
	}

	public void setLogistics_product_id(Long logistics_product_id) {
		this.logistics_product_id = logistics_product_id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	

}
