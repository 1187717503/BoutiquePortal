package com.intramirror.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.main.api.service.ExchangeRateService;
import com.intramirror.order.api.common.OrderCancelType;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.common.PaymentMethodType;
import com.intramirror.order.api.common.PaymentType;
import com.intramirror.order.api.model.Logistics;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.Order;
import com.intramirror.order.api.model.OrderLogistics;
import com.intramirror.order.api.model.PaymentOffline;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.LogisticsService;
import com.intramirror.order.api.service.OrderLogisticsService;
import com.intramirror.order.api.service.PaymentOfflineService;
import com.intramirror.order.api.vo.CheckoutEntity;
import com.intramirror.order.api.vo.InputCreateOrder;
import com.intramirror.product.api.model.ShopProductSku;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ShopProductSkuService;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.common.Cache;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RAtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderInputCreateOrderService {
    private static Logger logger = LoggerFactory.getLogger(OrderInputCreateOrderService.class);

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

    @Autowired
    private ShopProductSkuService shopProductSkuService;

    @Autowired
    private ISkuStoreService skuStoreService;

    @Autowired
    private PaymentOfflineService paymentOfflineService;

    @Autowired
    private SkuService skuService;

    /**
     * @param user
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> orderInputCreateOrder(InputCreateOrder inputCreateOrder, User user) throws Exception {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("status", StatusType.FAILURE);

        // Coupons Price
        BigDecimal coupon_fee = new BigDecimal(0);

        // 总支付的金额
        BigDecimal pay_fee = inputCreateOrder.getSubtotalPrice().subtract(coupon_fee).add(inputCreateOrder.getTotalShipFee()).add(
                inputCreateOrder.getTotalTaxFee()).setScale(2, RoundingMode.HALF_UP);

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
        order.setTotalRmb(order.getTotalSalePriceRmb().add(order.getTotalShippingFeeRmb()).add(order.getTotalTaxRmb()).setScale(2, RoundingMode.HALF_UP)
                .toString());

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

            List<Long> skuStoreSkuIdList = new ArrayList<>();
            List<Long> skuIdList = new ArrayList<>();

            List<LogisticsProduct> sendEmailList = new ArrayList<LogisticsProduct>();
            for (int j = 0; j < inputCreateOrder.checkoutListStr.size(); j++) {

                CheckoutEntity obj = inputCreateOrder.checkoutListStr.get(j);
                logger.info("checkout:" + obj);

                Long shop_product_sku_id = obj.getShop_product_sku_id();
                ShopProductSku shopProductSku = shopProductSkuService.getShopProductSkuById(shop_product_sku_id);
                Sku sku = skuService.getSkuById(shopProductSku.getSkuId());
                logger.info("OrderInputCreateOrderService,orderInputCreateOrder,getSkuById,shop_product_sku_id:" + shop_product_sku_id + ",sku:" + JSONObject
                        .toJSONString(sku));

                LogisticsProduct logisticsProduct = new LogisticsProduct();
                logisticsProduct.setOrder_logistics_id(orderLogistics.getOrderLogisticsId());
                logisticsProduct.setShop_product_sku_id(shop_product_sku_id);
                logisticsProduct.setIn_price(sku.getInPrice());
                logisticsProduct.setSale_price(sku.getImPrice());
                logisticsProduct.setRetail_price(sku.getPrice());
                logisticsProduct.setAmount(obj.getQuantity());
                logisticsProduct.setFee(obj.getTax_fee().multiply(BigDecimal.valueOf(obj.getQuantity()).setScale(2, RoundingMode.HALF_UP)));
                logisticsProduct.setDeduct_amount(0);
                logisticsProduct.setDeduct_fee(new BigDecimal(0));
                logisticsProduct.setRemain_amount(obj.getQuantity());
                logisticsProduct.setRemain_fee(logisticsProduct.getFee());
                logisticsProduct.setStatus(OrderStatusType.PENDING);
                logisticsProduct.setCreated_at(new Date());
                logisticsProduct.setUpdated_at(new Date());
                logisticsProduct.setEnabled(EnabledType.USED);

                // start 写默认值，为了生成订单
                // logisticsProduct.setOrder_line_num(order.getOrderNum() +
                // String.valueOf(100 + j));

                String vendorCode = "";
                List<Map<String, Object>> list = productService.getVendorCodeBySku(logisticsProduct.getShop_product_sku_id());
                if (null != list && list.size() > 0) {
                    Map<String, Object> map = list.get(0);
                    vendorCode = map.get("vendor_code").toString();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
                String date = sdf.format(new Date());
                Long num = getOrderLineNum(vendorCode + date);
                logisticsProduct.setOrder_line_num(vendorCode + date + (num < 1000 ? String.format("%04d", num) : num));

                String shopProductSkuIds = obj.getShop_product_sku_id().toString();
                Map<String, String> objectMap = vendorService.getProductSkuVendorIdMap(shopProductSkuIds.split(","));

                logisticsProduct.setVendor_id(Long.valueOf(objectMap.get(shopProductSkuIds)));
                logisticsProduct.setShipping_fee(obj.getShipping_fee());
                logisticsProduct.setTax_fee(obj.getTax_fee());
                logisticsProduct.setTax_id(obj.getTax_id());
                logisticsProduct.setTax_input_amt(new BigDecimal(0));

                logisticsProduct = logisticsProductService.createLogisticsProduct(logisticsProduct);

                results.put("logisticsProduct" + j, logisticsProduct);
                skuStoreSkuIdList.add(obj.getShop_product_sku_id());
                // end 写默认值，为了生成订单

                sendEmailList.add(logisticsProduct);
            }

            List<List<String>> listSkuIdInfo = setArray(skuStoreSkuIdList);
            if (listSkuIdInfo.size() > 0 && Helper.checkNotNull(listSkuIdInfo.get(0)) && Helper.checkNotNull(listSkuIdInfo.get(1))) {
                String shopProductSkuIdString = StringUtils.join(listSkuIdInfo.get(0), ",");
                String[] shopProductSkuIds = shopProductSkuIdString.split(",");
                List<Map<String, Object>> skuStoreSkuId = shopProductSkuService.getSkuIdByShopProductSkuId(shopProductSkuIds);
                if (skuStoreSkuId.size() > 0 && skuStoreSkuId.get(0) != null) {
                    for (int i = 0; i < skuStoreSkuId.size(); i++) {
                        skuIdList.add((Long) skuStoreSkuId.get(i).get("sku_id"));
                    }
                    String skuIdString = StringUtils.join(skuIdList, ",");
                    String[] skuIds = skuIdString.split(",");
                    skuStoreService.updateSkuStoreReserved(listSkuIdInfo.get(1), skuIds, skuIdList);
                }
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

            // 6.存入支付信息
            PaymentOffline paymentOffline = new PaymentOffline();
            paymentOffline.setOrderId(order.getOrderId());
            paymentOffline.setPaymentType((byte) PaymentType.PAYMENT);
            if ("1".equals(inputCreateOrder.getPaymentMethod())) {
                paymentOffline.setPaymentMethod((byte) PaymentMethodType.WECHAT);
            } else if ("2".equals(inputCreateOrder.getPaymentMethod())) {
                paymentOffline.setPaymentMethod((byte) PaymentMethodType.BANKCARD);
            } else if ("3".equals(inputCreateOrder.getPaymentMethod())) {
                paymentOffline.setPaymentMethod((byte) PaymentMethodType.ALIPAY);
            }
            paymentOffline.setAmt(inputCreateOrder.getAmt());

            java.text.SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = formatter.parse(inputCreateOrder.getPaidAt());

            paymentOffline.setPaidAt(date);
            paymentOffline.setBuyerName(inputCreateOrder.getPaymentBuyerName());
            paymentOffline.setEnabled(EnabledType.USED);
            paymentOffline = paymentOfflineService.createPaymentOffline(paymentOffline);
            results.put("paymentOffline", paymentOffline);

            /**
             * -----------------------------start send
             * mail-------------------------------
             **/
            logger.info("start send mail sendEmailList.size:" + sendEmailList.size());
            /*// 遍历发送邮件
            if (sendEmailList != null && sendEmailList.size() > 0) {
				for (LogisticsProduct logisticsProduct : sendEmailList) {
					try {
						logger.info("start send mail logisticsProductId:" + logisticsProduct.getLogistics_product_id());
						Map<String, Object> map = orderService
								.getOrderLogisticsInfoByIdWithSql(logisticsProduct.getLogistics_product_id());
						if (map != null) {
							OrderMail.sendOrderMail(map);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}*/
            /**
             * -----------------------------end send
             * mail-------------------------------
             **/

            /**
             * -----------------------------同步订单
             * start---------------------------------------
             **/
            if (sendEmailList != null && sendEmailList.size() > 0) {
                /*for (LogisticsProduct logisticsProduct : sendEmailList) {
                    *//*logger.info("调用接口同步订单,入参  LogisticsProduct:" + new Gson().toJson(logisticsProduct));
                    SystemCallFactory systemCallFactory = new SystemCallFactory();
					systemCallFactory.createOrder(logisticsProduct.getVendor_id(),
							logisticsProduct.getLogistics_product_id());*//*

					*//** start update by dingyifan 20171017 *//*
                    GetPostRequestService getPostRequestService = new GetPostRequestService();
					String url = commonProperties.getAppOrderUrl()+"?logisticProductId="+logisticsProduct.getLogistics_product_id();
					logger.info("OrderInputCreateOrderServiceOrderInputCreateOrder,start,requestMethod,url:"+url);
					String response = getPostRequestService.requestMethod(GetPostRequestService.HTTP_POST,url,null);
					logger.info("OrderInputCreateOrderServiceOrderInputCreateOrder,end,requestMethod,url:"+url+",response:"+response);

					*//** end update by dingyifan 20171017 *//*

				}*/
                results.put("sendEmailList", sendEmailList);
            }
            /**
             * -----------------------------同步订单
             * end-----------------------------------------
             **/

        }
        results.put("status", StatusType.SUCCESS);
        return results;
    }

    public List<List<String>> setArray(List<Long> skuStoreSkuIdList) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < skuStoreSkuIdList.size(); i++) {
            System.out.println(skuStoreSkuIdList.get(i));
            System.out.println(map.get(skuStoreSkuIdList.get(i).toString()));
            if (null != map.get(skuStoreSkuIdList.get(i).toString())) {
                map.put(skuStoreSkuIdList.get(i).toString(), map.get(skuStoreSkuIdList.get(i - 1).toString()) + 1); // value+1
            } else {
                map.put(skuStoreSkuIdList.get(i).toString(), 1);
            }
        }
        List<String> listKey = new ArrayList<String>();
        List<String> listValue = new ArrayList<String>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            System.out.println("key is :" + key + "---value :" + value);
            listKey.add(key);
            listValue.add(value);
        }
        List<List<String>> list = new ArrayList<List<String>>();
        list.add(listKey);
        list.add(listValue);
        return list;
    }

    public Long getOrderLineNum(String vendorCodeDate) {
        Cache cache = Cache.getInstance();
        RAtomicLong ratomicLong = cache.getIncr(vendorCodeDate);
        Long numLong = ratomicLong.getAndIncrement();
        logger.info("numResult for {} ", numLong);
        return numLong;
    }
}
