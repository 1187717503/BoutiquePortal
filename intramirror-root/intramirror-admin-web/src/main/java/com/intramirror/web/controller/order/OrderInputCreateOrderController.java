package com.intramirror.web.controller.order;

import com.google.gson.Gson;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.OrderInputCreateOrderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderInputCreateOrderController extends BaseController {

    private static Logger logger = Logger.getLogger(OrderInputCreateOrderController.class);

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

        //调用service  事物管理
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
