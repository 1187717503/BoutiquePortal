/**
 * 
 */
package com.intramirror.main.api.service;

import java.util.Map;

/**
 * @author 123
 *
 */
public interface SkuPropertyService {

	/**
	 * 根据skucode查询value
	 * 
	 * @param skuCode
	 * @return
	 */
	Map<String, Object> getSizeValue(String skuCode);
}
