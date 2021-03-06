package com.intramirror.product.api.service.content;

import com.intramirror.product.api.model.Block;
import com.intramirror.product.api.model.BlockDto;
import com.intramirror.product.api.model.BlockTagRel;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.model.TagProductRel;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/11/21.
 * @author YouFeng.Zhu
 */
public interface ContentManagementService {
    List<Map<String, Object>> listTagProductInfo(Long tagId);

    void updateContent(Block block, Tag tag, List<TagProductRel> sort);

    Map<String, Object> getTagAndBlockRelByTagId(Long tagId);

    List<Map<String, Object>> listUnbindTag(Long blockId);

    List<Long> listTagProductIds(List<Long> tagIds);

    List<Map<String, Object>> listTagsByProductIds(List<Map<String, Object>> products);

    List<Map<String, Object>> listTagsByProductIdsAndType(Map<String,Object> param);

    List<Long> listAllTagProductIds(List<Integer> tagTypes);

    List<Map<String, Object>> listBlockWithTag(String blockName, Integer status, Long tagId, Long modifiedAtFrom, Long modifiedAtTo, int start, int limit,
            int desc);

    int getBlockSize(String blockName, Integer status, Long tagId, Long modifiedAtFrom, Long modifiedAtTo);

    Map<String, Object> getBlockWithTagByBlockId(Long blockId);

    int updateBlockByBlockId(Block record);

    int batchUpdateBlock(List<Block> record);

    int createTag(Tag record);

    int deleteTag(Long tagId);

    int deleteByTagIdAndProductId(Long tagId, Long productId);

    int batchDeleteByTagIdAndProductId(List<TagProductRel> listTagProductRel);

    int batchDeleteByTagIdAndProductId1(List<Long> productIds,Long tagId,Map<String,Object> response);

    BlockTagRel createBlockWithDefaultTag(Block block) throws Exception;

}
