package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.Block;
import com.intramirror.product.api.service.BlockService;
import com.intramirror.product.core.mapper.BlockMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2017/11/17.
 *
 * @author YouFeng.Zhu
 */
@Service
public class BlockServiceImpl implements BlockService {

    private final static Logger LOGGER = LoggerFactory.getLogger(BlockServiceImpl.class);

    @Autowired
    private BlockMapper blockMapper;

    @Override
    public int insert(Block record) {
        return blockMapper.insert(record);
    }

    @Override
    public int updateByBlockId(Block record) {
        return blockMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public Block getBlockById(Long blockId) {
        return blockMapper.selectByPrimaryKey(blockId);
    }

    @Override
    public List<Block> listAllBlock() {
        return null;
    }

    @Override
    public List<Map<String, Object>> listSimpleBlock() {
        return blockMapper.listSimpleBlock();
    }
}
