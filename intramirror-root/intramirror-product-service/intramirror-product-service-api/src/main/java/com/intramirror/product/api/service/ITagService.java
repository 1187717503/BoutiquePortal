package com.intramirror.product.api.service;

import com.intramirror.product.api.model.Tag;
import java.util.List;
import java.util.Map;

public interface ITagService {

    int saveTagProductRel(Map<String, Object> map) throws Exception;

    List<Tag> getTags() throws Exception;

    Tag selectByPrimaryKey(Long tagId) throws Exception;
}
