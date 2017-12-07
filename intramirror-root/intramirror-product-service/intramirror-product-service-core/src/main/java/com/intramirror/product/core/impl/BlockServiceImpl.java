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
    public int createBlock(Block block) {
        block.setStatus((byte) 0);
        block.setEnabled(true);
        return blockMapper.insert(block);
    }

    @Override
    public int updateByBlockId(Block record) {
        return blockMapper.updateByBlockId(record);
    }

    @Override
    public Block getBlockById(Long blockId) {
        return blockMapper.selectByPrimaryKey(blockId);
    }

    @Override
    public List<Block> listAllBlock() {
        return blockMapper.listAllBlock();
    }

    @Override
    public List<Map<String, Object>> listSimpleBlock() {
        return blockMapper.listSimpleBlock();
    }

    @Override
    public List<Block> listBlockBySortExcludeSelf(int sortOrder, Long blockId) {
        return blockMapper.listBlockBySortExcludeSelf(sortOrder, blockId);
    }

    @Override
    public List<Block> listBlockBySort(int sortOrder) {
        return blockMapper.listBlockBySort(sortOrder);
    }

    @Override
    public int batchUpdateSort(List<Block> blockList) {
        return blockMapper.batchUpdateSort(blockList);
    }
}
