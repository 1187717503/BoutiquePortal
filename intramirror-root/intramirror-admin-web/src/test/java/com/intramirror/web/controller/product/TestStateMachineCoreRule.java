package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.Block;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.model.TagProductRel;
import com.intramirror.web.common.request.Content;
import com.intramirror.utils.transform.JsonTransformUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2017/10/26.
 *
 * @author YouFeng.Zhu
 */
public class TestStateMachineCoreRule {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestStateMachineCoreRule.class);

    @Test
    public void testMapMatchState() {
        for (StateEnum productState : StateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("product_status", String.valueOf(productState.getProductStatus()));
            map.put("shop_product_status", productState.getShopProductStatus() == -1 ? null : String.valueOf(productState.getShopProductStatus()));
            assertSame(productState.name() + " not match", productState, StateMachineCoreRule.map2StateEnum(map));
        }
    }

    @Test
    public void testCheckRule() {
        Content content = new Content();
        Block block = new Block();
        Tag tag = new Tag();
        block.setBlockId(1L);
        block.setBlockName("name");
        block.setBgColor("red");
        block.setSortOrder(2);
        block.setContent("this is content".getBytes());
        block.setTitle("title");
        block.setSubtitle("sub");
        content.setBlock(block);

        tag.setTagId(1L);
        content.setTag(tag);

        List<TagProductRel> list = new ArrayList<>();
        for (int i = 10; i < 20; i++) {
            TagProductRel tagProductRel = new TagProductRel();
            tagProductRel.setTagId(1L);
            tagProductRel.setProductId((long) i);
            tagProductRel.setSortNum((long) (i - 9));
            list.add(tagProductRel);
        }
        content.setSort(list);

        String json = JsonTransformUtil.toJson(content);
        LOGGER.info(json);

    }
}
