package com.intramirror.web.controller.order;

import com.alibaba.fastjson.JSON;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.Map;

/**
 * <一句话功能简述>
 *     订单功能相关测试
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles({"spring.profiles.active","sit"})
@ContextHierarchy({
        @ContextConfiguration(locations = {"classpath:spring/app-beans.xml"}),
        @ContextConfiguration(locations = {"classpath:freemarker/spring-servlet.xml"}),
})
public class OrderControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws ServletException {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * 状态机接口测试
     * @throws Exception
     */
    @Ignore
    @Test
    public void updateOrderStatusTest() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put("logisProductId", 109);
        map.put("status", 2);

        String request = JSON.toJSONString(map);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/updateOrderStatus")
                .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(1)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

}
