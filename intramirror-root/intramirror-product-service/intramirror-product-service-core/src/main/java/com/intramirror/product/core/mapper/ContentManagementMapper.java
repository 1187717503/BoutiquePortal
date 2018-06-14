package com.intramirror.product.core.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * Created on 2017/10/30.
 * @author YouFeng.Zhu
 */
public interface ContentManagementMapper {

    List<Map<String, Object>> listTagProductInfo(Long tagId);

    Map<String, Object> getTagAndBlockRelByTagId(Long tagId);

    List<Map<String, Object>> listUnbindTag(@Param(value = "blockId") Long blockId);

    List<Map<String, Object>> listTagsByProductIds(List<Map<String, Object>> productIds);

    List<Map<String, Object>> listTagsByProductIdsAndType(Map<String,Object> param);

    List<Map<String, Object>> listBlockWithTag(@Param(value = "blockName") String blockName, @Param(value = "status") Integer status,
            @Param(value = "tagId") Long tagId, @Param(value = "modifiedAtFrom") Long modifiedAtFrom, @Param(value = "modifiedAtTo") Long modifiedAtTo,
            @Param(value = "start") int start, @Param(value = "limit") int limit, @Param(value = "desc") int desc);

    int getBlockSize(@Param(value = "blockName") String blockName, @Param(value = "status") Integer status, @Param(value = "tagId") Long tagId,
            @Param(value = "modifiedAtFrom") Long modifiedAtFrom, @Param(value = "modifiedAtTo") Long modifiedAtTo);

    Map<String, Object> getBlockWithTagByBlockId(Long blockId);

    List<Long> listTagProductIds(Map<String,Object> param);

    List<Long> listAllTagProductIds(Map<String,Object> param);

}
