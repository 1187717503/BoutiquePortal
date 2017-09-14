package com.intramirror.web.controller.order;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.main.api.service.*;
import com.intramirror.order.api.common.OrderCancelType;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.model.Logistics;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.Order;
import com.intramirror.order.api.model.OrderLogistics;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.LogisticsService;
import com.intramirror.order.api.service.OrderLogisticsService;
import com.intramirror.order.api.vo.InputCreateOrder;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ShopProductSkuService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.OrderInputCreateOrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderInputCreateOrderController extends BaseController {

    private static Logger logger = Logger.getLogger(OrderInputCreateOrderController.class);

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ILogisticsProductService logisticsProductService;

    @Autowired
    private IProductService productService;

    @Autowired
    private ShopProductSkuService shopProductSkuService;

    @Autowired
    private ISkuStoreService skuStoreService;

    @Autowired
    private OrderInputCreateOrderService orderInputCreateOrderService;


    /**
     * Wang
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/input_create_order", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> orderInputCreateOrder(@RequestBody Map<String, Object> paramMap, HttpServletRequest httpRequest) throws Exception {
        Map<String, Object> results = new HashMap<>();

        User user = this.getUserInfo(httpRequest);
        if (user == null) {
            results.put("error", "Please log in again");
            return results;
        }

        //调用service 创建 事物管理
        try {
            results = orderInputCreateOrderService.orderInputCreateOrder(paramMap, user);
        } catch (Exception e) {
            e.printStackTrace();
            results.put("info", "create order fail ");
            return results;
        }
        logger.info("接口执行结束,响应数据,result:" + new Gson().toJson(results));
        return results;
    }

}
