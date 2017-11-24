package com.intramirror.product.core.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * Created on 2017/10/30.
 *
 * @author YouFeng.Zhu
 */
public interface ContentManagementMapper {

    List<Map<String, Object>> listTagProductInfo(Long tagId);

    Map<String, Object> getTagAndBlockRelByTagId(Long tagId);

    List<Map<String, Object>> listUnbindTag(@Param(value = "blockId") Long blockId);

    List<Map<String, Object>> listTagsByProductIds(List<Map<String, Object>> productIds);

    List<Map<String, Object>> listBlockWithTag();

    Map<String, Object> getBlockWithTagByBlockId(Long blockId);
}
