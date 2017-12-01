package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Block;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface BlockMapper {

    int deleteByPrimaryKey(Long blockId);

    int insert(Block record);

    int insertSelective(Block record);

    Block selectByPrimaryKey(Long blockId);

    int updateByBlockId(Block record);

    int updateByPrimaryKeyWithBLOBs(Block record);

    int updateByPrimaryKey(Block record);

    List<Map<String, Object>> listSimpleBlock();

    List<Block> listBlockBySortExcludeSelf(@Param(value = "sortOrder") int sortOrder, @Param(value = "blockId") Long blockId);

    List<Block> listBlockBySort(@Param(value = "sortOrder") int sortOrder);

    int batchUpdateSort(List<Block> blockList);
}