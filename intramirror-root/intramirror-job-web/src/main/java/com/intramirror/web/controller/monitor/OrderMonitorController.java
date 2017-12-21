package com.intramirror.web.controller.monitor;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.EmailUtils;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.order.api.service.IOrderService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dingyifan on 2017/12/5.
 */
@Controller
@RequestMapping("/order/monitor")
public class OrderMonitorController implements InitializingBean {

    private static final Logger logger = Logger.getLogger(OrderMonitorController.class);

    private Map<String,Object> params;

    private List<String> consignees;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("/monitorCreateThreeOrder")
    public void monitorCreateThreeOrder(){
        try {
            List<Map<String,Object>> orderMaps = iOrderService.selectCreateThreeOrderInfo(params);
            logger.info("OrderMonitorController,monitorCreateThreeOrder,orderMaps:"+ JSONObject.toJSONString(orderMaps)+",params:"+JSONObject.toJSONString(params));

            if(orderMaps == null || orderMaps.size() == 0) {
                return;
            }

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<span style='background:yellow;font-weight:bold'>pending状态没有调用买手店下单接口:</span><br />");
            for(Map<String,Object> orderMap : orderMaps) {
                emailContent.append("       "
                        + "logistics_product_id:"+orderMap.get("logistics_product_id")
                        +",order_line_num:"+orderMap.get("order_line_num") + "<br />");
            }

            EmailUtils.sendTextEmails(consignees,"买手店下单定时监控",emailContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("OrderMonitorController,monitorCreateThreeOrder,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        params = new HashMap<>();
        String[] vendors = new String[]{"9","16","21","22","20"};
        String[] order_status = new String[]{"1","2","7","3","4","5","6"};
        params.put("vendors",vendors);
        params.put("order_status",order_status);

        consignees = new ArrayList<>();
        consignees.add("yifan.ding@intramirror.com");
        consignees.add("arthur.chen@intramirror.com");
        consignees.add("jian.shang@intramirror.com");
    }
}
