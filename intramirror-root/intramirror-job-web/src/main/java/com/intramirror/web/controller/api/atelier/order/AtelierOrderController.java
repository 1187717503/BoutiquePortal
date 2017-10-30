package com.intramirror.web.controller.api.atelier.order;

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.web.controller.api.atelier.AtelierUpdateByProductService;
import com.intramirror.web.service.order.OrderState;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dingyifan on 2017/10/18.
 */
@Controller
@RequestMapping("/")
public class AtelierOrderController {

    /** logger **/
    private static final Logger logger = Logger.getLogger(AtelierOrderController.class);

    private static final String atelier_version = "1.0";

    private static final String atelier_success = "SUCCESS";

    private static final String response_error = "1000";

    private static final String response_success = "2000";

    private static final String error_code_1 = "1";
    private static final String error_desc_1 ="E001: Order Number doesn't exist.";

    private static final String error_code_2 = "2";
    private static final String error_desc_2 ="E002: ";

    @Resource(name = "atelierUpdateByProductService")
    private AtelierUpdateByProductService atelierUpdateByProductService;

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private OrderState orderState;

    @RequestMapping("/getOrderByDate")
    @ResponseBody
    public Object getOrderByDate(
            @Param(value = "Version")String Version,
            @Param(value = "StoreID")String StoreID,
            @Param(value = "offset")String offset,
            @Param(value = "limit")String limit,
            @Param(value = "date")String date
            ){
        List<GetOrderByDateResultVO> result = new ArrayList<GetOrderByDateResultVO>();

        try {
            ApiInputParam apiInputParam = new ApiInputParam();
            String checkResult = apiInputParam.checkGetOrderByDateParams(Version,StoreID,offset,limit,date);
            logger.info("AtelierOrderControllerGetOrderByDate,checkGetOrderByDateParams,end,checkResult:"+checkResult+",apiInputParam:"+JSONObject.toJSONString(apiInputParam));

            if(checkResult.equals(atelier_success)) {

                Map<String,Object> conditionMap = new HashMap<>();
                conditionMap.put("create_at",apiInputParam.getDate());
                conditionMap.put("vendor_id",apiInputParam.getVendorId());
                conditionMap.put("offset",apiInputParam.getIntOffset());
                conditionMap.put("limit",apiInputParam.getIntLimit());

                logger.info("AtelierOrderControllerGetOrderByDate,start,atelierSelectOrder,conditionMap:"+JSONObject.toJSONString(conditionMap));
                List<Map<String,Object>> dataMap = iOrderService.atelierSelectOrder(conditionMap);
                logger.info("AtelierOrderControllerGetOrderByDate,end,atelierSelectOrder,conditionMap:"+JSONObject.toJSONString(conditionMap)+",dataMap:"+JSONObject.toJSONString(dataMap));

                logger.info("AtelierOrderControllerGetOrderByDate,start,convertByGetDate,dataMap:"+JSONObject.toJSONString(dataMap));
                result = this.convertByGetDate(dataMap);
                logger.info("AtelierOrderControllerGetOrderByDate,end,convertByGetDate,result:"+JSONObject.toJSONString(result)+",dataMap:"+JSONObject.toJSONString(dataMap));
                return result;
            }

            return this.result(response_error,error_code_2,error_desc_2+checkResult);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierOrderControllerGetOrderByDate,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AtelierOrderControllerGetOrderByDate,outputParams,result:"+JSONObject.toJSONString(result));
        return result;
    }


    @RequestMapping("/updateOrderStatus")
    @ResponseBody
    public Map<String,Object> updateOrderStatus(HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        try {
            InputStream is = request.getInputStream();
            String body = IOUtils.toString(is, "utf-8");
            String storeID = request.getParameter("StoreID");
            String version = request.getParameter("Version");
            logger.info("AtelierOrderControllerUpdateOrderStatus,inputParams,body:"+body+",storeID:"+storeID+",version:"+version);

            UpdateOrderStatusVO updateOrderStatusVO = new UpdateOrderStatusVO();
            String checkResult = updateOrderStatusVO.checkUpdateOrderStatusParams(body,storeID,version);
            logger.info("AtelierOrderControllerUpdateOrderStatus,checkUpdateOrderStatusParams,updateOrderStatusVO:"+JSONObject.toJSONString(
                    updateOrderStatusVO));

            if(atelier_success.equals(checkResult)) {
                String status = updateOrderStatusVO.getStatus();

                OrderState.StatusEnum statusEnum = null;
                OrderState.StateParams stateParams = new OrderState().getStateParams();
                stateParams.setExceptionTypeEnum(OrderState.ExceptionTypeEnum.INCORRECT_SYSTEM_INVENTORY);
                stateParams.setOrder_line_num(updateOrderStatusVO.getOrder_number());
                stateParams.setComments("Incorrect system inventory");

                if(status.equals(OrderState.StatusEnum.Confirmed.getCode())) {
                    statusEnum = OrderState.StatusEnum.Confirmed;
                } else if(status.equals(OrderState.StatusEnum.Cancelled.getCode())){
                    statusEnum = OrderState.StatusEnum.Cancelled;
                } else if(status.equals(OrderState.StatusEnum.Shipped.getCode())){
                    statusEnum = OrderState.StatusEnum.Shipped;
                }

                logger.info("AtelierOrderControllerUpdateOrderStatus,OrderStateChange,start,status:"+status+",stateParams:"+JSONObject.toJSONString(stateParams));
                ResultMessage resultMessage = orderState.change(statusEnum,stateParams).execute();
                logger.info("AtelierOrderControllerUpdateOrderStatus,OrderStateChange,end,status:"+status+",stateParams:"+JSONObject.toJSONString(stateParams)+",resultMessage:"+JSONObject.toJSONString(resultMessage));

                if(resultMessage.isSUCCESS()){
                    result = this.result(response_success,null,null);
                } else {
                    result = this.result(response_error,error_code_2,resultMessage.getMsg());
                }
            } else {
                result = this.result(response_error,error_code_2,error_desc_2+checkResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = this.result(response_error,error_code_2,error_desc_2+"ErrorMessage:"+e.getMessage());
            logger.info("AtelierOrderControllerUpdateOrderStatus,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AtelierOrderControllerUpdateOrderStatus,outputParams,result:"+JSONObject.toJSONString(result));
        return result;
    }

    public List<GetOrderByDateResultVO> convertByGetDate(List<Map<String,Object>> data){
        List<GetOrderByDateResultVO> result = new ArrayList<>();
        if(data != null && data.size() > 0) {

            for(Map<String,Object> map:data) {
                GetOrderByDateResultVO  getOrderByDateResultVO = new GetOrderByDateResultVO();
                String order_line_num = map.get("order_line_num") == null?"":map.get("order_line_num").toString();
                String product_code = map.get("product_code") == null?"":map.get("product_code").toString();
                String value = map.get("value") == null?"":map.get("value").toString();
                String in_price = map.get("in_price") == null?"":map.get("in_price").toString();
                String created_at = map.get("created_at") == null?"":map.get("created_at").toString();

                getOrderByDateResultVO.setBoutique_id(product_code);
                getOrderByDateResultVO.setCreate_date(created_at);
                getOrderByDateResultVO.setOrder_number(order_line_num);
                getOrderByDateResultVO.setPurchase_price(new BigDecimal(in_price));
                getOrderByDateResultVO.setQty(1);
                getOrderByDateResultVO.setSize(value);
                result.add(getOrderByDateResultVO);
            }
        }
        return result;
    }

    private Map<String,Object> result(String responseCode,String errorCode,String errorMsg){
        Map<String,Object> responseMap = new HashMap<>();
        Map<String,Object> errorMap = new HashMap<>();

        responseMap.put("responseCode",responseCode);

        if(responseCode.equals(response_error)) {
            errorMap.put("errorCode",errorCode);
            errorMap.put("errorMsg",errorMsg);
            errorMap.put("TimeStamp", DateUtils.getStrDate(new Date(),"yyyy-MM -hh HH:mm:ss"));

            responseMap.put("Error",errorMap);
        }
        return responseMap;
    }

    class ApiInputParam{
        private String version;
        private String storeId;
        private String offset;
        private String limit;
        private String date;

        private int intOffset;
        private int intLimit;
        private Date paramDate;
        private Long vendorId;

        public Long getVendorId() {
            return vendorId;
        }

        public void setVendorId(Long vendorId) {
            this.vendorId = vendorId;
        }

        public Date getParamDate() {
            return paramDate;
        }

        public void setParamDate(Date paramDate) {
            this.paramDate = paramDate;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public String getLimit() {
            return limit;
        }

        public void setLimit(String limit) {
            this.limit = limit;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getIntOffset() {
            return intOffset;
        }

        public void setIntOffset(int intOffset) {
            this.intOffset = intOffset;
        }

        public int getIntLimit() {
            return intLimit;
        }

        public void setIntLimit(int intLimit) {
            this.intLimit = intLimit;
        }

        private String checkGetOrderByDateParams(String version, String storeId, String offset, String limit, String date){
            this.version = version;
            this.storeId = storeId;
            this.offset = offset;
            this.limit = limit;
            this.date = date;

            logger.info("checkGetOrderByDateParams,start,inputParams:"+ JSONObject.toJSONString(this));
            if(StringUtils.isBlank(version) || !atelier_version.equals(version)) {
                return "Version error";
            }

            if(StringUtils.isBlank(storeId)) {
                return "StoreID error";
            }

            if(StringUtils.isBlank(offset)) {
                this.intOffset = 0;
            } else {
                this.intOffset = Integer.parseInt(offset);
            }

            if(StringUtils.isBlank(limit)) {
                this.intLimit = 20;
            } else {
                this.intLimit = Integer.parseInt(limit);
                if(this.intLimit > 500) {
                    this.intLimit = 500;
                }
            }

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                this.paramDate = format.parse(date);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("checkGetOrderByDateParams,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
                return "date error";
            }

            Map<String,Object> paramMap = atelierUpdateByProductService.getParamsMap();
            if(paramMap.get(storeId) == null) {
                return "StoreID error";
            }
            Map<String,Object> dataMap = (Map<String, Object>) paramMap.get(storeId);
            this.vendorId = Long.parseLong(dataMap.get("vendor_id").toString());
            logger.info("checkGetOrderByDateParams,end,inputParams:"+ JSONObject.toJSONString(this));
            return atelier_success;
        }
    }
    class GetOrderByDateResultVO{
        private String order_number;
        private String create_date;
        private String boutique_id;
        private String size;
        private int qty;
        private BigDecimal purchase_price;

        public String getOrder_number() {
            return order_number;
        }

        public void setOrder_number(String order_number) {
            this.order_number = order_number;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public String getBoutique_id() {
            return boutique_id;
        }

        public void setBoutique_id(String boutique_id) {
            this.boutique_id = boutique_id;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public BigDecimal getPurchase_price() {
            return purchase_price;
        }

        public void setPurchase_price(BigDecimal purchase_price) {
            this.purchase_price = purchase_price;
        }
    }
    class UpdateOrderStatusVO {
        private String Version;
        private String StoreID;
        private String order_number;
        private String status;
        private String tracking_number;
        private String invoice_number;
        private Long vendorId;

        public String getOrder_number() {
            return order_number;
        }

        public void setOrder_number(String order_number) {
            this.order_number = order_number;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTracking_number() {
            return tracking_number;
        }

        public void setTracking_number(String tracking_number) {
            this.tracking_number = tracking_number;
        }

        public String getInvoice_number() {
            return invoice_number;
        }

        public void setInvoice_number(String invoice_number) {
            this.invoice_number = invoice_number;
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String version) {
            Version = version;
        }

        public String getStoreID() {
            return StoreID;
        }

        public void setStoreID(String storeID) {
            StoreID = storeID;
        }

        public Long getVendorId() {
            return vendorId;
        }

        public void setVendorId(Long vendorId) {
            this.vendorId = vendorId;
        }

        public String checkUpdateOrderStatusParams(String body,String storeId,String version){
            if(StringUtils.isBlank(body)) {
                return "body is null.";
            }

            if(StringUtils.isBlank(storeId)){
                return "StoreID is null.";
            }

            if(StringUtils.isBlank(version)) {
                return "version is null.";
            }

            Map<String,Object> paramMap = atelierUpdateByProductService.getParamsMap();
            if(paramMap.get(storeId) == null) {
                return "StoreID error.";
            }

            Map<String,Object> dataMap = (Map<String, Object>) paramMap.get(storeId);
            this.vendorId = Long.parseLong(dataMap.get("vendor_id").toString());

            JSONObject jsonObjectBody = JSON.parseObject(body);
            this.order_number = jsonObjectBody.getString("order_number");
            this.status = jsonObjectBody.getString("status");

            if(StringUtils.isBlank(this.order_number)) {
                return "order_number is null.";
            }

            if(StringUtils.isBlank(this.status)) {
                return "status is null.";
            }

            if(!(OrderState.StatusEnum.Cancelled.getCode().equals(this.status) || OrderState.StatusEnum.Confirmed.getCode().equals(this.status) || OrderState.StatusEnum.Shipped.getCode().equals(this.status))) {
                return "status is error.";
            }

            this.tracking_number = jsonObjectBody.getString("tracking_number");
            this.invoice_number = jsonObjectBody.getString("invoice_number");

            return atelier_success;
        }
    }
}
