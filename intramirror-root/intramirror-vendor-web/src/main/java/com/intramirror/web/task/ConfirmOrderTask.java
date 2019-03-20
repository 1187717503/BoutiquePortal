package com.intramirror.web.task;

import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.vo.ConfirmOrderVO;
import com.intramirror.web.service.OrderService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by caowei on 2019/3/4.
 */
public class ConfirmOrderTask implements Callable<Boolean> {

    private static Logger logger = LoggerFactory.getLogger(ConfirmOrderTask.class);

    private ConfirmOrderVO orderVO;
    private OrderService orderService;
    private ILogisticsProductService iLogisticsProductService;


    public ConfirmOrderTask(ConfirmOrderVO orderVO, OrderService orderService,
                            ILogisticsProductService iLogisticsProductService) {
        this.orderVO = orderVO;
        this.orderService = orderService;
        this.iLogisticsProductService = iLogisticsProductService;
    }

    @Override
    public Boolean call() throws Exception {
        if (orderVO.getLogisticsProductId() == null){
            logger.error("LogisticsProductId不能为空");
            orderVO.setFailMsg("LogisticsProductId不能为空");
            return false;
        }
        Long logisticsProductId = orderVO.getLogisticsProductId();
        if (orderVO.getStockLocation() == null){
            logger.error("StockLocation不能为空");
            orderVO.setFailMsg("StockLocation不能为空");
            return false;
        }
        String stockLocation = orderVO.getStockLocation();
        if (orderVO.getStockLocationId() == null){
            logger.error("StockLocationId不能为空");
            orderVO.setFailMsg("StockLocation不能为空");
            return false;
        }
        Long stockLocationId = orderVO.getStockLocationId();
        LogisticsProduct logis = iLogisticsProductService.selectById(logisticsProductId);
        if (logis != null) {
            orderVO.setOrderLineNum(logis.getOrder_line_num());
        }
        List<Map<String, Object>> productConfirm = iLogisticsProductService.queryLogisticProductConfirm(logisticsProductId);
        if (CollectionUtils.isEmpty(productConfirm)){
            logger.error("不存在此订单信息,logisticsProductId:{}",logisticsProductId);
            orderVO.setFailMsg("不存在此订单信息,logisticsProductId:"+logisticsProductId);
            return false;
        }
        Map<String, Object> confirmMap = productConfirm.get(0);
        if (confirmMap.get("oeStatus")!=null&&"1".equals(confirmMap.get("oeStatus").toString())){
            logger.error("异常订单不能confirm,logisticsProductId:{}",logisticsProductId);
            orderVO.setFailMsg("异常订单不能confirm,logisticsProductId:"+logisticsProductId);
            return false;
        }
        if (confirmMap.get("status")==null||(!"1".equals(confirmMap.get("status").toString())
                &&!"8".equals(confirmMap.get("status").toString()))){
            logger.error("订单状态异常，不能confirm,logisticsProductId:{}",logisticsProductId);
            orderVO.setFailMsg("订单状态异常，不能confirm,logisticsProductId:"+logisticsProductId);
            return false;
        }
        boolean flag = false; //stockLocation是否有效
        for (Map map1 :productConfirm){
            if (map1.get("locationId") != null
                    && stockLocationId.equals(Long.valueOf(map1.get("locationId").toString()))){
                stockLocation = map1.get("stockLocation").toString();
                flag = true;
                break;
            }
        }
        if (!flag){
            logger.error("stockLocation无有效,logisticsProductId:{}",logisticsProductId);
            orderVO.setFailMsg("stockLocation无有效,logisticsProductId:"+logisticsProductId);
            return false;
        }
        try{
            orderService.confirmOrder(logis,stockLocation,stockLocationId);
        }catch (Exception e){
            logger.error(e.getMessage());
            orderVO.setFailMsg(e.getMessage());
            return false;
        }
        return true;
    }
}
