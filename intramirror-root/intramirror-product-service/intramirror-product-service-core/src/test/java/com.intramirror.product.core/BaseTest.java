package com.intramirror.product.core;

import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created on 2017/12/3.
 *
 * @author YouFeng.Zhu
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mockApplicationContext.xml" })
public class BaseTest {
    private Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
    @Autowired
    private ProductManagementService productManagementService;

    @Test
    public void justtest() {
        org.apache.ibatis.logging.LogFactory.useLog4JLogging();
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setProductStatus(1);
        searchCondition.setShopProductStatus(-1);
        List<Map<String, Object>> map = productManagementService.listAllProductCountGounpByState(searchCondition);
        LOGGER.info("{}", map);
    }
}
