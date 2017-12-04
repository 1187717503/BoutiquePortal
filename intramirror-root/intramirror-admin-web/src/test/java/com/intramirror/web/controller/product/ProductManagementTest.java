package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.web.common.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created on 2017/12/3.
 *
 * @author YouFeng.Zhu
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mockApplicationContext.xml" })
// @formatter:off
@SqlGroup({
          @Sql(scripts = "classpath:database/sha_ci_db_brand.sql"),
          @Sql(scripts = "classpath:database/sha_ci_db_category.sql"),
          @Sql(scripts = "classpath:database/sha_ci_db_product.sql"),
          @Sql(scripts = "classpath:database/sha_ci_db_product_exception.sql"),
          @Sql(scripts = "classpath:database/sha_ci_db_shop_product.sql"),
          @Sql(scripts = "classpath:database/sha_ci_db_sku.sql"),
          @Sql(scripts = "classpath:database/sha_ci_db_sku_store.sql"),
          @Sql(scripts = "classpath:database/sha_ci_db_tag.sql"),
          @Sql(scripts = "classpath:database/sha_ci_db_vendor.sql") })
// @formatter:on
public class ProductManagementTest {
    private Logger LOGGER = LoggerFactory.getLogger(ProductManagementTest.class);

    @Autowired
    private ProductMgntController productMgntController;

    @Test
    public void testStateCount() {
        Map<StateEnum, Long> exceptedMap = new HashMap<>();
        exceptedMap.put(StateEnum.NEW, 3L);
        exceptedMap.put(StateEnum.TRASH, 4L);
        exceptedMap.put(StateEnum.PROCESSING, 5L);
        exceptedMap.put(StateEnum.READY_TO_SELL, 5L);
        exceptedMap.put(StateEnum.SHOP_READY_TO_SELL, 5L);
        exceptedMap.put(StateEnum.SHOP_PROCESSING, 4L);
        exceptedMap.put(StateEnum.SHOP_ON_SALE, 5L);
        exceptedMap.put(StateEnum.SHOP_SOLD_OUT, 4L);
        exceptedMap.put(StateEnum.SHOP_REMOVED, 3L);
        exceptedMap.put(StateEnum.ALL, 38L);

        org.apache.ibatis.logging.LogFactory.useLog4JLogging();
        SearchCondition searchCondition = new SearchCondition();
        Response response = productMgntController.listAllProductStateCount(null, searchCondition);
        Map<StateEnum, Long> countMap = (Map<StateEnum, Long>) response.getData();
        Assert.assertTrue("Resopnse count error.", exceptedMap.size() == countMap.size());
        for (Map.Entry<StateEnum, Long> count : countMap.entrySet()) {
            Assert.assertSame(count.getKey() + " is not match ", exceptedMap.get(count.getKey()), count.getValue());
        }
    }

}
