/**
 * 
 */
package com.intramirror.order.api.model;

/**
 * @author 123
 *
 */
public class OrderExceptionType {
	
	/**
	 * @Fields 订单错误类型ID
	 */
	private Long order_exception_type_id;
	
	/**
	 * @Fields 订单错误类型内容
	 */
	private String description;

	public Long getOrder_exception_type_id() {
		return order_exception_type_id;
	}

	public void setOrder_exception_type_id(Long order_exception_type_id) {
		this.order_exception_type_id = order_exception_type_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
}
