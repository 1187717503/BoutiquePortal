/**
 * 
 */
package com.intramirror.order.core.mapper;

import java.util.Map;

/**
 * @author yml
 *
 */
public interface OrderExceptionMapper {

	/**
	 * 保存订单当前的用户留言
	 * @param map
	 * @return
	 */
	int saveOrderComments(Map<String, Object> map);
	
	/**
	 * 修改orderException
	 * @param map
	 * @return
	 */
	int updateOrderException(Map<String, Object> map);
}
