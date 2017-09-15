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
public interface SeasonMappingMapping {

	/**
	 * 根据vendorID查询BoutiqueSeasonCode
	 * @param vendorId
	 * @return
	 */
	List<Map<String, Object>> getBoutiqueSeasonCode(String vendorId);
}
