package com.intramirror.web.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.main.api.model.StockLocation;
import com.intramirror.main.api.service.StockLocationService;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.ShipEmailLog;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.vo.MailModelVO;
import com.intramirror.order.core.utils.MailSendManageService;
import com.intramirror.order.core.utils.MailSendUtil;
import com.intramirror.web.service.LogisticsProductService;
import com.intramirror.web.service.OrderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by caowei on 2018/11/9.
 */
@CrossOrigin
@Controller
@RequestMapping("/boutique/portal")
public class BoutiqueController {

    @Autowired
    OrderService orderService;
    @Autowired
    private LogisticsProductService logisticsProductService;
    @Autowired
    private ILogisticsProductService iLogisticsProductService;
    @Autowired
    private StockLocationService stockLocationService;
    @Autowired
    private MailSendManageService mailSendManageService;

    @RequestMapping(value = "/confirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage confirmOrder(@RequestBody Map<String, Object> map) {
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        Long logisticsProductId = null;
        String stockLocation = null;
        Long stockLocationId = null;

        if (map.get("logisticsProductId") != null && StringUtils.isNotBlank(map.get("logisticsProductId").toString())) {
            logisticsProductId = Long.valueOf(map.get("logisticsProductId").toString());
        }else {
            result.setMsg("logisticsProductId不能为空");
            result.setCode(400);
            return result;
        }
        if (map.get("stockLocation") != null && StringUtils.isNotBlank(map.get("stockLocation").toString())) {
            stockLocation = map.get("stockLocation").toString();
        }
        if (map.get("stockLocationId") != null && StringUtils.isNotBlank(map.get("stockLocationId").toString())) {
            stockLocationId = Long.valueOf(map.get("stockLocationId").toString());
        }else {
            result.setMsg("stockLocationId不能为空");
            result.setCode(400);
            return result;
        }
        List<Map<String, Object>> productConfirm = iLogisticsProductService.queryLogisticProductConfirm(logisticsProductId);
        if (CollectionUtils.isEmpty(productConfirm)){
            result.setCode(100);
            result.setMsg("不存在此订单信息");
            return result;
        }
        Map<String, Object> confirmMap = productConfirm.get(0);
        if (confirmMap.get("oeStatus")!=null&&"1".equals(confirmMap.get("oeStatus").toString())){
            result.setCode(410);
            result.setMsg("异常订单不能confirm");
            return result;
        }
        if (confirmMap.get("status")==null||(!"1".equals(confirmMap.get("status").toString())
                &&!"8".equals(confirmMap.get("status").toString()))){
            result.setCode(430);
            result.setMsg("订单状态异常，不能confirm");
            return result;
        }
        boolean flag = false; //stocklocation是否有效
        for (Map map1 :productConfirm){
            if (map1.get("locationId") != null
                    && stockLocationId.equals(Long.valueOf(map1.get("locationId").toString()))){
                stockLocation = map1.get("stockLocation").toString();
                flag = true;
                break;
            }
        }
        if (!flag){
            result.setCode(420);
            result.setMsg("stock location无效");
            return result;
        }
        try{
            if (logisticsProductId != null) {

                LogisticsProduct logis = logisticsProductService.selectById(logisticsProductId);

                result.successStatus();

                if (logis != null) {
                    orderService.confirmOrder(logis,stockLocation,stockLocationId);
                } else {
                    result.errorStatus().setMsg("Order does not exist,logisticsProductId:" + logisticsProductId);
                }
            }
        }catch (Exception e){
            result.errorStatus().setMsg(e.getMessage());
            result.setCode(100);
            return result;
        }
        return result;
    }

    @RequestMapping(value = "/getStockLocation", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage getStockLocation(Long vendorId) {
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        try {
            if (vendorId != null && vendorId > 0 ){
                List<StockLocation> stockLocationList = stockLocationService.getStockLocations(vendorId);
                List<Map<String,Object>> mapList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(stockLocationList)){
                    for (StockLocation stockLocation:stockLocationList){
                        Map<String,Object> map = new HashMap<>();
                        map.put("stockLocationId",stockLocation.getLocationId());
                        map.put("stockLocation",stockLocation.getStockLocation());
                        mapList.add(map);
                    }
                }
                result.successStatus();
                result.setData(mapList);
            }else {
                result.setMsg("Parameter error.");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query StockLocation list fail,Check parameters, please ");
            return result;
        }
        return result;
    }

    @RequestMapping(value = "/shipEmail", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage shipEmail(@RequestParam String shipmentNo) {
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        try{
            List<ShipEmailLog> emailLogs = mailSendManageService.getEmailLog(shipmentNo);
            if (CollectionUtils.isNotEmpty(emailLogs)){
                ShipEmailLog emailLog = emailLogs.get(0);
                String emailBody = emailLog.getEmailBody();
                MailModelVO modelVO = JSONObject.parseObject(emailBody, MailModelVO.class);
                MailSendUtil.sendMail(modelVO);

                //更新log
                emailLog.setIsDeleted(1);
                emailLog.setUpdateTime(new Date());
                mailSendManageService.updateEmailLog(emailLog);
                result.successStatus();
            }else {
                result.setMsg("emailLog为空");
            }
        }catch (Exception e){
            result.setMsg("shipmentNo："+shipmentNo +"，重推失败");
        }
        return result;
    }
}
