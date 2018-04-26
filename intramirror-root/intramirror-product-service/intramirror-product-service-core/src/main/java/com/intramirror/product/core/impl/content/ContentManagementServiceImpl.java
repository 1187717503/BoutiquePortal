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
    public List<Map<String, Object>> listBlockWithTag(String blockName, Integer status, Long tagId, Long modifiedAtFrom, Long modifiedAtTo, int start,
            int limit, int desc) {
        return contentManagementMapper.listBlockWithTag(blockName, status, tagId, modifiedAtFrom, modifiedAtTo, start, limit, desc);
    }

    @Override
    public int getBlockSize(String blockName, Integer status, Long tagId, Long modifiedAtFrom, Long modifiedAtTo) {
        return contentManagementMapper.getBlockSize(blockName, status, tagId, modifiedAtFrom, modifiedAtTo);
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
        return blockMapper.batchUpdateBlock(record.get(0).getStatus(), record);
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
        //blockTagRelMapper.deleteByTagId(tagId);
        return tagMapper.deleteByPrimaryKey(tagId);
    }

    @Override
    @Transactional
    public int deleteByTagIdAndProductId(Long tagId, Long productId) {
        return tagProductRelMapper.deleteByTagIdAndProductId(tagId, productId);
    }

    @Override
    @Transactional
    public int batchDeleteByTagIdAndProductId(List<TagProductRel> listTagProductRel) {
        return tagProductRelMapper.batchDeleteByTagIdAndProductId(listTagProductRel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlockTagRel createBlockWithDefaultTag(Block block) throws Exception {
        List<Block> blockList = blockMapper.listBlockBySort(block.getSortOrder());
        block.setStatus((byte) 1);
        block.setEnabled(true);
        blockMapper.insertSelective(block);
        if (blockList.size() != 0) {
            blockMapper.batchUpdateSort(blockList);
        }

        Tag tag = new Tag();
        tag.setTagName(block.getBlockName());

        if (tagMapper.getTagsByName(tag) != null) {
            LOGGER.error("Tag name is duplicate {}.", block.getBlockName());
            throw new Exception("Tag name is duplicate");
        }

        tag.setEnabled(true);

        int rowNum = tagMapper.insertSelective(tag);
        LOGGER.info("Create tag for name {}, effect {} rows.", block.getBlockName(), rowNum);

        BlockTagRel btRel = new BlockTagRel();
        btRel.setTagId(tag.getTagId());
        btRel.setBlockId(block.getBlockId());

        blockTagRelMapper.insertSelective(btRel);

        return btRel;
    }

    private int updateBlock(Block block) {
        Block originBlock = blockMapper.selectByPrimaryKey(block.getBlockId());
        int origSort = originBlock.getSortOrder();
        int newSort = block.getSortOrder();

        if (newSort < origSort) {
            blockMapper.updateLeftSort(newSort, origSort);
        } else if (newSort > origSort) {
            blockMapper.updateRightSort(origSort, newSort);
        }

        return blockMapper.updateByBlockId(block);
    }

    private int rebindBlockTag(Block block, Long tagId) {
        blockTagRelMapper.deleteByBlockId(block.getBlockId());
        if (tagId == -1) {
            return 0;
        }
        BlockTagRel btRel = new BlockTagRel();
        btRel.setBlockId(block.getBlockId());
        btRel.setTagId(tagId);
        return blockTagRelMapper.insertSelective(btRel);
    }

    private int updateTagProductSort(Long tagId, List<TagProductRel> sort) {
        tagProductRelMapper.deleteByTagId(tagId);
        if (sort.size() == 0 || tagId == -1) {
            return 0;
        }
        for (TagProductRel tagProductRel : sort) {
            tagProductRel.setTagId(tagId);
        }
        return tagProductRelMapper.batchInsert(sort);
    }

}
