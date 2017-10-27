package com.intramirror.web.controller.product;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 * Created on 2017/10/26.
 *
 * @author YouFeng.Zhu
 */
public class TestStateMachineCoreRule {
    @Test
    public void testMapMatchState() {
        for (StateEnum productState : StateEnum.values()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("product_status", String.valueOf(productState.getProductStatus()));
            map.put("shop_product_status", productState.getShopProductStatus() == -1 ? null : String.valueOf(productState.getShopProductStatus()));
            assertSame(productState.name() + " not match", productState, StateMachineCoreRule.map2StateEnum(map));
        }
    }

    @Test
    public void testCheckRule(){

    }
}
