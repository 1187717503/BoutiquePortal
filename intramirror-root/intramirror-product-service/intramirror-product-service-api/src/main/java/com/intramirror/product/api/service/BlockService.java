package com.intramirror.product.api.service;

import com.intramirror.product.api.model.Block;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/11/17.
 *
 * @author YouFeng.Zhu
 */
public interface BlockService {
    int insert(Block record);

    int createBlock(Block record);

    int updateByBlockId(Block block);

    Block getBlockById(Long blockId);

    List<Block> listAllBlock();

    List<Map<String, Object>> listSimpleBlock();

    List<Block> listBlockBySortExcludeSelf(int sortOrder, Long blockId);

    List<Block> listBlockBySort(int sortOrder);

    int batchUpdateSort(List<Block> blockList);

}
