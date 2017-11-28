/**
 *
 */
package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.TagProductRel;
import java.util.List;
import java.util.Map;

/**
 * @author Shang jian
 */
public interface TagProductRelMapper {

    int saveTagProductRel(Map<String, Object> map);

    int deleteByTagId(Long tagId);

    List<Map<String, Object>> getByProductAndTagId(Map<String, Object> map);

    int batchInsert(List<TagProductRel> listTagProductRel);

}
