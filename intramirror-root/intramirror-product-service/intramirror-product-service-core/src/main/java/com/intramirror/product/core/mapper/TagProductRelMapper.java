/**
 *
 */
package com.intramirror.product.core.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Shang jian
 */
public interface TagProductRelMapper {

    int saveTagProductRel(Map<String, Object> map);

    List<Map<String, Object>> getByProductAndTagId(Map<String, Object> map);
}
