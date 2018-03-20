/**
 * 
 */
package com.intramirror.order.api.service;

import java.util.List;
import java.util.Map;

/**
 * @author yml
 *
 */
public interface IOrderExceptionTypeService {

	/**
	 * 保存订单当前的用户留言
	 * @return
	 */
	List<Map<String, Object>> getExceptionType();
}
