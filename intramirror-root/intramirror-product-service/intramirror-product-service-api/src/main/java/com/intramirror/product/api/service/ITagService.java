package com.intramirror.product.api.service;

import com.intramirror.product.api.model.Tag;
import java.util.List;
import java.util.Map;

public interface ITagService {

    int saveTagProductRel(Map<String, Object> map,Map<String,Object> response);

    List<Tag> getTags(String orderBy);

    List<Tag> getTagsByParam(Map<String,Object> param);

    Tag selectTagByTagId(Long tagId);

    void saveTagRel(Map<String, Object> param);
}
