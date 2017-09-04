/**
 * 
 */
package com.intramirror.order.api.model;

import java.util.Date;

/**
 * @author 123
 *
 */
public class OrderException {
	
	/**
	 * @Fields 订单错误ID
	 */
	private Long order_exception_id;
	
	/**
	 * @Fields 订单错误类型ID
	 */
	private Long order_exception_type_id;
	
	/**
	 * @Fields 物流订单的商品记录ID
	 */
	private String logistics_product_id;
	
	/**
	 * @Fields 评论内容
	 */
	private String comments;
	
	/**
	 * @Fields 决定
	 */
	private String resolution;
	
	/**
	 * @Fields 状态 1待审核，2，审核中，3审核通过，4审核失败
	 */
	private int status;
	
	/**
	 * @Fields 创建时间
	 */
	private String created_at;
	
	/**
	 * @Fileds 用户Id
	 */
	private Long created_by_user_id;
	
	/**
	 * @Fields 修改时间
	 */
	private Date modified_at;
	
	/**
	 * @Fileds 处理人Id
	 */
	private Long modified_by_user_id;

	public Long getOrder_exception_id() {
		return order_exception_id;
	}

	public void setOrder_exception_id(Long order_exception_id) {
		this.order_exception_id = order_exception_id;
	}

	public Long getOrder_exception_type_id() {
		return order_exception_type_id;
	}

	public void setOrder_exception_type_id(Long order_exception_type_id) {
		this.order_exception_type_id = order_exception_type_id;
	}

	public String getLogistics_product_id() {
		return logistics_product_id;
	}

	public void setLogistics_product_id(String logistics_product_id) {
		this.logistics_product_id = logistics_product_id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
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

	public Long getCreated_by_user_id() {
		return created_by_user_id;
	}

	public void setCreated_by_user_id(Long created_by_user_id) {
		this.created_by_user_id = created_by_user_id;
	}

	public Date getModified_at() {
		return modified_at;
	}

	public void setModified_at(Date modified_at) {
		this.modified_at = modified_at;
	}

	public Long getModified_by_user_id() {
		return modified_by_user_id;
	}

	public void setModified_by_user_id(Long modified_by_user_id) {
		this.modified_by_user_id = modified_by_user_id;
	}

	
}
