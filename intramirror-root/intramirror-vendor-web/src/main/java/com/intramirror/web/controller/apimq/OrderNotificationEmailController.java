package com.intramirror.web.controller.apimq;

import com.alibaba.fastjson.JSONArray;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.constants.Week;
import com.intramirror.user.api.eo.EmailContentType;
import com.intramirror.user.api.model.OrderNotificationEmail;
import com.intramirror.user.api.model.OrderTime;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.user.api.service.vendor.OrderNotificationEmailService;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/orderNotificationEmail")
public class OrderNotificationEmailController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(StockLocationController.class);

    @Autowired
    private OrderNotificationEmailService orderNotificationEmailService;
    @Autowired
    private VendorService vendorService;

    @RequestMapping(value = "/getOrderNotificationEmailList", method = RequestMethod.GET)
    @ResponseBody
    public Map getOrderNotificationEmailList(HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = super.getUser(httpRequest);
            Vendor vendor = vendorService.getVendorByUserId(user.getUserId());
            List<OrderNotificationEmail> list = orderNotificationEmailService.selectByVendorId(vendor.getVendorId());
            result.put("status", StatusType.SUCCESS);
            result.put("data",list);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("查询orderNotificationEmail列表异常："+e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/getEmailContentType", method = RequestMethod.GET)
    @ResponseBody
    public Map getEmailContentType(HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("status", StatusType.SUCCESS);
            result.put("data",EmailContentType.toLinkedHashMap());
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("查询orderNotificationEmail列表异常："+e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Map create(HttpServletRequest httpRequest, @RequestBody OrderNotificationEmail orderNotificationEmail) {
        logger.info("创建orderNotificationEmail："+JsonTransformUtil.toJson(orderNotificationEmail));
        Map<String, Object> result = new HashMap<>();
        try {
            Date now = new Date();
            orderNotificationEmail.setCreatedAt(now);
            orderNotificationEmail.setUpdatedAt(now);
            orderNotificationEmailService.create(orderNotificationEmail);
            result.put("status", StatusType.SUCCESS);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("创建orderNotificationEmail异常："+e.getMessage(),e);
        }
        return result;
    }
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    @ResponseBody
    public Map modify(HttpServletRequest httpRequest,@RequestBody OrderNotificationEmail orderNotificationEmail) {
        logger.info("更新orderNotificationEmail："+JsonTransformUtil.toJson(orderNotificationEmail));
        Map<String, Object> result = new HashMap<>();
        if(!checkOrderTime(orderNotificationEmail.getOrderTime())){

        }
        try {
            Date now = new Date();
            orderNotificationEmail.setUpdatedAt(now);
            orderNotificationEmailService.modify(orderNotificationEmail);
            result.put("status", StatusType.SUCCESS);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("修改orderNotificationEmail异常："+e.getMessage(),e);
        }
        return result;
    }
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Map delete(HttpServletRequest httpRequest,@RequestParam Long orderNotificationEmailId) {
        logger.info("删除orderNotificationEmail："+orderNotificationEmailId);
        Map<String, Object> result = new HashMap<>();
        try {
            OrderNotificationEmail orderNotificationEmail = new OrderNotificationEmail();
            orderNotificationEmail.setEnabled(false);
            orderNotificationEmail.setOrderNotificationEmailId(orderNotificationEmailId);
            orderNotificationEmailService.modify(orderNotificationEmail);
            result.put("status", StatusType.SUCCESS);
        } catch (Exception e) {
            result.put("status", StatusType.FAILURE);
            logger.error("删除orderNotificationEmail异常："+e.getMessage(),e);
        }
        return result;
    }

    /**
     * 判断时间段有没有交集
     * @param orderTime
     * @return
     */
    private boolean checkOrderTime(String orderTime){
        List<OrderTime> orderTimeList = JSONArray.parseArray(orderTime,OrderTime.class);
        if(orderTimeList!=null&&orderTimeList.size()>0&&orderTimeList.size()==1)
            return true;
        for(int i=0;i<orderTimeList.size();i++){
            int baseStartTime = toIntByTime(orderTimeList.get(i).getStartTime());
            int baseEndTime = toIntByTime(orderTimeList.get(i).getEndTime());
            for(int j=i+1;j<orderTimeList.size();j++){
                int startTime = toIntByTime(orderTimeList.get(j).getStartTime());
                int endTime = toIntByTime(orderTimeList.get(j).getEndTime());
                if(baseEndTime<=startTime||baseStartTime>=endTime){
                    continue;
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    private int toIntByTime(String time){
        //Monday 00:00
        String week = time.split(" ")[0];
        String hourMin = time.split(" ")[1].trim().replace(":","");
        int weekDay = 0;
        if(Week.Monday.name().equals(week)){
            weekDay = 1;
        }else if(Week.Tuesday.name().equals(week)){
            weekDay = 2;
        }else if(Week.Wednesday.name().equals(week)){
            weekDay = 3;
        }else if(Week.Thursday.name().equals(week)){
            weekDay = 4;
        }else if(Week.Friday.name().equals(week)){
            weekDay = 5;
        }else if(Week.Saturday.name().equals(week)){
            weekDay = 6;
        }else if(Week.Sunday.name().equals(week)){
            weekDay = 7;
        }
        return weekDay+Integer.parseInt(hourMin);
    }

}
