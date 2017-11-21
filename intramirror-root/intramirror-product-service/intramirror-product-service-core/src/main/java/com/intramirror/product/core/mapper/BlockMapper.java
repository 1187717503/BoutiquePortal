package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Block;
import java.util.List;
import java.util.Map;

public interface BlockMapper {

    int deleteByPrimaryKey(Long blockId);

    int insert(Block record);

    int insertSelective(Block record);

    Block selectByPrimaryKey(Long blockId);

    int updateByPrimaryKeySelective(Block record);

    int updateByPrimaryKeyWithBLOBs(Block record);

    int updateByPrimaryKey(Block record);

    List<Map<String, Object>> listSimpleBlock();
}