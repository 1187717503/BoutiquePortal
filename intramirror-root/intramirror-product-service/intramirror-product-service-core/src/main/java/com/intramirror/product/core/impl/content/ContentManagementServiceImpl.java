package com.intramirror.product.core.impl.content;

import com.intramirror.product.api.model.Block;
import com.intramirror.product.api.model.BlockTagRel;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.model.TagProductRel;
import com.intramirror.product.api.service.content.ContentManagementService;
import com.intramirror.product.core.mapper.BlockMapper;
import com.intramirror.product.core.mapper.BlockTagRelMapper;
import com.intramirror.product.core.mapper.ContentManagementMapper;
import com.intramirror.product.core.mapper.TagMapper;
import com.intramirror.product.core.mapper.TagProductRelMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 2017/11/21.
 *
 * @author YouFeng.Zhu
 */
@Service
public class ContentManagementServiceImpl implements ContentManagementService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ContentManagementServiceImpl.class);
    @Autowired
    private ContentManagementMapper contentManagementMapper;

    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    private BlockTagRelMapper blockTagRelMapper;

    @Autowired
    private TagProductRelMapper tagProductRelMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Map<String, Object>> listTagProductInfo(Long tagId) {
        return contentManagementMapper.listTagProductInfo(tagId);
    }

    @Override
    @Transactional
    public void updateContent(Block block, Tag tag, List<TagProductRel> sort) {
        updateBlock(block);
        rebindBlockTag(block, tag.getTagId());
        updateTagProductSort(tag.getTagId(), sort);
    }

    @Override
    public Map<String, Object> getTagAndBlockRelByTagId(Long tagId) {
        return contentManagementMapper.getTagAndBlockRelByTagId(tagId);
    }

    @Override
    public List<Map<String, Object>> listUnbindTag(Long blockId) {
        return contentManagementMapper.listUnbindTag(blockId);
    }

    @Override
    public List<Long> listTagProductIds(Long tagId) {
        return contentManagementMapper.listTagProductIds(tagId);
    }

    @Override
    public List<Map<String, Object>> listTagsByProductIds(List<Map<String, Object>> productIds) {
        return contentManagementMapper.listTagsByProductIds(productIds);
    }

    @Override
    public List<Long> listAllTagProductIds() {
        return contentManagementMapper.listAllTagProductIds();
    }

    @Override
    public List<Map<String, Object>> listBlockWithTag(String blockName, Integer status, Long tagId, Long modifiedAtFrom, Long modifiedAtTo) {
        return contentManagementMapper.listBlockWithTag(blockName, status, tagId, modifiedAtFrom, modifiedAtTo);
    }

    @Override
    public Map<String, Object> getBlockWithTagByBlockId(Long blockId) {
        return contentManagementMapper.getBlockWithTagByBlockId(blockId);
    }

    @Override
    @Transactional
    public int updateBlockByBlockId(Block record) {
        if (record.getSortOrder() == null) {
            return blockMapper.updateByBlockId(record);
        } else {
            return updateBlock(record);
        }
    }

    @Override
    @Transactional
    public int batchUpdateBlock(List<Block> record) {
        return blockMapper.batchUpdateBlock(record);
    }

    @Override
    @Transactional
    public int createTag(Tag record) {
        record.setEnabled(true);
        return tagMapper.insertSelective(record);
    }

    @Override
    @Transactional
    public int deleteTag(Long tagId) {
        tagProductRelMapper.deleteByTagId(tagId);
        blockTagRelMapper.deleteByTagId(tagId);
        return tagMapper.deleteByPrimaryKey(tagId);
    }

    private int updateBlock(Block block) {
        List<Block> blockList = blockMapper.listBlockBySort(block.getSortOrder());
        blockMapper.updateByBlockId(block);
        Block self = null;
        for (Block b : blockList) {
            if (b.getBlockId() == block.getBlockId()) {
                if (b.getSortOrder() == block.getSortOrder()) {
                    return 0;
                } else {
                    self = b;
                }
            }
        }
        blockList.remove(self);
        if (blockList.size() == 0) {
            return 0;
        }
        return blockMapper.batchUpdateSort(blockList);
    }

    private int rebindBlockTag(Block block, Long tagId) {
        blockTagRelMapper.deleteByBlockId(block.getBlockId());
        BlockTagRel btRel = new BlockTagRel();
        btRel.setBlockId(block.getBlockId());
        btRel.setTagId(tagId);
        return blockTagRelMapper.insertSelective(btRel);
    }

    private int updateTagProductSort(Long tagId, List<TagProductRel> sort) {
        tagProductRelMapper.deleteByTagId(tagId);
        if (sort.size() == 0) {
            return 0;
        }
        for (TagProductRel tagProductRel : sort) {
            tagProductRel.setTagId(tagId);
        }
        return tagProductRelMapper.batchInsert(sort);
    }

}
