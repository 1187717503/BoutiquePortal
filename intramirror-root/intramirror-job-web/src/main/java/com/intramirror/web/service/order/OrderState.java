package com.intramirror.web.service.order;

import com.alibaba.fastjson15.JSONObject;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.Order;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderExceptionService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.product.api.service.ISkuStoreService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by dingyifan on 2017/10/25.
 */
@Component(value = "orderState")
public class OrderState {

    /** logger */
    private static final Logger logger = Logger.getLogger(OrderState.class);

    private OrderState.StatusEnum statusEnum;

    private OrderState.StateParams stateParams;

    private Map<String,Object> dataMap ;

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private IOrderExceptionService orderExceptionService;

    @Autowired
    private ILogisticsProductService iLogisticsProductService;

    @Autowired
    private ISkuStoreService skuStoreService;

    public OrderState change(OrderState.StatusEnum changeStatusEnum,OrderState.StateParams stateParams){
        this.statusEnum = changeStatusEnum;
        this.stateParams = stateParams;

        Map<String,Object> conditionMap = new HashMap<>();
        conditionMap.put("order_line_num",stateParams.getOrder_line_num());
        List<Map<String,Object>> listMap = iOrderService.selectOrderInfo(conditionMap);
        logger.info("OrderState,conditionMap,listMap:"+ JSONObject.toJSONString(listMap));
        if(listMap != null && listMap.size() > 0) {
            dataMap = listMap.get(0);
        }
        return this;
    }

    public ResultMessage execute() throws Exception {

        ResultMessage resultMessage = new ResultMessage();

        // checked
        if(dataMap == null || dataMap.size() == 0) {
            return resultMessage.errorStatus().addMsg("Number doesn't exist!");
        }

        // 分发请求
        if(statusEnum.getCode().equals(StatusEnum.Confirmed.getCode())) {
            return confirmed();
        } else if(statusEnum.getCode().equals(StatusEnum.Cancelled.getCode())) {
            return cancelled();
        } else if(statusEnum.getCode().equals(StatusEnum.Shipped.getCode())) {
            return Shipped();
        }

        return resultMessage.errorStatus().addMsg("Number doesn't exist!");
    }

    private ResultMessage cancelled() throws Exception{
        String orderStatus = dataMap.get("logis_status").toString();
        String logistics_product_id = dataMap.get("logistics_product_id").toString();

        if(orderStatus.equals(StatusEnum.Pending.getValue())) {
            Map<String,Object> insertMap = new HashMap<>();
            insertMap.put("logistics_product_id",logistics_product_id);
            insertMap.put("comments",stateParams.getComments());
            insertMap.put("order_exception_type_id",stateParams.getExceptionTypeEnum().getValue());
            insertMap.put("status",1);
            insertMap.put("created_at",new Date());
            insertMap.put("created_by_user_id",stateParams.getUser_id());
            logger.info("OrderState,cancelled,insertMap:"+JSONObject.toJSONString(insertMap));
            orderExceptionService.saveOrderComments(insertMap);
            return ResultMessage.getInstance().successStatus();
        }

        return ResultMessage.getInstance().errorStatus().addMsg("cancelled. Status is not allowed to flow!").setData(dataMap);
    }

    private ResultMessage confirmed() throws Exception {
        String logisStatus = dataMap.get("logis_status").toString();
        String logistics_product_id = dataMap.get("logistics_product_id").toString();
        String orderId = dataMap.get("order_id").toString();
        if(logisStatus.equals(StatusEnum.Pending.getValue())) {

            skuStoreService.updateConfirmStoreByShopProductSkuId(Long.parseLong(logistics_product_id));

            LogisticsProduct logisticsProduct = new LogisticsProduct();
            logisticsProduct.setLogistics_product_id(Long.parseLong(logistics_product_id));
            logisticsProduct.setStatus(Integer.parseInt(StatusEnum.Confirmed.getCode()));
            iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);

            Order order = new Order();
            order.setOrderId(Long.parseLong(orderId));
            order.setStatus(Integer.parseInt(StatusEnum.Confirmed.getCode()));
            iOrderService.updateOrder(order);
            return ResultMessage.getInstance().successStatus();
        }
        return ResultMessage.getInstance().errorStatus().addMsg("confirmed. Status is not allowed to flow!").setData(dataMap);
    }

    private ResultMessage Shipped() throws Exception{
        String logisStatus = dataMap.get("logis_status").toString();
        String logistics_product_id = dataMap.get("logistics_product_id").toString();
        if(logisStatus.equals(StatusEnum.Confirmed.getValue())) {

            LogisticsProduct logisticsProduct = new LogisticsProduct();
            logisticsProduct.setLogistics_product_id(Long.parseLong(logistics_product_id));
            logisticsProduct.setTracking_num(stateParams.getTracking_num());
            logisticsProduct.setVat_num(stateParams.getVat_num());
            logisticsProduct.setStatus(Integer.parseInt(StatusEnum.Shipped.getCode()));
            iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);
        }
        return ResultMessage.getInstance().errorStatus().addMsg("confirmed. Status is not allowed to flow!").setData(dataMap);
    }

    public enum StatusEnum {
        Confirmed("confirmed","2","待包装"),
        Cancelled("cancelled","6","已取消"),
        Shipped("shipped","3","待收货"),
        Pending("pending","1","待确认");

        private String code;
        private String value;
        private String desc;

        StatusEnum(String code, String value,String desc) {
            this.code = code;
            this.value = value;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }

    public enum ExceptionTypeEnum {
        OUT_OF_STOCK("OUT_OF_STOCK","1",""),
        INCORRECT_SYSTEM_INVENTORY("INCORRECT_SYSTEM_INVENTORY","3",""),
        FAILED_TO_PLACE_ORDER("FAILED_TO_PLACE_ORDER","4","");

        private String code;
        private String value;
        private String desc;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        ExceptionTypeEnum(String code, String value, String desc) {
            this.code = code;
            this.value = value;
            this.desc = desc;
        }
    }

    public StateParams getStateParams(){
        return new StateParams();
    }

    public class StateParams{

        // required
        private String order_line_num;

        // required(Cancelled)
        private OrderState.ExceptionTypeEnum exceptionTypeEnum;

        // required(Cancelled)
        private String comments;

        // required(Cancelled)
        private Long user_id;

        // required(Shiped)
        private String tracking_num;

        // required(Shiped)
        private String vat_num;

        public String getOrder_line_num() {
            return order_line_num;
        }

        public void setOrder_line_num(String order_line_num) {
            this.order_line_num = order_line_num;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public ExceptionTypeEnum getExceptionTypeEnum() {
            return exceptionTypeEnum;
        }

        public void setExceptionTypeEnum(ExceptionTypeEnum exceptionTypeEnum) {
            this.exceptionTypeEnum = exceptionTypeEnum;
        }

        public Long getUser_id() {
            return user_id;
        }

        public void setUser_id(Long user_id) {
            this.user_id = user_id;
        }

        public String getTracking_num() {
            return tracking_num;
        }

        public void setTracking_num(String tracking_num) {
            this.tracking_num = tracking_num;
        }

        public String getVat_num() {
            return vat_num;
        }

        public void setVat_num(String vat_num) {
            this.vat_num = vat_num;
        }
    }
}
