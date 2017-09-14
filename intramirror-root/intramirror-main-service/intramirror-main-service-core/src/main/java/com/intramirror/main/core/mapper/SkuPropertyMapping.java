/**
 * 
 */
package com.intramirror.main.core.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 123
 *
 */
public interface SkuPropertyMapping {

	/**
	 * 根据skuCode查询value
	 * @param skuCode
	 * @return
	 */
	List<Map<String, Object>> getSizeValue(Map<String, Object> map);
}
