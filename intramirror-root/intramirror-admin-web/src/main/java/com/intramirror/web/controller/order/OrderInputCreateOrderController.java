package com.intramirror.web.controller.order;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.main.api.service.*;
import com.intramirror.order.api.common.OrderCancelType;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.model.Logistics;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.Order;
import com.intramirror.order.api.model.OrderLogistics;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.LogisticsService;
import com.intramirror.order.api.service.OrderLogisticsService;
import com.intramirror.order.api.vo.InputCreateOrder;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderInputCreateOrderController extends BaseController {

    private static Logger logger = Logger.getLogger(OrderInputCreateOrderController.class);

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ILogisticsProductService logisticsProductService;

    @Autowired
    private IProductService productService;

    /**
     * Wang
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/input_create_order", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage orderInputCreateOrder(@RequestBody Map<String, Object> paramMap, HttpServletRequest httpRequest) throws Exception {
        ResultMessage result = new ResultMessage();
        Map<String, Object> results = new HashMap<>();
        result.errorStatus();

        InputCreateOrder inputCreateOrder = new Gson().fromJson(paramMap.toString(), InputCreateOrder.class);

        System.out.println("+++++++++" + paramMap.toString());

        User user = this.getUserInfo(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        try {
            // Coupons Price
            BigDecimal coupon_fee = new BigDecimal(0);

            //总支付的金额
            BigDecimal pay_fee = inputCreateOrder.getSubtotalPrice().subtract(coupon_fee).add(inputCreateOrder.getTotalShipFee()).add(inputCreateOrder.getTotalTaxFee()).setScale(2, RoundingMode.HALF_UP);

            // 1. 创建 order
            Order order = new Order();
            order.setUserId(user.getUserId());
            order.setFee(inputCreateOrder.getSubtotalPrice());
            order.setCoupon("");
            order.setCouponFee(coupon_fee);
            order.setDiscount(0);
            order.setDiscountFee(new BigDecimal(0));
            order.setPayFee(pay_fee);
            order.setShipFee(inputCreateOrder.getTotalShipFee());
            order.setTaxFee(inputCreateOrder.getTotalTaxFee());
            order.setPayWay((byte) 3);
            order.setOnlinePaymentId(-1l);
            order.setStatus(OrderStatusType.PENDING);
            order.setCancelType((byte) OrderCancelType.NOT_CANCEL);
            order.setCreatedAt(new Date());
            order.setUpdatedAt(new Date());
            order.setEnabled(EnabledType.USED);
            order.setWechat(inputCreateOrder.buyerWechat);
            order.setContactPhone(inputCreateOrder.buyerPhone);
            order.setBuyerName(inputCreateOrder.buyerName);

            List<Map<String, Object>> exchangerRate = exchangeRateService.getShipFeeByCityId(null, null);
            order.setCurrentRate(BigDecimal.valueOf(Double.valueOf(exchangerRate.get(0).get("exchange_rate").toString())));

            order.setRemainingRmb(String.valueOf(inputCreateOrder.getBalanceDue().setScale(2, RoundingMode.HALF_UP)));
            order.setAreaCode("");

            order.setTotalTaxRmb(order.getTaxFee().multiply(order.getCurrentRate()).setScale(2, RoundingMode.HALF_UP));
            order.setTotalSalePriceRmb(order.getFee().multiply(order.getCurrentRate()).setScale(2, RoundingMode.HALF_UP));
            order.setTotalShippingFeeRmb(order.getShipFee().multiply(order.getCurrentRate()).setScale(2, RoundingMode.HALF_UP));
            order.setTotalRmb(order.getTotalSalePriceRmb().add(order.getTotalShippingFeeRmb()).add(order.getTotalTaxRmb()).setScale(2, RoundingMode.HALF_UP).toString());

            order = orderService.createOrder(order);
            logger.info("订单" + order.getOrderId() + "创建order前端入参:" + JSON.toJSONString(order));
            results.put("order", order);
            if (order != null) {
                // 补充orderLogistics表
                // orderLogistics
                // 2. 创建order_logistics表，其中logistics_id 和 return
                OrderLogistics orderLogistics = new OrderLogistics();
                orderLogistics.setShopId(Long.valueOf(65));
                orderLogistics.setOrderId(order.getOrderId());
                orderLogistics.setDestinationType((byte) 0);
                orderLogistics.setType((byte) 0); // 暂无
                orderLogistics.setShipPayer((byte) 0);// 默认用户支付
                orderLogistics.setShipFee(inputCreateOrder.getTotalShipFee());
                orderLogistics.setFee(inputCreateOrder.getSubtotalPrice());
                orderLogistics.setDeductFee(coupon_fee);
                orderLogistics.setRemainFee(pay_fee);
                orderLogistics.setReceiveFee(new BigDecimal(0));
                orderLogistics.setStatus((byte) OrderStatusType.PENDING);
                orderLogistics.setReturnApplicationId(0l);
                orderLogistics.setLogisticsId(0l);
                orderLogistics.setRemark("");
                orderLogistics.setCreatedAt(new Date());
                orderLogistics.setUpdatedAt(new Date());
                orderLogistics.setEnabled(EnabledType.USED);
                orderLogistics.setIsShipFee(false);
                orderLogistics = orderLogisticsService.createOrderLogistics(orderLogistics);
                results.put("orderLogistics", orderLogistics);

                for (int j = 0; j < inputCreateOrder.checkoutListStr.size(); j++) {
                    JsonObject obj = inputCreateOrder.checkoutListStr.get(j);
                    logger.info("checkout:" + obj);

                    LogisticsProduct logisticsProduct = new LogisticsProduct();
                    logisticsProduct.setOrder_logistics_id(orderLogistics.getOrderLogisticsId());
                    logisticsProduct.setShop_product_sku_id(obj.get("shop_product_sku_id").getAsLong());
                    logisticsProduct.setIn_price(obj.get("in_price").getAsBigDecimal());
                    logisticsProduct.setSale_price(obj.get("sale_price").getAsBigDecimal());
                    logisticsProduct.setAmount(obj.get("quantity").getAsInt());
                    logisticsProduct.setFee(obj.get("sale_price").getAsBigDecimal().multiply(BigDecimal.valueOf(obj.get("quantity").getAsInt())).setScale(2, RoundingMode.HALF_UP));
                    logisticsProduct.setDeduct_amount(0);
                    logisticsProduct.setDeduct_fee(new BigDecimal(0));
                    logisticsProduct.setRemain_amount(obj.get("quantity").getAsInt());
                    logisticsProduct.setRemain_fee(logisticsProduct.getFee());
                    logisticsProduct.setStatus(OrderStatusType.PENDING);
                    logisticsProduct.setCreated_at(new Date());
                    logisticsProduct.setUpdated_at(new Date());
                    logisticsProduct.setEnabled(EnabledType.USED);

                    // start 写默认值，为了生成订单
                    // logisticsProduct.setOrder_line_num(order.getOrderNum() + String.valueOf(100 + j));

                    String vendorCode = "";
                    List<Map<String, Object>> list = productService.getVendorCodeBySku(logisticsProduct.getShop_product_sku_id());
                    if (null != list && list.size() > 0) {
                        Map<String, Object> map = list.get(0);
                        vendorCode = map.get("vendor_code").toString();
                    }

                    logisticsProduct.setOrder_line_num(vendorCode + com.intramirror.common.help.StringUtils.getUUID(8));

                    String productSkuIds = obj.get("shop_product_sku_id").getAsString();
                    Map<String, String> objectMap = vendorService.getProductSkuVendorIdMap(productSkuIds.split(","));

                    logisticsProduct.setVendor_id(Long.valueOf(objectMap.get(productSkuIds)));
                    logisticsProduct.setShipping_fee(new BigDecimal(obj.get("shipping_fee").getAsString()));
                    logisticsProduct.setTax_fee(new BigDecimal(obj.get("tax_fee").getAsString()));
                    logisticsProduct.setTax_id(obj.get("tax_id").getAsLong());
                    logisticsProduct.setTax_input_amt(new BigDecimal(0));

                    logisticsProduct = logisticsProductService.createLogisticsProduct(logisticsProduct);

                    results.put("logisticsProduct", logisticsProduct);
                    // end 写默认值，为了生成订单
                }

                // 4. 根据传入地址，在logistics表中加入收货地址数据
                // 创建logistics 需要
                Logistics logistics = new Logistics();
                logistics.setCompany("");
                logistics.setOrderLogisticsId(orderLogistics.getOrderLogisticsId());
                logistics.setGeographyId(inputCreateOrder.geographyId);
                logistics.setUserRecName(inputCreateOrder.shipToMap.get("user_rec_name").toString());
                logistics.setUserRecCountry(inputCreateOrder.shipToMap.get("user_rec_country").toString());
                logistics.setUserRecProvince(inputCreateOrder.shipToMap.get("user_rec_province").toString());
                logistics.setUserRecCity(inputCreateOrder.shipToMap.get("user_rec_city").toString());
                logistics.setUserRecArea(inputCreateOrder.shipToMap.get("user_rec_area").toString());
                logistics.setUserRecAddr(inputCreateOrder.shipToMap.get("user_rec_addr").toString());
                logistics.setUserRecCode(inputCreateOrder.shipToMap.get("user_rec_code").toString());
                logistics.setUserRecMobile(inputCreateOrder.shipToMap.get("user_rec_mobile").toString());
                logistics.setRecName("");
                logistics.setRecProvince("");
                logistics.setRecMobile("");
                logistics.setLogisticsFee(new BigDecimal(0));
                logistics.setWeight(0f);
                logistics.setBeginTime(new Date());
                logistics.setReceiveTime(new Date());
                logistics.setInsureFee(new BigDecimal(0));
                logistics.setLogisticsStaff("");
                logistics.setLogisticsUrl("");
                logistics.setStatus(OrderStatusType.PENDING);
                logistics.setCreatedAt(new Date());
                logistics.setUpdatedAt(new Date());
                logistics.setEnabled(EnabledType.USED);
                logistics = logisticsService.createLogistics(logistics);
                results.put("logistics", logistics);

                // 5. 把新增logistics_填入order_logistics中
                OrderLogistics updateOrderLogistics = new OrderLogistics();
                updateOrderLogistics.setOrderLogisticsId(orderLogistics.getOrderLogisticsId());
                updateOrderLogistics.setLogisticsId(logistics.getLogisticsId());
                logger.info("订单" + order.getOrderId() + "创建订单后更新运费 orderLogistics 入参" + JSON.toJSON(updateOrderLogistics));
                orderLogisticsService.updateOrderLogistics(updateOrderLogistics);
            }
            result.successStatus();
            result.setData(results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("接口执行结束,响应数据,result:" + new Gson().toJson(result));
        return result;
    }
}
