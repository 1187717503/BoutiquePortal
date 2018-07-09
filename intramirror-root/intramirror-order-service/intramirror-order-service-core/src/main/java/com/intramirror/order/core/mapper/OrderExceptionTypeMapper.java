/**
 * 
 */
package com.intramirror.order.core.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author yml
 *
 */
public interface OrderExceptionTypeMapper {

	/**
	 * 保存订单当前的用户留言
	 * @return
	 */
	List<Map<String, Object>> getExceptionType();
}
