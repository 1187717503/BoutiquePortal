package com.intramirror.product.api.service.content;

import com.intramirror.product.api.model.Block;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.model.TagProductRel;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/11/21.
 *
 * @author YouFeng.Zhu
 */
public interface ContentManagementService {
    List<Map<String, Object>> listTagProductInfo(Long tagId);

    void updateContent(Block block, Tag tag, List<TagProductRel> sort);

    Map<String, Object> getTagAndBlockRelByTagId(Long tagId);

    List<Map<String, Object>> listUnbindTag(Long blockId);

    List<Long> listTagProductIds(Long tagId);

    List<Map<String, Object>> listTagsByProductIds(List<Map<String, Object>> products);

    List<Map<String, Object>> listBlockWithTag();

    Map<String, Object> getBlockWithTagByBlockId(Long blockId);

}
