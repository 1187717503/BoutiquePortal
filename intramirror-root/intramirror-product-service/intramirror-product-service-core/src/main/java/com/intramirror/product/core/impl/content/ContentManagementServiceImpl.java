package com.intramirror.product.core.impl.content;

import com.intramirror.product.api.service.content.ContentManagementService;
import com.intramirror.product.core.mapper.ContentManagementMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<Map<String, Object>> listTagProductInfo(Long tagId) {
        return contentManagementMapper.listTagProductInfo(tagId);
    }
}
