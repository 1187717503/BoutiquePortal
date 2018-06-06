package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.core.mapper.ProductMapper;
import com.intramirror.product.core.mapper.TagMapper;
import com.intramirror.product.core.mapper.TagProductRelMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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

    @Autowired
    private ProductMapper productMapper;


    @Override
    public int saveTagProductRel(Map<String, Object> map) {

        List<String> listPrdIdDuplicated = new ArrayList<>();
        // type = 2 时
        //1. 不同boutqiue的商品不能加入到group中
        //2. 同一个商品不能加入两个group中，除非是爆款）
        List<Long> productIdList = (List<Long>) map.get("productIdList");
        List<Long> vendorTagIds = new ArrayList<>();
        Long tagId = Long.valueOf(map.get("tag_id").toString());
        Tag tag = tagMapper.selectByPrimaryKey(tagId);
        if(tag.getTagType() == 2){ // 爆款 除外 tag 也除外
            Long vendorId = tag.getVendorId();
            List<ProductWithBLOBs> productWithBLOBs =  productMapper.listProductByProductIds(productIdList);
            if(CollectionUtils.isNotEmpty(productWithBLOBs)){
                for(ProductWithBLOBs p : productWithBLOBs){
                    if(!vendorId.equals(p.getVendorId())){
                        productIdList.remove(p.getProductId()); // 不在一个vendor的不要加
                        listPrdIdDuplicated.add(p.getProductId().toString());
                    }
                }
            }
            Map<String,Object> param = new HashMap<>();
            List<Integer> types = new ArrayList<>();
            types.add(2);
            List<Long> vendorIds = new ArrayList<>();
            vendorIds.add(vendorId);
            param.put("vendorIds",vendorIds);
            param.put("tagTypes",types);
            List<Tag> tags = tagMapper.getTagsByParam(param);
            if(CollectionUtils.isNotEmpty(tags)){
                for(Tag t : tags){
                    vendorTagIds.add(t.getTagId());
                }
            }

        }
        Map<String,Object> para = new HashMap<>();
        para.putAll(map);
        if(tag.getTagType() == 2){
            para.remove("tag_id");
        }

        // 已经有的关系，不用添加，并且作为失败列表返回给前端
        // 1. 根据tag_id和product_id拿到重复的选项
        List<Map<String, Object>> listTagProductRel = tagProductRelMapper.getByProductAndTagId(para);

        // 2. 剔重
        for (int i = 0; i < listTagProductRel.size(); i++) {
            Map<String, Object> mTagPrdRel = listTagProductRel.get(i);
            String sPrdIdRes = mTagPrdRel.get("product_id").toString();
            Long tag_id = null;
            if(mTagPrdRel.get("tag_id")!=null){
                tag_id = Long.valueOf(mTagPrdRel.get("tag_id").toString());
            }

            Iterator it = productIdList.iterator();
            while (it.hasNext()) {
                String sPrdIdTarget = it.next().toString();
                if (sPrdIdTarget.equals(sPrdIdRes) || (tag.getTagType() == 2 &&vendorTagIds.contains(tag_id))) {
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
        mapToSave.put("tag_id", map.get("tag_id"));
        mapToSave.put("productIdList", map.get("productIdList"));
        mapToSave.put("sort_num", map.get("sort_num"));
        mapToSave.put("tagType",map.get("tagType"));

        return tagProductRelMapper.saveTagProductRel(mapToSave);
    }

    @Override
    public List<Tag> getTags(String orderBy) {
        return tagMapper.getTags(orderBy);
    }

    @Override
    public List<Tag> getTagsByParam(Map<String,Object> param) {
        if(param == null) return null;
        List<Tag> tags = tagMapper.getTagsByParam(param);
        return tags;
    }

    @Override
    public Tag selectTagByTagId(Long tagId) {
        if(tagId == null) return null;
        return tagMapper.selectByPrimaryKey(tagId);
    }

}
