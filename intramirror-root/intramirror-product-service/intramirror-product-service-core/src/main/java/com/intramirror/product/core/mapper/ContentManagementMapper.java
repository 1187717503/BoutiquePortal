package com.intramirror.product.core.mapper;

import java.util.List;
import java.util.Map;

/**
 * Created on 2017/10/30.
 *
 * @author YouFeng.Zhu
 */
public interface ContentManagementMapper {

    List<Map<String, Object>> listTagProductInfo(Long tagId);

    Map<String, Object> getTagAndBlockRelByTagId(Long tagId);

    List<Map<String, Object>> listUnbindTag();
}
