package com.intramirror.product.core.impl;

import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.TagProductRelMapper;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends BaseDao implements ITagService {

    private static Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    private TagProductRelMapper tagProductRelMapper;

    public void init() {
        tagProductRelMapper = this.getSqlSession().getMapper(TagProductRelMapper.class);
    }

    @Override
    public int saveTagProductRel(Map<String, Object> map) throws Exception {
        return tagProductRelMapper.saveTagProductRel(map);
    }
}
