package com.intramirror.product.core.impl.content;

import com.intramirror.product.api.model.Block;
import com.intramirror.product.api.model.BlockTagRel;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.model.TagProductRel;
import com.intramirror.product.api.service.BlockService;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.api.service.content.ContentManagementService;
import com.intramirror.product.core.mapper.BlockTagRelMapper;
import com.intramirror.product.core.mapper.ContentManagementMapper;
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
    private BlockService blockService;

    @Autowired
    private ITagService tagService;

    @Autowired
    private BlockTagRelMapper blockTagRelMapper;

    @Autowired
    private TagProductRelMapper tagProductRelMapper;

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

    private int updateBlock(Block block) {
        blockService.updateByBlockId(block);
        List<Block> blockList = blockService.listBlockBySortExcludeSelf(block.getSortOrder(), block.getBlockId());
        if (blockList.size() == 0) {
            return 0;
        }
        return blockService.batchUpdateSort(blockList);
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
        for (TagProductRel tagProductRel : sort) {
            tagProductRel.setTagId(tagId);
        }
        return tagProductRelMapper.batchInsert(sort);
    }

}
