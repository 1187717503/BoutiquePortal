package com.intramirror.web.controller.monitor;

import com.intramirror.common.help.EmailUtils;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.product.api.service.IProductService;
import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping("/product/monitor")
public class ProductMonitorController implements InitializingBean {

    private static final Logger logger = Logger.getLogger(ProductMonitorController.class);

    private List<String> consignees;

    @Autowired
    private IProductService iProductService;

    @RequestMapping("/monitorProductUpdate")
    public void monitorProductUpdate(){
        String currentDate = DateUtils.getStrDate(new Date(),"yyyy-MM-dd");

        String lastCheck = currentDate+" 00:00:00";
        String sLastCheck = currentDate+" 00:00:00";
        String eLastCheck = currentDate+" 24:00:00";

        try {
            Map<String,Object> updateParams = new HashMap<>();
            updateParams.put("sLastCheck",sLastCheck);
            updateParams.put("eLastCheck",eLastCheck);

            Map<String,Object> noUpdateParams = new HashMap<>();
            noUpdateParams.put("lastCheck",lastCheck);

            List<Map<String,Object>> updateMap = iProductService.selectDayUpdateSum(updateParams);
            List<Map<String,Object>> noUpdateMap = iProductService.selectDayNoUpdateSum(noUpdateParams);

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<table cellspacing='10' >");
            emailContent.append("<tr><td>今天已更新商品</td></tr>");
            emailContent.append("<tr>");
            emailContent.append("<td>vendor</td>");
            emailContent.append("<td>sum</td>");
            emailContent.append("<tr/>");
            if(updateMap != null && updateMap.size() > 0) {
                for(Map<String,Object> map : updateMap) {
                    emailContent.append("<tr>");
                    emailContent.append("<td>"+map.get("vendor_name")+"</td>");
                    emailContent.append("<td>"+map.get("num")+"</td>");
                    emailContent.append("<tr/>");
                }
            } else {
                emailContent.append("<tr><td colspan='2'>查无数据</td></tr>");
            }
            emailContent.append("</table>");

            emailContent.append("  <hr />");


            emailContent.append("<table cellspacing='10' >");
            emailContent.append("<tr><td colspan='2' style='background:yellow;font-weight:bold'>警告!!! 今天未更新商品且库存大于0</td></tr>");
            emailContent.append("<tr>");
            emailContent.append("<td>vendor</td>");
            emailContent.append("<td>sum</td>");
            emailContent.append("<tr/>");
            if(noUpdateMap != null && noUpdateMap.size() >0) {

                for(Map<String,Object> map : noUpdateMap) {
                    emailContent.append("<tr>");
                    emailContent.append("<td>"+map.get("vendor_name")+"</td>");
                    emailContent.append("<td>"+map.get("num")+"</td>");
                    emailContent.append("<tr/>");
                }
            } else {
                emailContent.append("<tr><td colspan='2'>查无数据</td></tr>");
            }
            emailContent.append("</table>");
            EmailUtils.sendTextEmails(consignees,"JOB定时监控商品更新",emailContent.toString());

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("OrderMonitorController,monitorProductUpdate,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        consignees = new ArrayList<>();
        consignees.add("yifan.ding@intramirror.com");
        consignees.add("arthur.chen@intramirror.com");
    }
}
