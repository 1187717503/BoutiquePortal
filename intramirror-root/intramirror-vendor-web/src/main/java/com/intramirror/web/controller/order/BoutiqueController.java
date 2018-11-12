package com.intramirror.web.controller.order;

import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.main.api.model.StockLocation;
import com.intramirror.main.api.service.StockLocationService;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.web.service.LogisticsProductService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caowei on 2018/11/9.
 */
@CrossOrigin
@Controller
@RequestMapping("/boutique/portal")
public class BoutiqueController {

    @Autowired
    IOrderService orderService;
    @Autowired
    private LogisticsProductService logisticsProductService;
    @Autowired
    private ILogisticsProductService iLogisticsProductService;
    @Autowired
    private StockLocationService stockLocationService;

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
        if (confirmMap.get("status")==null||!"1".equals(confirmMap.get("status").toString())
                ||!"8".equals(confirmMap.get("status").toString())){
            result.setCode(430);
            result.setMsg("订单状态异常，不能confirm");
            return result;
        }
        boolean flag = false; //stocklocation是否有效
        for (Map map1 :productConfirm){
            if (map1.get("locationId") != null
                    && stockLocationId.equals(Integer.valueOf(map1.get("locationId").toString()))){
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
                    LogisticsProduct upLogis = new LogisticsProduct();
                    upLogis.setLogistics_product_id(logis.getLogistics_product_id());
                    if (stockLocation != null) {
                        upLogis.setStock_location(stockLocation);
                    }

                    if (stockLocationId !=null ){
                        upLogis.setStock_location_id(stockLocationId);
                    }
                    upLogis.setConfirmed_at(Helper.getCurrentUTCTime());

                    upLogis.setOrder_line_num(logis.getOrder_line_num());
                    //确认订单
                    Integer oldStatus = logis.getStatus();
                    logisticsProductService.confirmOrder(upLogis);

                    iLogisticsProductService.updateByLogisticsProduct4Jpush(oldStatus,upLogis);

                    //会员系统积分
                    logisticsProductService.updateMemberCredits(logis.getOrder_line_num());
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
}
