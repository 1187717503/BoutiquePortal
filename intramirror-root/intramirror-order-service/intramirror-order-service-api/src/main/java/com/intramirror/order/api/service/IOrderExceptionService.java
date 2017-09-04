/**
 * 
 */
package com.intramirror.order.api.service;

import java.util.Map;

/**
 * @author yml
 *
 */
public interface IOrderExceptionService {

	/**
	 * 保存订单当前的用户留言
	 * @param map
	 * @return
	 */
	int saveOrderComments(Map<String, Object> map);
}
