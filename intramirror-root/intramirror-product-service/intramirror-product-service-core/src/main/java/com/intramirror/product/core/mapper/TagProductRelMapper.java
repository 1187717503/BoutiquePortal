/**
 *
 */
package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.TagProductRel;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * @author Shang jian
 */
public interface TagProductRelMapper {

    int saveTagProductRel(Map<String, Object> map);

    int deleteByTagId(Long tagId);

    List<Map<String, Object>> getByProductAndTagId(Map<String, Object> map);

    int batchInsert(List<TagProductRel> listTagProductRel);

    int deleteByTagIdAndProductId(@Param(value = "tagId") Long tagId, @Param(value = "productId") Long productId);

    int batchDeleteByTagIdAndProductId(List<TagProductRel> listTagProductRel);

}
