package com.intramirror.web.controller.order;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.main.api.enums.GeographyEnum;
import com.intramirror.main.api.model.StockLocation;
import com.intramirror.main.api.model.Tax;
import com.intramirror.main.api.service.StockLocationService;
import com.intramirror.main.api.service.TaxService;
import com.intramirror.main.api.vo.StockLocationVO;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.*;
import com.intramirror.order.api.model.CancelOrderVO;
import com.intramirror.order.api.vo.PageListVO;
import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.model.Product;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.common.CommonsProperties;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.common.cache.CategoryCache;
import com.intramirror.web.service.LogisticsProductService;
import com.intramirror.web.service.OrderRefund;
import com.intramirror.web.service.OrderService;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.common.utils.StringUtil;

@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;

    @Autowired
    private LogisticsProductService logisticsProductService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private OrderRefund orderRefund;

    @Autowired
    private OrderService orderServiceImpl;

    @Autowired
    private ISkuStoreService skuStoreService;

    @Autowired
    private ILogisticsProductService iLogisticsProductService;

    @Autowired
    private IContainerService containerService;

    @Autowired
    private IOrderExceptionService orderExceptionService;

    @Autowired
    private IOrderExceptionTypeService orderExceptionTypeService;

    @Autowired
    private IProductService productService;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private CommonsProperties commonsProperties;

    @Autowired
    private StockLocationService stockLocationService;

    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private TaxService taxService;

    /**
     * 获取订单列表
     * @param map
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/getOrderList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getOrderList(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info("order getOrderList parameters: {}", new Gson().toJson(map));
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map.get("orderNumber")!=null){
            map.put("orderNumber",map.get("orderNumber").toString().trim());
        }

        if (map == null || map.size() == 0 || map.get("status") == null || StringUtils.isBlank(map.get("status").toString())) {
            result.setMsg("Parameter cannot be empty");
            return result;
        }

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        String sortByName = null;
        if (map.get("sortByName") != null) {
            sortByName = map.get("sortByName").toString();
        } else {
            sortByName = "created_at";
        }
        List<Long> categoryIds = new ArrayList<>();
        if (map.get("categoryId") != null) {
            categoryIds = categoryCache.getAllChildCategory(Long.parseLong(map.get("categoryId").toString()));
        }
        List<Long> vendorIds = null;
        if(map.get("vendorId") != null){
            vendorIds = Arrays.asList(Long.parseLong(map.get("vendorId").toString()));
        }else {
            List<Vendor> vendors = null;
            try {
                vendors = vendorService.getVendorsByUserId(user.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (vendors == null) {
                result.setMsg("Please log in again");
                return result;
            }
            vendorIds = vendors.stream().map(Vendor::getVendorId).collect(Collectors.toList());
        }

        String status = map.get("status").toString();
        //根据订单状态查询订单
        logger.info(MessageFormat.format("order getOrderList 调用接口 orderService.getOrderListByStatus 查询订单信息  入参 status:{0},vendorIds:{1},sortByName:{2}",
                status, vendorIds, sortByName));
        List<Map<String, Object>> orderList = null;
        PageListVO orderCancelList = null;
        Map<Long,List<StockLocationVO>> stockLocationListMap = null;
        if ("6".equals(status)){
            //cancel TAB列表查询
            map.put("vendorIds",vendorIds);
            orderCancelList = orderService.getOrderCancelList(map);
        }else {
            Map<String, Object> params = new HashMap<>();
            params.put("status", status);
            params.put("vendorIds", vendorIds);
            params.put("sortByName", sortByName);
            params.put("categoryIds", categoryIds);
            params.put("brandId", map.get("brandId"));
            params.put("locationId", map.get("locationId"));
            params.put("logisticsProductIds", map.get("logisticsProductIds"));
            orderList = orderService.getOrderListByParams(params);
            //根据vendorIds查询所有的stockLocation
            List<StockLocationVO> stockLocationList = stockLocationService.getStockLocationByVendorIds(vendorIds);
            stockLocationListMap = stockLocationList.stream().collect(Collectors.groupingBy(StockLocationVO::getVendorId));
        }
        Map<Long,Category> categoryMap =iCategoryService.queryAllCategoryName();
        if (orderList != null && orderList.size() > 0) {
            logger.info("order getOrderList 解析订单列表信息  ");
            for (Map<String, Object> info : orderList) {
                //计算折扣
                arithmeticalDiscount(info);
                if(stockLocationListMap!=null){
                    info.put("stockLocations",stockLocationListMap.get(info.get("vendor_id")));
                }
                info.put("categoryPath",categoryMap.get(info.get("category_id"))==null?"":categoryMap.get(info.get("category_id")).getCategoryPath());
            }
        }
        if(orderCancelList!=null
                &&orderCancelList.getTotal()!=null
                &&orderCancelList.getTotal()>0){
            for(Object o:orderCancelList.getData()){
                CancelOrderVO co = (CancelOrderVO)o;
                Double price = Double.parseDouble(co.getPrice().toString());
                Double inPrice = Double.parseDouble(co.getIn_price().toString());
                Tax tax = taxService.calculateDiscountTax(co.getOrder_line_num());
                double discountTax;
                if (tax != null){
                    discountTax = tax.getTaxRate().doubleValue();
                }else {
                    discountTax = 0;
                }

                BigDecimal supply_price_discount = new BigDecimal((inPrice * (1 + discountTax) / price) * 100);
                if (supply_price_discount.intValue() > 100 || supply_price_discount.intValue() < 0) {
                    co.setSupply_price_discount(0 + " %");
                } else {
                    co.setSupply_price_discount ((100 - supply_price_discount.setScale(0, BigDecimal.ROUND_HALF_UP).intValue()) + " %");
                }
                co.setPrice(new BigDecimal(co.getPrice().toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                co.setIn_price(new BigDecimal(co.getIn_price().toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString());

                co.setCategoryPath(categoryMap.get(co.getCategory_id())==null?"":categoryMap.get(co.getCategory_id()).getCategoryPath());

                //计算vat
                BigDecimal bigDecimal = taxService.calculateTax(co.getOrder_line_num());
                double taxRate = bigDecimal.doubleValue();
                if (taxRate > 0){
                    co.setTax_fee(new BigDecimal(inPrice * taxRate).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        }

        result.successStatus();
        if("6".equals(status)){
            result.setData(orderCancelList);
        }else {
            result.setData(orderList);
        }
        return result;
    }

    private void arithmeticalDiscount(Map<String, Object> info) {
        //获取税率
        String orderLineNum = info.get("order_line_num").toString();
        Tax tax = taxService.calculateDiscountTax(orderLineNum);
        double discountTax;
        if (tax != null){
            discountTax = tax.getTaxRate().doubleValue();
        }else {
            discountTax = 0;
        }
        String priceStr = info.get("price").toString();
        String inPriceStr = info.get("in_price").toString();
        Double price = Double.parseDouble(priceStr);
        Double inPrice = Double.parseDouble(inPriceStr);

        BigDecimal supply_price_discount = new BigDecimal((inPrice * (1 + discountTax) / price) * 100);
        if (supply_price_discount.intValue() > 100 || supply_price_discount.intValue() < 0) {
            info.put("supply_price_discount", 0 + " %");
        } else {
            info.put("supply_price_discount", (100 - supply_price_discount.setScale(0, BigDecimal.ROUND_HALF_UP).intValue()) + " %");
        }
        //四舍五入金额
        info.put("price",new BigDecimal(priceStr).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
        info.put("in_price",new BigDecimal(inPriceStr).setScale(2,BigDecimal.ROUND_HALF_UP).toString());

        BigDecimal calculateTax = taxService.calculateTax(orderLineNum);
        if (calculateTax.doubleValue() > 0){
            info.put("vat",new BigDecimal(inPrice * calculateTax.doubleValue()).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
        }
    }

    /**
     * 获取订单详情
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/getOrderDetail", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getOrderDetail(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info(MessageFormat.format("order getOrderDetail 入参:{0}", new Gson().toJson(map)));
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map == null || map.size() == 0 || map.get("status") == null || StringUtils.isBlank(map.get("status").toString()) || map.get("orderNumber")
                == null) {
            result.setMsg("Parameter cannot be empty");
            return result;
        }

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        Vendor vendor = null;
        try {
            vendor = vendorService.getVendorByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendor == null) {
            result.setMsg("Please log in again");
            return result;
        }

        //Long vendorId = vendor.getVendorId();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("status", Integer.parseInt(map.get("status").toString()));
        //conditionMap.put("vendorId", vendorId);
        conditionMap.put("orderNumber", map.get("orderNumber").toString());

        //根据订单orderLineNumber 查询订单详情
        logger.info(MessageFormat.format("order getOrderDetail 调用 orderService.getOrderInfoByCondition接口获取详情 入参:{0}", new Gson().toJson(conditionMap)));
        Map<String, Object> orderInfo = orderService.getOrderInfoByCondition(conditionMap);
        if (orderInfo == null || orderInfo.size() == 0) {
            result.setMsg("Order does not exist");
            return result;
        }

        //处理打印标记
        String printFlag = null;
        if (orderInfo.get("countryId") != null &&
                ("3".equals(orderInfo.get("countryId").toString()) || "4".equals(orderInfo.get("countryId").toString()))){
            printFlag = "*LOCAL*";
        }
        String shippingMethod = orderInfo.get("shippingMethod") == null ? "" : orderInfo.get("shippingMethod").toString();
        if ("1".equals(shippingMethod)) {
            printFlag = "*【Fedex】* Transit Warehouse";
        }else if ("2".equals(shippingMethod)) {
            printFlag = "*COMO*";
        }

        String expressType = orderInfo.get("express_type") == null ? "" : orderInfo.get("express_type").toString();
        String geographyGroupId = orderInfo.get("geographyGroupId") == null ? "" : orderInfo.get("geographyGroupId").toString();
        if ("1".equals(geographyGroupId) && "0".equals(shippingMethod)){
            //发往中国大陆
            String sortingType = orderInfo.get("sortingType") == null ? "" : orderInfo.get("sortingType").toString();
            if ("1".equals(expressType)){
                if ("1".equals(sortingType)){
                    printFlag = "*COE(CC Zhang)*";
                }else if ("2".equals(sortingType)){
                    printFlag = "*ZSY(CC Zhang)*";
                }
            }else{
                if ("1".equals(sortingType)){
                    printFlag = "*COE*";
                }else if ("2".equals(sortingType)){
                    printFlag = "*ZSY*";
                }
            }
        }
        orderInfo.put("printFlag",printFlag);

        if (orderInfo != null) {
            logger.info("order getOrderDetail 解析订单详情,计算价格折扣");
            //汇率
            Double rate = Double.parseDouble(orderInfo.get("current_rate").toString());

            //按汇率计算人民币价钱
            Double sale_price2 = Double.parseDouble(orderInfo.get("sale_price").toString()) * rate;
            orderInfo.put("sale_price2", sale_price2);
            //计算利润
            Double profit = Double.parseDouble(orderInfo.get("sale_price").toString()) - Double.parseDouble(orderInfo.get("in_price").toString());
            BigDecimal b = new BigDecimal(profit);
            profit = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            orderInfo.put("profit", profit * rate);

            //计算折扣
            Double salePrice = Double.parseDouble(orderInfo.get("sale_price").toString());
            Double price = Double.parseDouble(orderInfo.get("price").toString());
            BigDecimal inPriceDecimal = new BigDecimal(Double.parseDouble(orderInfo.get("in_price").toString()));
            Double inPrice = inPriceDecimal.doubleValue();
            orderInfo.put("in_price",inPriceDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());


            BigDecimal sale_price_discount = new BigDecimal((salePrice / price) * 100);
            //				info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
            orderInfo.put("sale_price_discount", (100 - sale_price_discount.setScale(0, BigDecimal.ROUND_HALF_UP).intValue()) + " %");


            //获取税率
            String orderLineNum = orderInfo.get("order_line_num").toString();
            Tax tax = taxService.calculateDiscountTax(orderLineNum);
            double discountTax;
            if (tax != null){
                discountTax = tax.getTaxRate().doubleValue();
            }else {
                discountTax = 0;
            }
            BigDecimal supply_price_discount = new BigDecimal((inPrice * (1 + discountTax) / price) * 100);
            //			orderInfo.put("supply_price_discount", (100 -supply_price_discount.setScale(0,BigDecimal.ROUND_HALF_UP).intValue())+" %");
            if (supply_price_discount.intValue() > 100 || supply_price_discount.intValue() < 0) {
                orderInfo.put("supply_price_discount", 0 + " %");
            } else {
                orderInfo.put("supply_price_discount", (100 - supply_price_discount.setScale(0, BigDecimal.ROUND_HALF_UP).intValue()) + " %");
            }

            /*
         * 2017-9-30 add by Jain
         * The boutique id in the print page should not be the vendor_id but the product_code
        * */

            Long productId = Long.parseLong(orderInfo.get("product_id").toString());
            Product product = productService.selectByPrimaryKey(productId);
            String sProductCode = product.getProductCode();
            orderInfo.put("ProductCode", sProductCode);
        }

        //		logger.info("order getOrderDetail 生成条形码  返回URL");
        //		String orderNumberUrl = "Barcode-"+orderInfo.get("order_line_num").toString()+".png";
        //		orderNumberUrl = BarcodeUtil.generateFile(orderInfo.get("order_line_num").toString(), orderNumberUrl,false);
        //		orderInfo.put("orderNumberUrl", orderNumberUrl);
        //
        //		//如果包含#号  则不生成条形码
        //		String skuBarcodeUrl = "Barcode-"+orderInfo.get("sku_code").toString()+".png";
        //		if(!skuBarcodeUrl.contains("#")){
        //			skuBarcodeUrl = BarcodeUtil.generateFile(orderInfo.get("sku_code").toString(), skuBarcodeUrl,true);
        //			orderInfo.put("skuBarcodeUrl", skuBarcodeUrl);
        //		}else{
        //			orderInfo.put("skuBarcodeUrl", null);
        //		}


        result.successStatus();
        result.setData(orderInfo);
        return result;
    }

    /**
     * 状态机接口
     * @param map
     * @return
     */
    @RequestMapping(value = "/updateOrderStatus", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage updateOrderStatus(@RequestBody Map<String, Object> map, HttpServletRequest httpServletRequest) {
        logger.info(MessageFormat.format("updateOrderStatus param:{0}", new Gson().toJson(map)));

        User user = getUser(httpServletRequest);
        ResultMessage message = ResultMessage.getInstance();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", StatusType.FAILURE);
        try {
            //参数不能为空
            if (map == null || map.size() == 0) {
                message.successStatus().putMsg("info", "Parameter cannot be null");
                return message;
            }

            if (map.get("logisProductId") == null || StringUtils.isBlank(map.get("logisProductId").toString())) {
                message.successStatus().putMsg("info", "logisProductId cannot be null");
                return message;
            }

            if (map.get("status") == null || StringUtils.isBlank(map.get("status").toString())) {
                message.successStatus().putMsg("info", "status cannot be null");
                return message;
            }

            Long logisProductId = Long.parseLong(map.get("logisProductId").toString());
            int status = Integer.parseInt(map.get("status").toString());

            //调用修改订单状态
            //根据id获取当前数据库旧的对象信息
            LogisticsProduct oldLogisticsProduct = logisticsProductService.selectById(logisProductId);
            resultMap = logisticsProductService.updateOrderLogisticsStatusById(oldLogisticsProduct, status);
            if (StatusType.SUCCESS == Integer.parseInt(resultMap.get("status").toString())) {
                //更新confirm库存
                if (2 == status) {
                    if (null != oldLogisticsProduct) {
                        Long shopProductSkuId = oldLogisticsProduct.getShop_product_sku_id();
                        logger.info("订单 " + shopProductSkuId + " , 用户 " + JSON.toJSONString(user.getUsername()) + " confirm操作, 修改confirm库存开始");
                        skuStoreService.updateConfirmStoreByShopProductSkuId(shopProductSkuId);
                        logger.info("订单 " + shopProductSkuId + " confirm操作, 修改confirm库存成功");
                    }
                }
                message.successStatus().putMsg("Info", "SUCCESS").setData(resultMap);
                return message;
            }
            message.successStatus().putMsg("Info", "" + StatusType.FAILURE).setData(resultMap);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("errorMsg : " + e.getMessage());
            message.errorStatus().putMsg("errorMsg", e.getMessage());
        }
        return message;
    }

    /**
     * 根据不同订单状态统计数量
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/getOrderCount", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage getOrderCount(HttpServletRequest httpRequest) {
        ResultMessage resultMessage = ResultMessage.getInstance();
        Map<String, Object> map;
        try {
            User user = this.getUser(httpRequest);
            if (user == null) {
                resultMessage.setMsg("Please log in again");
                return resultMessage;
            }

            List<Vendor> vendors = null;
            try {
                vendors = vendorService.getVendorsByUserId(user.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (CollectionUtils.isEmpty(vendors)) {
                resultMessage.setMsg("Please log in again");
                return resultMessage;
            }
            List<Long> vendorIds = vendors.stream().map(Vendor::getVendorId).collect(Collectors.toList());
            int[] item = { OrderStatusType.PENDING, OrderStatusType.COMFIRMED ,OrderStatusType.CANCELED,OrderStatusType.PICKING};
            Map<String, Object> resultMap = new HashMap<>();
            for (int i = 0; i < item.length; i++) {
                map = new HashMap<>();
                map.put("status", item[i]);
                map.put("vendorIds", vendorIds);
                int result = orderService.getOrderByIsvalidCount(map);
                if (OrderStatusType.PENDING == item[i])
                    resultMap.put("comfirmed", result);
                if (OrderStatusType.COMFIRMED == item[i])
                    resultMap.put("pack", result);
                if (OrderStatusType.CANCELED == item[i]){
                    //cancel
                    int orderCancelCount = orderService.getOrderCancelCount(map);
                    resultMap.put("canceled",orderCancelCount);
                }
                if(OrderStatusType.PICKING == item[i]){
                    resultMap.put("picking", result);
                }
                /*if (OrderStatusType.READYTOSHIP == item[i]){
                    resultMap.put("readyToship",result);
                }*/
            }

            //readytoship数量
            map = new HashMap<>();
            map.put("vendorIds", vendorIds);
            Integer result = containerService.getContainerCount(map);
            resultMap.put("readyToship", result==null?0:result);

            //shippedCount
            Integer shippedCount = orderService.getShippedCount(map);
            resultMap.put("shipped", shippedCount==null?0:shippedCount);

            resultMessage.successStatus().putMsg("info", "SUCCESS").setData(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(MessageFormat.format(" error message : {}", e.getMessage()));
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;

    }

    /**
     * 退款接口
     * @param map
     * @return
     */
    @RequestMapping(value = "/orderRefund", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage orderRefund(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        //打印退款参数
        logger.info("info parameter" + new Gson().toJson(map));
        ResultMessage message = ResultMessage.getInstance();
        User user = this.getUser(request);
        if (null == user) {
            message.successStatus().putMsg("status", "" + StatusType.SESSION_USER_NULL);
            message.successStatus().putMsg("errorMsg", "session失效,请重新登陆!!!");
            return message;
        }
        //入参处理
        Map<String, String> params;
        try {
            params = getParamsFromRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            message.errorStatus().putMsg("errorMsg", e.getMessage());
            message.errorStatus().putMsg("status", "" + StatusType.STRING_CONVERT_UNSUPPORTED_ENCODING_EXCEPTION);
            return message;
        }
        //判断status
        int checkResult = checkParams(params);
        if (StatusType.SUCCESS != checkResult) {
            message.errorStatus().putMsg("errorMsg", "参数校验失败！！！");
            message.errorStatus().putMsg("status", checkResult + "");
            return message;
        }
        //获取订单状态
        Integer orderStatus = Integer.valueOf(params.get("status"));
        //该接口只针对退款，如果不是走状态机接口
        if (-4 != orderStatus) {
            message.errorStatus().putMsg("errorMsg", "status not in -4");
            message.errorStatus().putMsg("status", checkResult + "");
            return message;
        }
        //单个或多个logistics_product_id编辑改变状态
        JsonArray logisticsProductArray = null;
        //传过来order_num和vendor_id进行编辑改变状态
        JsonArray orderVendorArray = null;
        // 响应状态(默认为失败)
        int status = StatusType.FAILURE;
        try {
            //退款处理
            if (null != params.get("logistics_product_list") || StringUtils.isNoneBlank(params.get("logistics_product_list").toString())) {
                logisticsProductArray = new JsonParser().parse(params.get("logistics_product_list").toString()).getAsJsonArray();
                status = orderRefund.updateStausByJson(logisticsProductArray, null, orderStatus);

            }
            if (null != params.get("order_vendor_list") || StringUtils.isNoneBlank(params.get("order_vendor_list").toString())) {
                orderVendorArray = new JsonParser().parse(params.get("order_vendor_list").toString()).getAsJsonArray();
                status = orderRefund.updateStausByJson(null, orderVendorArray, orderStatus);
            }
            status = StatusType.SUCCESS;
            message.successStatus().putMsg("status", "" + status).setData(status);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("error" + e.getMessage());
            message.errorStatus().putMsg("errorMsg", e.getMessage());
        }
        return message;
    }

    /***
     * 获取箱子里面的订单信息
     * @param map
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/getPackOrderList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getPackOrderList(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info(MessageFormat.format("order getPackOrderList 入参:{0}", new Gson().toJson(map)));
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map == null || map.size() == 0 || map.get("status") == null || map.get("containerId") == null) {
            result.setMsg("Parameter cannot be empty");
            return result;
        }

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        List<Vendor> vendors = null;
        try {
            vendors = vendorService.getVendorsByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(vendors)) {
            result.setMsg("Please log in again");
            return result;
        }

        List<Long> vendorIds = vendors.stream().map(Vendor::getVendorId).collect(Collectors.toList());

        try {
            //获取PackOrderorder列表
            logger.info(MessageFormat
                    .format("order getPackOrderList 调用接口getOrderListByStatusAndContainerId 获取箱子内订单列表 入参 containerId:{0},status:{1},vendorId:{2}",
                            map.get("containerId").toString(), map.get("status").toString(), vendorIds.toArray()));
            List<Map<String, Object>> packList = orderService.getOrderListByStatusAndContainerId(Integer.parseInt(map.get("containerId").toString()),
                    Integer.parseInt(map.get("status").toString()), vendorIds);

            //			if(packList != null && packList.size() > 0){
            //				//遍历获取所有商品ID
            //				String productIds = "";
            //				for(Map<String, Object> info : packList){
            //					productIds +=info.get("product_id").toString()+",";
            //				}
            //
            //				if(StringUtils.isNoneBlank(productIds)){
            //					productIds = productIds.substring(0,productIds.length() -1);
            //				}
            //
            //				//根据ID列表获取商品属性
            //				List<Map<String, Object>> productPropertyList = productPropertyService.getProductPropertyListByProductId(productIds);
            //				Map<String, Map<String, String>> productPropertyResult= new HashMap<String, Map<String, String>>();
            //
            //				for(Map<String, Object> productProperty : productPropertyList){
            //
            //					//如果存在
            //					if(productPropertyResult.containsKey(productProperty.get("product_id").toString())){
            //						Map<String, String> info = productPropertyResult.get(productProperty.get("product_id").toString());
            //					    info.put(productProperty.get("key_name").toString(), productProperty.get("value").toString());
            //					}else{
            //						Map<String, String> info = new HashMap<String, String>();
            //						info.put(productProperty.get("key_name").toString(), productProperty.get("value").toString());
            //						productPropertyResult.put(productProperty.get("product_id").toString(), info);
            //					}
            //
            //				}
            //			}

            result.successStatus();
            result.setData(packList);

        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("Query order list fail,Check parameters, please ");
            return result;
        }

        return result;
    }

    /**
     * 装箱验证
     * @param map
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/packingCheckOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage packingCheckOrder(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info(MessageFormat.format("order packingCheckOrder 入参:{0}", new Gson().toJson(map)));
        ResultMessage result = new ResultMessage();
        result.errorStatus();
        Map<String, Object> infoMap = new HashMap<String, Object>();
        //0 校验   2返回shipment列表  3箱子为空时  4不为空时
        infoMap.put("statusType", StatusType.ORDER_CHECK_ORDER);

        if (checkParamsBypackingCheckOrder(map)) {
            result.setMsg("Parameter cannot be empty");
            infoMap.put("code", StatusType.ORDER_ERROR_CODE);
            result.setInfoMap(infoMap);
            return result;
        }

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            infoMap.put("code", StatusType.ORDER_ERROR_CODE);
            result.setInfoMap(infoMap);
            return result;
        }

        Vendor vendor = null;
        try {
            vendor = vendorService.getVendorByUserId(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendor == null) {
            result.setMsg("Please log in again");
            infoMap.put("code", StatusType.ORDER_ERROR_CODE);
            result.setInfoMap(infoMap);
            return result;
        }

        //map.put("vendorId", vendor.getVendorId());

        LogisticsProduct logisticsProduct=iLogisticsProductService.selectByOrderLineNum(map.get("orderLineNum").toString());
        if(logisticsProduct!=null&&logisticsProduct.getShippingMethod()==1){
            Map<String,Object> containerParam = Maps.newHashMap();
            containerParam.put("containerId",map.get("containerId"));
            Map<String,Object> container = containerService.getContainerById(containerParam);
            if(container!=null){
                String shipToGeography = (String) container.get("ship_to_geography");
                if(!"Transit Warehouse".equals(shipToGeography)){
                    result.setMsg("This order must to be deliveried to transit warehouse,cause buyer choose the shipping method of Fedex.Please delete this carton and create a new carton to transit warehouse.");
                    infoMap.put("code", StatusType.ORDER_CHECK_ORDER);
                    result.setInfoMap(infoMap);
                    return result;
                }
            }
        }
        //订单装箱
        try {
            result = orderServiceImpl.packingOrder(map);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
            infoMap.put("code", StatusType.ORDER_ERROR_CODE);
            result.setInfoMap(infoMap);
            return result;
        }

        return result;
    }

    /**
     * 删除箱子中的订单
     * @param map
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/deletePackingCheckOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage deletePackingCheckOrder(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info(MessageFormat.format("order deletePackingCheckOrder 入参:{0}", new Gson().toJson(map)));
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map == null || map.size() == 0 || map.get("logistics_product_id") == null || map.get("container_id") == null) {
            result.setMsg("Parameter cannot be empty");
            return result;
        }

        try {
            result = orderServiceImpl.deleteOrder(map);
        } catch (Exception e) {
            result.setMsg("Delete failed, please check whether the parameter is correct");
            logger.error("order deletePackingCheckOrder failed,调用接口orderServiceImpl.deleteOrder 入参:" + new Gson().toJson(map));
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取请求参数
     * @param req
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> getParamsFromRequest(HttpServletRequest req) throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<>();

        // 获取status(需执行的操作)
        String status = req.getParameter("status");
        if (StringUtils.isNotBlank(status)) {
            status = URLDecoder.decode(status, "UTF-8");
            if (status.equalsIgnoreCase("CONFIRM")) {
                status = "2";
            } else if (status.equalsIgnoreCase("ORDERED")) {//ship
                status = "3";
            } else if (status.equalsIgnoreCase("DELIVERED")) {//delivered
                status = "4";
            } else if (status.equalsIgnoreCase("CLOSE")) {
                status = "5";
            } else if (status.equalsIgnoreCase("CANCELED")) {
                status = "-4";
            }
            result.put("status", status);
        }

        String tracking_num = req.getParameter("tracking_num");
        if (StringUtils.isNotBlank(tracking_num)) {
            result.put("tracking_num", tracking_num);
        } else {
            result.put("tracking_num", "");
        }

        String vat_num = req.getParameter("vat_num");
        if (StringUtils.isNotBlank(vat_num)) {
            result.put("vat_num", vat_num);
        } else {
            result.put("vat_num", "");
        }

        String logistics_product_list = req.getParameter("logistics_product_list");
        if (logistics_product_list != null) {
            logistics_product_list = URLDecoder.decode(logistics_product_list, "UTF-8");
            result.put("logistics_product_list", logistics_product_list);
        }

        String order_vendor_list = req.getParameter("order_vendor_list");
        if (order_vendor_list != null) {
            order_vendor_list = URLDecoder.decode(order_vendor_list, "UTF-8");
            result.put("order_vendor_list", order_vendor_list);
        }

        return result;
    }

    /**
     * 判断是否是合法
     * @param params
     * @return
     */
    public int checkParams(Map<String, String> params) {
        // 判断status是否合法
        if (Helper.isNullOrEmpty(params.get("status").toString())) {
            return StatusType.PARAM_EMPTY_OR_NULL;
        }

        if (params.get("status").toString().length() > 256) {
            return StatusType.STRING_LENGTH_ERROR;
        }

        // 判断logistics_product_list是否为空
        if (Helper.isNullOrEmpty(params.get("logistics_product_list").toString())) {
            return StatusType.PARAM_EMPTY_OR_NULL;
        }
        // 判断logistics_product_list是否json
        if (!Helper.isGoodJson(params.get("logistics_product_list").toString())) {
            return StatusType.IS_NOT_GOOD_JSON;
        }

        // 判断order_vendor_list是否为空
        if (Helper.isNullOrEmpty(params.get("order_vendor_list").toString())) {
            return StatusType.PARAM_EMPTY_OR_NULL;
        }
        // 判断order_vendor_list是否json
        if (!Helper.isGoodJson(params.get("order_vendor_list").toString())) {
            return StatusType.IS_NOT_GOOD_JSON;
        }
        return StatusType.SUCCESS;
    }

    /**
     * packingCheckOrder 接口的参数校验
     * @return
     */
    public boolean checkParamsBypackingCheckOrder(Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return true;
        }

        if (map.get("containerId") == null || StringUtils.isBlank(map.get("containerId").toString())) {
            return true;
        }

        if (map.get("status") == null || StringUtils.isBlank(map.get("status").toString())) {
            return true;
        }

        if (map.get("orderLineNum") == null || StringUtils.isBlank(map.get("orderLineNum").toString())) {
            return true;
        }

        return false;
    }

    //    /**
    //     * 回调接口
    //     *
    //     * @return
    //     */
    //<<<<<<< HEAD
    //    @RequestMapping(value = "/orderRefundCallback")
    //    @ResponseBody
    //    public ResultMessage orderRefundCallback(@RequestBody Map<String, Object> map) {
    //        logger.info("info parameter :" + new Gson().toJson(map));
    //        ResultMessage message = ResultMessage.getInstance();
    //        try {
    //            //校验入参
    //            if (null == map || 0 == map.size()) {
    //                message.successStatus().putMsg("info", "Parameter cannot be null");
    //                logger.info("info parameter cannot be null");
    //                return message;
    //            }
    //            if (null == map.get("logisProductId") || StringUtils.isBlank(map.get("logisProductId").toString())) {
    //                message.successStatus().putMsg("info", "logisProductId cannot be null");
    //                logger.info("info logisProductId  cannot be null");
    //                return message;
    //            }
    //            if (null == map.get("status") || StringUtils.isBlank(map.get("status").toString())) {
    //                message.successStatus().putMsg("info", "status cannot be null");
    //                logger.info("info status  cannot be null");
    //                return message;
    //            }
    //            Long logisProductId = Long.parseLong(map.get("logisProductId").toString());
    //            int status = Integer.parseInt(map.get("status").toString());
    //
    //            //回调接口只支持取消
    //            if (OrderStatusType.CANCELED != status) {
    //                message.successStatus().putMsg("info", "status not is 6");
    //                logger.info("info status not is 6");
    //                return message;
    //            }
    //            logger.info(logisProductId + "： 进入退款回调流程!!");
    //            Map<String, Object> resultMap = logisticsProductService.updateOrderLogisticsStatusById(logisProductId, status);
    //            //获取SKU
    //            LogisticsProduct oldLogisticsProduct = iLogisticsProductService.selectById(logisProductId);
    //            //开始修改库存
    //            if (StatusType.SUCCESS == Integer.parseInt(resultMap.get("status").toString())) {
    //                //如果成功，释放库存
    //                Long skuId = skuStoreService.selectSkuIdByShopProductSkuId(oldLogisticsProduct.getShop_product_sku_id());
    //                int result = skuStoreService.updateBySkuId(OrderStatusType.REFUND, skuId);
    //                message.successStatus().putMsg("Info", "SUCCESS").setData(result);
    //                logger.info(logisProductId + "===========>>>>>释放库存");
    //                return message;
    //            }
    //            message.successStatus().putMsg("Info", "当前状态不符合状态机扭转请检查");
    //            logger.info("退款回调结束!!");
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            logger.info("errorMsg : " + e.getMessage());
    //            message.errorStatus().putMsg("errorMsg", e.getMessage());
    //        }
    //        return message;
    //=======
    @RequestMapping(value = "/orderRefundCallback")
    @ResponseBody
    public ResultMessage orderRefundCallback(@RequestBody Map<String, Object> map) {
        logger.info("info parameter :" + new Gson().toJson(map));
        ResultMessage message = ResultMessage.getInstance();
        try {
            //校验入参
            if (null == map || 0 == map.size()) {
                message.successStatus().putMsg("info", "Parameter cannot be null");
                logger.info("info parameter cannot be null");
                return message;
            }
            if (null == map.get("logisProductId") || StringUtils.isBlank(map.get("logisProductId").toString())) {
                message.successStatus().putMsg("info", "logisProductId cannot be null");
                logger.info("info logisProductId  cannot be null");
                return message;
            }
            if (null == map.get("status") || StringUtils.isBlank(map.get("status").toString())) {
                message.successStatus().putMsg("info", "status cannot be null");
                logger.info("info status  cannot be null");
                return message;
            }
            Long logisProductId = Long.parseLong(map.get("logisProductId").toString());
            int status = Integer.parseInt(map.get("status").toString());

            //回调接口只支持取消
            if (OrderStatusType.CANCELED != status) {
                message.successStatus().putMsg("info", "status not is 6");
                logger.info("info status not is 6");
                return message;
            }
            logger.info(logisProductId + "： 进入退款回调流程!!");
            LogisticsProduct logisticsProduct = new LogisticsProduct();
            logisticsProduct.setLogistics_product_id(logisProductId);
            Map<String, Object> resultMap = logisticsProductService.updateOrderLogisticsStatusById(logisticsProduct, status);
            //获取SKU
            LogisticsProduct oldLogisticsProduct = iLogisticsProductService.selectById(logisProductId);
            //开始修改库存
            if (StatusType.SUCCESS == Integer.parseInt(resultMap.get("status").toString())) {
                //如果成功，释放库存
                Long skuId = skuStoreService.selectSkuIdByShopProductSkuId(oldLogisticsProduct.getShop_product_sku_id());
                int result = skuStoreService.updateBySkuId(OrderStatusType.REFUND, skuId);
                message.successStatus().putMsg("Info", "SUCCESS").setData(result);
                logger.info(logisProductId + "===========>>>>>释放库存");
                return message;
            }
            message.successStatus().putMsg("Info", "当前状态不符合状态机扭转请检查");
            logger.info("退款回调结束!!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("errorMsg : " + e.getMessage());
            message.errorStatus().putMsg("errorMsg", e.getMessage());
        }
        return message;
    }

    /*
   public ResultMessage updateLogisticsProduct(Map<String, Object> orderMap, Map<String, Object> shipMentMap, boolean ischeck, boolean isSaveSubShipment) {
        logger.info(MessageFormat
                .format("order updateLogisticsProduct 订单装箱 入参信息   orderMap:{0},shipMentMap:{1},ischeck:{2},isSaveSubShipment:{3}", new Gson().toJson(orderMap),
                        new Gson().toJson(shipMentMap), ischeck, isSaveSubShipment));
        ResultMessage result = new ResultMessage();
        Map<String, Object> info = new HashMap<String, Object>();
        if (ischeck) {
            info.put("statusType", StatusType.ORDER_CONTAINER_NOT_EMPTY);
        } else {
            info.put("statusType", StatusType.ORDER_CONTAINER_EMPTY);
        }
        result.errorStatus();

        //校验该订单跟箱子所属的Shipment的目的地是否一致,一致则加入,是否分段运输，发货员自行判断
        logger.info("order updateLogisticsProduct 装箱校验  1.大区是否一致 2.是否为空箱子 3.shipment_type");

        //如果大区不一致，直接返回
        if (!orderMap.get("geography_name").toString().equals(shipMentMap.get("ship_to_geography").toString())) {
            result.setMsg("This Order's consignee address is different than carton's. ");
            info.put("code", StatusType.ORDER_ERROR_CODE);
            result.setInfoMap(info);
            return result;
        }

        //如果大区一致,且不为空箱子,则比较shipment_type(空箱子ischeck 都为false)
        if (orderMap.get("geography_name").toString().equals(shipMentMap.get("ship_to_geography").toString())) {

            //空箱子不需要判断,直接存入   shipment_type 用于判断该箱子是否能存放多个，状态为1 只能存放一个  所以不能在存入
            if (ischeck && shipMentMap.get("shipment_type_id").toString().equals("1")) {
                result.setMsg("Only one Order can be packed in this carton. ");
                info.put("code", StatusType.ORDER_ERROR_CODE);
                result.setInfoMap(info);
                return result;
            }

            //			//比较具体地址 省市区
            //			}else if(shipMentMap.get("shipment_type_id").toString().equals("2")){
            //
            //
            //			}else if(shipMentMap.get("shipment_type_id").toString().equals("3")){
            //
            //			}

        }

        //添加订单跟箱子的关联,并修改状态为READYTOSHIP
        logger.info("order updateLogisticsProduct 添加订单与箱子的关联  ");
        LogisticsProduct logisticsProduct = new LogisticsProduct();
        logisticsProduct.setLogistics_product_id(Long.parseLong(orderMap.get("logistics_product_id").toString()));
        logisticsProduct.setContainer_id(Long.parseLong(orderMap.get("containerId").toString()));
        logisticsProduct.setStatus(OrderStatusType.READYTOSHIP);
        logisticsProduct.setPacked_at(Helper.getCurrentUTCTime());
        logger.info("order updateLogisticsProduct 添加订单与箱子的关联 并修改状态  调用接口iLogisticsProductService.updateByLogisticsProduct 订单修改前状态:" + orderMap.get("status")
                .toString() + "  入参:" + new Gson().toJson(logisticsProduct));
        int row = iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);

        if (row > 0) {
            result.successStatus();

            Map<String, Object> saveShipmentParam = new HashMap<String, Object>();
            saveShipmentParam.put("orderNumber", orderMap.get("order_line_num").toString());
            saveShipmentParam.put("shipmentId", Long.parseLong(shipMentMap.get("shipment_id").toString()));
            Map<String, Object> orderResult = orderService.getShipmentDetails(saveShipmentParam);
            orderResult.put("shipmentId", Long.parseLong(shipMentMap.get("shipment_id").toString()));

            if (isSaveSubShipment) {
                //添加第三段物流
                logger.info(
                        "order updateLogisticsProduct 添加sub_shipment物流信息   调用接口   iShipmentService.saveShipmentByOrderId 入参:" + new Gson().toJson(orderResult));
                iShipmentService.saveShipmentByOrderId(orderResult);
            }

        } else {
            info.put("code", StatusType.ORDER_ERROR_CODE);
            result.setInfoMap(info);
            result.setMsg("Package failure");
        }
        result.setInfoMap(info);
        return result;
    }*/

    @RequestMapping(value = "/saveUserComment", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage saveUserComment(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info("order saveUserComment Param : " + new Gson().toJson(map));
        ResultMessage message = new ResultMessage();
        try {
            if (null == map || 0 == map.size()) {
                logger.info("parameter cannot be null");
                message.errorStatus().putMsg("info", "Parameter cannot be null");
                return message;
            }
            if (map.get("logistics_product_id") == null || StringUtils.isBlank(map.get("logistics_product_id").toString())) {
                logger.info("logistics_product_id cannot be null");
                message.errorStatus().putMsg("info", "logistics_product_id cannot be null");
                return message;
            }
            if (map.get("comments") == null || StringUtils.isBlank(map.get("comments").toString())) {
                logger.info("comments cannot be null");
                //message.errorStatus().putMsg("info", "comments cannot be null");
                //return message;
            }
            if (map.get("order_exception_type_id") == null || StringUtils.isBlank(map.get("order_exception_type_id").toString())) {
                logger.info("order_exception_type_id cannot be null");
                message.errorStatus().putMsg("order_exception_type_id", "reason cannot be null");
                return message;
            }
            User user = this.getUser(httpRequest);
            if (user == null) {
                logger.info("user cannot be null");
                message.errorStatus().putMsg("info", "user cannot be null");
                return message;
            }
            map.put("created_by_user_id", user.getUserId());
            map.put("status", 1);
            Date currentDate = new Date();
            map.put("created_at", currentDate);

            System.out.println(user.getEmail());
            int result = orderExceptionService.saveOrderComments(map);
            if (result == 1) {
                message.successStatus().putMsg("SUCCESS", "result " + result);
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("errorMsg : " + e.getMessage());
            message.errorStatus().putMsg("errorMsg", e.getMessage());
        }
        return message;
    }

    /**
     * 修改stockLocation
     * */
    @RequestMapping(value = "/saveOrderStockLocation", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage saveOrderStockLocation(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) {
        logger.info("order saveOrderStockLocation Param : " + new Gson().toJson(map));
        ResultMessage message = new ResultMessage();
        try {
            if (null == map || 0 == map.size()) {
                logger.info("parameter cannot be null");
                message.errorStatus().putMsg("info", "Parameter cannot be null");
                return message;
            }
            if (map.get("logistics_product_id") == null || StringUtils.isBlank(map.get("logistics_product_id").toString())) {
                logger.info("logistics_product_id cannot be null");
                message.errorStatus().putMsg("info", "logistics_product_id cannot be null");
                return message;
            }
            if(map.get("locationId") == null || StringUtils.isBlank(map.get("locationId").toString())){
                logger.info("locationId cannot be null");
                message.errorStatus().putMsg("info", "locationId cannot be null");
                return message;
            }
            Long lpId = Long.valueOf(map.get("logistics_product_id").toString());
            Long locationId = Long.valueOf(map.get("locationId").toString());
            StockLocation stockLocation = stockLocationService.getStockLocation(locationId);
            LogisticsProduct logisticsProduct = new LogisticsProduct();
            logisticsProduct.setLogistics_product_id(lpId);
            logisticsProduct.setStock_location_id(locationId);
            if (stockLocation!=null){
                logisticsProduct.setStock_location(stockLocation.getStockLocation());
            }
            int result = iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);
            if (result == 1) {
                message.successStatus().putMsg("SUCCESS", "result " + result);
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("errorMsg : " + e.getMessage());
            message.errorStatus().putMsg("errorMsg", e.getMessage());
        }
        return message;
    }

    @RequestMapping(value = "/getExceptionType", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage getExceptionType() {
        logger.info("order getExceptionType ");
        ResultMessage message = new ResultMessage();
        try {
            List<Map<String, Object>> list = orderExceptionTypeService.getExceptionType();
            if (null != list && 0 < list.size()) {
                message.successStatus().putMsg("INFO", "SUCCESS").setData(list);
                return message;
            }
            message.errorStatus().putMsg("INFO", "SUCCESS").setData(null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("errorMsg : " + e.getMessage());
            message.errorStatus().putMsg("errorMsg", e.getMessage());
        }
        return message;
    }

    @RequestMapping(value = "/updateException", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage updateException(@RequestBody Map<String, Object> map) {
        logger.info("order updateException Param : " + new Gson().toJson(map));
        ResultMessage message = new ResultMessage();
        try {
            if (null == map || 0 == map.size()) {
                logger.info("parameter cannot be null");
                message.errorStatus().putMsg("info", "Parameter cannot be null");
                return message;
            }
            if (map.get("status") == null || StringUtils.isBlank(map.get("status").toString())) {
                logger.info("status cannot be null");
                message.errorStatus().putMsg("info", "status cannot be null");
                return message;
            }
            if (map.get("modified_at") == null || StringUtils.isBlank(map.get("modified_at").toString())) {
                logger.info("modified_at cannot be null");
                message.errorStatus().putMsg("info", "modified_at cannot be null");
                return message;
            }
            if (map.get("modified_by_user_id") == null || StringUtils.isBlank(map.get("modified_by_user_id").toString())) {
                logger.info("modified_by_user_id cannot be null");
                message.errorStatus().putMsg("info", "modified_by_user_id cannot be null");
                return message;
            }
            if (map.get("resolution") == null || StringUtils.isBlank(map.get("resolution").toString())) {
                logger.info("resolution cannot be null");
                message.errorStatus().putMsg("info", "resolution cannot be null");
                return message;
            }
            if (map.get("order_exception_id") == null || StringUtils.isBlank(map.get("order_exception_id").toString())) {
                logger.info("order_exception_id cannot be null");
                message.errorStatus().putMsg("info", "order_exception_id cannot be null");
                return message;
            }
            int result = orderExceptionService.updateOrderExceptionById(map);
            if (1 == result) {
                message.successStatus().putMsg("INFO", "SUCCESS").setData(result);
                return message;
            }
            message.errorStatus().putMsg("INFO", "SUCCESS").setData(-1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("errorMsg : " + e.getMessage());
            message.errorStatus().putMsg("errorMsg", e.getMessage());
        }
        return message;
    }



    /**
     * 导出订单列表
     * @param map
     * @param httpRequest
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = "/exportOrderList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage exportOrderList(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest,HttpServletResponse httpResponse) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        ResultMessage result = new ResultMessage();
        result.errorStatus();

        if (map == null || map.size() == 0 || map.get("status") == null || StringUtils.isBlank(map.get("status").toString())) {
            result.setMsg("Parameter cannot be empty");
            return result;
        }

        User user = this.getUser(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }
        boolean isParent =false;
        List<Vendor> vendors = null;
        try {
            vendors = vendorService.getVendorsByUserId(user.getUserId());
            if(CollectionUtils.isNotEmpty(vendors)){
                if(CollectionUtils.isNotEmpty(vendors.stream().filter(e -> !e.getUserId().equals(user.getUserId())).collect(Collectors.toList()))){
                    isParent=true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vendors == null) {
            result.setMsg("Please log in again");
            return result;
        }
        List<Long> vendorIds = vendors.stream().map(Vendor::getVendorId).collect(Collectors.toList());

        List<Long> logisticsProductIdList = new ArrayList<>();
        if (map.get("logisticsProductIds") != null && StringUtil.isNotEmpty(map.get("logisticsProductIds").toString())) {
            logisticsProductIdList = Arrays.asList((Long[]) ConvertUtils.convert(map.get("logisticsProductIds").toString().split(","), Long.class));
        }

//        Long vendorId = vendor.getVendorId();
        String status = map.get("status").toString();
        //根据订单状态查询订单
        logger.info(MessageFormat.format("order getOrderList 调用接口 orderService.getOrderListByParams 查询订单信息  入参 status:{0},vendorIds:{1},logisticsProductIdList:{2}",
                status, vendorIds, logisticsProductIdList));
        List<Map<String, Object>> orderList = null;
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("vendorIds", vendorIds);
        params.put("logisticsProductIds", logisticsProductIdList);
        orderList = orderService.getOrderListByParams(params);
        if (orderList == null || orderList.size() == 0) {
            logger.info("获取失败");
            result.setMsg("Query order fail,Check parameters, please ");
            return result;
        }
        logger.info("exportOrderList 解析订单列表信息");
        for (Map<String, Object> info : orderList) {
            //计算折扣
            arithmeticalDiscount(info);
        }
        logger.info("exportOrderList 设置文件目录路径");
        String dateStr = DateUtils.getStrDate(new Date(), "yyyyMMddHHmmss");
        String fileName = dateStr + ".xls";
        String path = commonsProperties.getOrderPath() + "download/";
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String filePath = path + fileName;

        logger.info("exportOrderList 生成订单文件");
        generateOrderExcel("Order", orderList, filePath,isParent);

        File newFile = new File(filePath);
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
        httpResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(newFile);
            bis = new BufferedInputStream(fis);
            OutputStream os = httpResponse.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        result.successStatus();
        return result;
    }

    private String generateOrderExcel(String excelName, List<Map<String, Object>> orderList, String filePath,boolean isParent) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        int rowLength = 0;
        HSSFSheet sheet = workbook.createSheet(excelName);

        //图片处理
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

        String[] excelHeaders = null;
        if(isParent){
            excelHeaders=new String[]{"Order Line No.", "Order Date", "Picture", "Brand", "Name", "Designer ID", "Color Code", "Size", "Retail Price", "Purchase Price", "VAT?", "Purchase Discount", "Consignee Geography","Boutique"};
        }else{
            excelHeaders=new String[]{"Order Line No.", "Order Date", "Picture", "Brand", "Name", "Designer ID", "Color Code", "Size", "Retail Price", "Purchase Price", "VAT?", "Purchase Discount", "Consignee Geography"};
        }

        // 创建表头
        HSSFRow row1 = sheet.createRow(rowLength);
        for (int i = 0, iLen = excelHeaders.length; i < iLen; i++) {
            HSSFCell cell = row1.createCell(i);
            cell.setCellValue(excelHeaders[i]);
        }

        rowLength++;
        HSSFRow row = null;
        HSSFCell cell = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        for (Map<String, Object> order : orderList) {
            row = sheet.createRow(rowLength);
            row.setHeightInPoints(125F);
            String[] values = null;
            if (isParent){
                values =new String[]{
                        transforNullValue(order.get("order_line_num")),
                        transforNullValue(sdf.format(order.get("created_at"))),
                        transforNullValue(order.get("cover_img")),
                        transforNullValue(order.get("brandName")),
                        transforNullValue(order.get("name")),
                        transforNullValue(order.get("brandID")),
                        transforNullValue(order.get("colorCode")),
                        transforNullValue(order.get("size")),
                        transforNullValue("€ " + new BigDecimal(order.get("price").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()),
                        transforNullValue("€ " + new BigDecimal(order.get("in_price").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()),
                        transforNullValue(GeographyEnum.EUROPEAN_UNION.getId().equals(order.get("geography_id").toString()) ? "incld. VAT" : "excld. VAT"),
                        transforNullValue(order.get("supply_price_discount")),
                        transforNullValue(order.get("geography_name")),
                        transforNullValue(order.get("vendor_name"))
                };
            }else {
                values = new String[]{
                        transforNullValue(order.get("order_line_num")),
                        transforNullValue(sdf.format(order.get("created_at"))),
                        transforNullValue(order.get("cover_img")),
                        transforNullValue(order.get("brandName")),
                        transforNullValue(order.get("name")),
                        transforNullValue(order.get("brandID")),
                        transforNullValue(order.get("colorCode")),
                        transforNullValue(order.get("size")),
                        transforNullValue("€ " + new BigDecimal(order.get("price").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()),
                        transforNullValue("€ " + new BigDecimal(order.get("in_price").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()),
                        transforNullValue(GeographyEnum.EUROPEAN_UNION.getId().equals(order.get("geography_id").toString()) ? "incld. VAT" : "excld. VAT"),
                        transforNullValue(order.get("supply_price_discount")),
                        transforNullValue(order.get("geography_name"))
                };
            }

            //将数据放到excel中，第三列是图片需要特殊处理
            for (int i = 0; i < excelHeaders.length; i++) {
                cell = row.createCell(i);
                if (i == 2) {
                    String urlList = values[i];
                    JsonArray urlJsonArray = new JsonParser().parse(urlList).getAsJsonArray();
                    generateProductImage(workbook, patriarch, new Gson().fromJson(urlJsonArray.get(0), String.class), i, rowLength);
                } else {
                    cell.setCellValue(values[i]);
                }
            }
            rowLength++;
        }
        //宽度自适应
        for (int i = 0; i < excelHeaders.length; i++) {
            if (i != 2) {
                sheet.autoSizeColumn(i, true);
            } else {
                sheet.setColumnWidth(i, 16 * 256);
            }
        }

        FileOutputStream fileOut = null;
        try {

            fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    private void generateProductImage(HSSFWorkbook workbook, HSSFPatriarch patriarch, String pictureUrl, int i, int j) {
        //处理商品图片
        if (StringUtils.isNotBlank(pictureUrl)) {
            try {
                //添加域名替换
                pictureUrl = pictureUrl.replace("image.intramirror.com", "sha-oss-static.oss-cn-shanghai.aliyuncs.com");
                //获取网络图片 ,压缩图片
                //URL url = new URL(pictureUrl);
                URL url = new URL(pictureUrl+"?x-oss-process=image/resize,m_fill,w_110,limit_0/auto-orient,0/quality,q_90");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                //超时响应时间为5秒
                conn.setConnectTimeout(5 * 1000);
                InputStream inStream = conn.getInputStream();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                inStream.close();
                byte[] data = outStream.toByteArray();
                //anchor主要用于设置图片的属性
                HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) i, j, (short) (i + 1), j + 1);
                //Sets the anchor type （图片在单元格的位置）
                //0 = Move and size with Cells, 2 = Move but don't size with cells, 3 = Don't move or size with cells.
                anchor.setAnchorType(0);
                patriarch.createPicture(anchor, workbook.addPicture(data, HSSFWorkbook.PICTURE_TYPE_JPEG));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String transforNullValue(Object obj) {
        if(obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }

}
