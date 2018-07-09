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
import java.text.DateFormat;
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

    /**
     * logger
     **/
    private static final Logger logger = Logger.getLogger(AtelierOrderController.class);

    private static final String atelier_version = "1.0";

    private static final String atelier_success = "SUCCESS";

    private static final String response_error = "2000";

    private static final String response_success = "1000";

    private static final String error_code_1 = "1";
    private static final String error_desc_1 = "E001: ";

    @Resource(name = "atelierUpdateByProductService")
    private AtelierUpdateByProductService atelierUpdateByProductService;

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private OrderState orderState;

    @RequestMapping("/getOrderByDate")
    @ResponseBody
    public Object getOrderByDate(@Param(value = "Version") String Version, @Param(value = "StoreID") String StoreID, @Param(value = "offset") String offset,
            @Param(value = "limit") String limit, @Param(value = "date") String date, @Param(value = "status") String status,
            @Param(value = "barcode") String barcode) {
        List<GetOrderByDateResultVO> result = new ArrayList<GetOrderByDateResultVO>();

        try {
            ApiInputParam apiInputParam = new ApiInputParam();
            String checkResult = apiInputParam.checkGetOrderByDateParams(Version, StoreID, offset, limit, date);
            logger.info("AtelierOrderControllerGetOrderByDate,checkGetOrderByDateParams,end,checkResult:" + checkResult + ",apiInputParam:" + JSONObject
                    .toJSONString(apiInputParam));

            if (checkResult.equals(atelier_success)) {

                Map<String, Object> conditionMap = new HashMap<>();
                conditionMap.put("create_at", apiInputParam.getDate());
                conditionMap.put("vendor_id", apiInputParam.getVendorId());
                conditionMap.put("offset", apiInputParam.getIntOffset());
                conditionMap.put("limit", apiInputParam.getIntLimit());
                if (StringUtils.isNotBlank(status)) {
                    String[] strs = status.split(",");

                    String newStatus = "";
                    for (String str : strs) {

                        if (str.trim().equalsIgnoreCase("pending payment")) {
                            newStatus += ",-1";
                        }
                        if (str.trim().equalsIgnoreCase("invalid")) {
                            newStatus += ",-2";
                        }
                        if (str.trim().equalsIgnoreCase("payment submit success")) {
                            newStatus += ",-3";
                        }
                        if (str.trim().equalsIgnoreCase("refund submit")) {
                            newStatus += ",-4";
                        }
                        if (str.trim().equalsIgnoreCase("payment exception")) {
                            newStatus += ",-5";
                        }
                        if (str.trim().equalsIgnoreCase("pending")) {
                            newStatus += ",1";
                        }
                        if (str.trim().equalsIgnoreCase("confirmed")) {
                            newStatus += ",2";
                        }
                        if (str.trim().equalsIgnoreCase("shipped")) {
                            newStatus += ",3";
                        }
                        if (str.trim().equalsIgnoreCase("delivered")) {
                            newStatus += ",4";
                        }
                        if (str.trim().equalsIgnoreCase("closed")) {
                            newStatus += ",5";
                        }
                        if (str.trim().equalsIgnoreCase("cancelled")) {
                            newStatus += ",6";
                        }
                        if (str.trim().equalsIgnoreCase("ready to ship")) {
                            newStatus += ",7";
                        }
                        if (str.trim().equalsIgnoreCase("picking")) {
                            newStatus += ",8";
                        }
                    }

                    conditionMap.put("status", newStatus.split(","));
                } else {
                    conditionMap.put("status", status);
                }
                conditionMap.put("barcode", barcode);

                logger.info("AtelierOrderControllerGetOrderByDate,start,atelierSelectOrder,conditionMap:" + JSONObject.toJSONString(conditionMap));
                List<Map<String, Object>> dataMap = iOrderService.atelierSelectOrder(conditionMap);
                logger.info("AtelierOrderControllerGetOrderByDate,end,atelierSelectOrder,conditionMap:" + JSONObject.toJSONString(conditionMap) + ",dataMap:"
                        + JSONObject.toJSONString(dataMap));

                logger.info("AtelierOrderControllerGetOrderByDate,start,convertByGet Date,dataMap:" + JSONObject.toJSONString(dataMap));
                result = this.convertByGetDate(dataMap);
                logger.info("AtelierOrderControllerGetOrderByDate,end,convertByGetDate,result:" + JSONObject.toJSONString(result) + ",dataMap:" + JSONObject
                        .toJSONString(dataMap));
                return result;
            }

            return this.result(response_error, error_code_1, error_desc_1 + checkResult);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierOrderControllerGetOrderByDate,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AtelierOrderControllerGetOrderByDate,outputParams,result:" + JSONObject.toJSONString(result));
        return result;
    }

    @RequestMapping("/updateOrderStatus")
    @ResponseBody
    public Map<String, Object> updateOrderStatus(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            InputStream is = request.getInputStream();
            String body = IOUtils.toString(is, "utf-8");
            String storeID = request.getParameter("StoreID");
            String version = request.getParameter("Version");
            logger.info("AtelierOrderControllerUpdateOrderStatus,inputParams,body:" + body + ",storeID:" + storeID + ",version:" + version);

            UpdateOrderStatusVO updateOrderStatusVO = new UpdateOrderStatusVO();
            String checkResult = updateOrderStatusVO.checkUpdateOrderStatusParams(body, storeID, version);
            logger.info(
                    "AtelierOrderControllerUpdateOrderStatus,checkUpdateOrderStatusParams,updateOrderStatusVO:" + JSONObject.toJSONString(updateOrderStatusVO));

            if (atelier_success.equals(checkResult)) {
                String status = updateOrderStatusVO.getStatus();

                OrderState.StatusEnum statusEnum = null;
                OrderState.StateParams stateParams = new OrderState().getStateParams();
                stateParams.setExceptionTypeEnum(OrderState.ExceptionTypeEnum.INCORRECT_SYSTEM_INVENTORY);
                stateParams.setOrder_line_num(updateOrderStatusVO.getOrder_number());
                stateParams.setComments("Incorrect system inventory");
                stateParams.setVendor_id(updateOrderStatusVO.getVendorId());
                stateParams.setTracking_num(updateOrderStatusVO.getTracking_number());
                stateParams.setVat_num(updateOrderStatusVO.getInvoice_number());

                if (status.equals(OrderState.StatusEnum.Confirmed.getCode())) {
                    statusEnum = OrderState.StatusEnum.Confirmed;
                } else if (status.equals(OrderState.StatusEnum.Cancelled.getCode())) {
                    statusEnum = OrderState.StatusEnum.Cancelled;
                } else if (status.equals(OrderState.StatusEnum.Shipped.getCode())) {
                    statusEnum = OrderState.StatusEnum.Shipped;
                }

                logger.info("AtelierOrderControllerUpdateOrderStatus,OrderStateChange,start,status:" + status + ",stateParams:" + JSONObject
                        .toJSONString(stateParams));
                ResultMessage resultMessage = orderState.change(statusEnum, stateParams).execute();
                logger.info(
                        "AtelierOrderControllerUpdateOrderStatus,OrderStateChange,end,status:" + status + ",stateParams:" + JSONObject.toJSONString(stateParams)
                                + ",resultMessage:" + JSONObject.toJSONString(resultMessage));

                if (resultMessage.isSUCCESS()) {
                    result = this.result(response_success, null, null);
                } else {
                    result = this.result(response_error, error_code_1, resultMessage.getMsg());
                }
            } else {
                result = this.result(response_error, error_code_1, error_desc_1 + checkResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = this.result(response_error, error_code_1, error_desc_1 + "ErrorMessage:" + e.getMessage());
            logger.info("AtelierOrderControllerUpdateOrderStatus,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AtelierOrderControllerUpdateOrderStatus,outputParams,result:" + JSONObject.toJSONString(result));
        return result;
    }

    public List<GetOrderByDateResultVO> convertByGetDate(List<Map<String, Object>> data) {
        List<GetOrderByDateResultVO> result = new ArrayList<>();
        if (data != null && data.size() > 0) {

            for (Map<String, Object> map : data) {
                GetOrderByDateResultVO getOrderByDateResultVO = new GetOrderByDateResultVO();
                String order_line_num = map.get("order_line_num") == null ? "" : map.get("order_line_num").toString();
                String product_code = map.get("product_code") == null ? "" : map.get("product_code").toString();
                String value = map.get("value") == null ? "" : map.get("value").toString();
                String in_price = map.get("in_price") == null ? "" : map.get("in_price").toString();
                String created_at = map.get("created_at") == null ? "" : map.get("created_at").toString();
                String status = map.get("status") == null ? "" : map.get("status").toString();
                String sku_code = map.get("sku_code") == null ? "#" : map.get("sku_code").toString();

                switch (status) {
                case "-1":
                    status = "pending payment";
                    break;
                case "-2":
                    status = "invalid";
                    break;
                case "-3":
                    status = "payment submit success";
                    break;
                case "-4":
                    status = "refund submit";
                    break;
                case "-5":
                    status = "payment exception";
                    break;
                case "1":
                    status = "pending";
                    break;
                case "2":
                    status = "confirmed";
                    break;
                case "3":
                    status = "shipped";
                    break;
                case "4":
                    status = "delivered";
                    break;
                case "5":
                    status = "closed";
                    break;
                case "6":
                    status = "cancelled";
                    break;
                case "7":
                    status = "ready to ship";
                    break;
                case "8":
                    status = "picking";
                    break;
                }

                getOrderByDateResultVO.setBoutique_id(product_code);
                getOrderByDateResultVO.setCreate_date(created_at);
                getOrderByDateResultVO.setOrder_number(order_line_num);
                getOrderByDateResultVO.setPurchase_price(new BigDecimal(in_price));
                getOrderByDateResultVO.setQty(1);
                getOrderByDateResultVO.setSize(value);
                getOrderByDateResultVO.setStatus(status);
                getOrderByDateResultVO.setBarcode(sku_code);
                result.add(getOrderByDateResultVO);
            }
        }
        return result;
    }

    private Map<String, Object> result(String responseCode, String errorCode, String errorMsg) {
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();

        responseMap.put("ResponseStatus", responseCode);

        if (responseCode.equals(response_error)) {
            errorMap.put("ErrorCode", errorCode);
            errorMap.put("ErrorMsg", errorMsg);
            errorMap.put("TimeStamp", DateUtils.getStrDate(new Date(), "yyyy-MM-dd HH:mm:ss"));

            responseMap.put("Error", errorMap);
        }
        return responseMap;
    }

    class ApiInputParam {
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

        private String checkGetOrderByDateParams(String version, String storeId, String offset, String limit, String date) {
            this.version = version;
            this.storeId = storeId;
            this.offset = offset;
            this.limit = limit;
            this.date = date;

            logger.info("checkGetOrderByDateParams,start,inputParams:" + JSONObject.toJSONString(this));
            if (StringUtils.isBlank(version) || !atelier_version.equals(version)) {
                return "Invalid Version";
            }

            if (StringUtils.isBlank(storeId)) {
                return "Invalid StoreID";
            }

            try {
                Integer.parseInt(offset);
            } catch (Exception e) {
                return "Invalid offset";
            }

            try {
                Integer.parseInt(limit);
            } catch (Exception e) {
                return "Invalid limit";
            }

            if (StringUtils.isBlank(offset)) {
                this.intOffset = 0;
            } else {
                this.intOffset = Integer.parseInt(offset);
            }

            if (StringUtils.isBlank(limit)) {
                this.intLimit = 20;
            } else {
                this.intLimit = Integer.parseInt(limit);
                if (this.intLimit > 50 || this.intLimit < 0) {
                    //                    this.intLimit = 50;
                    return "Invalid limit";
                }
            }

            if (!isValidDate(date)) {
                return "Invalid date";
            }

            Map<String, Object> paramMap = atelierUpdateByProductService.getParamsMap();
            if (paramMap.get(storeId) == null) {
                return "Invalid StoreID";
            }
            Map<String, Object> dataMap = (Map<String, Object>) paramMap.get(storeId);
            this.vendorId = Long.parseLong(dataMap.get("vendor_id").toString());
            logger.info("checkGetOrderByDateParams,end,inputParams:" + JSONObject.toJSONString(this));
            return atelier_success;
        }
    }

    public boolean isValidDate(String str) {
        //String str = "2007-01-02";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = (Date) formatter.parse(str);
            return str.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    class GetOrderByDateResultVO {
        private String order_number;
        private String create_date;
        private String boutique_id;
        private String size;
        private int qty;
        private BigDecimal purchase_price;
        private String status;
        private String barcode;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
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

        public String checkUpdateOrderStatusParams(String body, String storeId, String version) {
            if (StringUtils.isBlank(body)) {
                return "body is null.";
            }

            if (StringUtils.isBlank(storeId)) {
                return "Invalid StoreID";
            }

            if (StringUtils.isBlank(version) || !atelier_version.equals(version)) {
                return "Invalid Version";
            }

            Map<String, Object> paramMap = atelierUpdateByProductService.getParamsMap();
            if (paramMap.get(storeId) == null) {
                return "Invalid StoreID";
            }

            Map<String, Object> dataMap = (Map<String, Object>) paramMap.get(storeId);
            this.vendorId = Long.parseLong(dataMap.get("vendor_id").toString());

            JSONObject jsonObjectBody = JSON.parseObject(body);
            this.order_number = jsonObjectBody.getString("order_number");
            this.status = jsonObjectBody.getString("status");

            if (StringUtils.isBlank(this.order_number)) {
                return "Invalid order_number";
            }

            if (StringUtils.isBlank(this.status)) {
                return "Invalid status";
            }

            if (!(OrderState.StatusEnum.Cancelled.getCode().equals(this.status) || OrderState.StatusEnum.Confirmed.getCode().equals(this.status)
                    || OrderState.StatusEnum.Shipped.getCode().equals(this.status))) {
                return "Invalid status";
            }

            this.tracking_number = jsonObjectBody.getString("tracking_number");
            this.invoice_number = jsonObjectBody.getString("invoice_number");

            return atelier_success;
        }
    }
}
