package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.core.mapper.TagMapper;
import com.intramirror.product.core.mapper.TagProductRelMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements ITagService {

    private static Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    private TagProductRelMapper tagProductRelMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public int saveTagProductRel(Map<String, Object> map) {

        List<String> listPrdIdDuplicated = new ArrayList<>();

        // 已经有的关系，不用添加，并且作为失败列表返回给前端
        // 1. 根据tag_id和product_id拿到重复的选项
        List<Map<String, Object>> listTagProductRel = tagProductRelMapper.getByProductAndTagId(map);
        List<String> productIdList = (List<String>) map.get("productIdList");
        // 2. 剔重
        for (int i = 0; i < listTagProductRel.size(); i++) {
            Map<String, Object> mTagPrdRel = listTagProductRel.get(i);
            String sPrdIdRes = mTagPrdRel.get("product_id").toString();

            Iterator it = productIdList.iterator();
            while (it.hasNext()) {
                String sPrdIdTarget = it.next().toString();
                if (sPrdIdTarget.equals(sPrdIdRes)) {
                    it.remove();
                    listPrdIdDuplicated.add(sPrdIdTarget);
                }
            }
        }

        logger.info("saveTagProductRel： repeat count [{}]; pass count [{}]", listPrdIdDuplicated.size(), productIdList.size());

        if (productIdList.size() <= 0) {
            return 0;
        }

        Map<String, Object> mapToSave = new HashMap<>();
        mapToSave.put("tagId", map.get("tagId"));
        mapToSave.put("productIdList", map.get("productIdList"));
        mapToSave.put("sort_num", map.get("sort_num"));

        return tagProductRelMapper.saveTagProductRel(mapToSave);
    }

    @Override
    public List<Tag> getTags() {
        logger.info("[service] getTags");
        return tagMapper.getTags();
    }

    @Override
    public Tag selectByPrimaryKey(Long tagId) {
        logger.info("[service] getTags and tagId is [{}]", tagId);
        return tagMapper.selectByPrimaryKey(tagId);
    }
}
