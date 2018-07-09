/**
 * 
 */
package com.intramirror.web.controller.buyersys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jodd.util.URLDecoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.order.api.common.ConverMapToOrderMager;
import com.intramirror.order.api.model.OrderMager;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderExceptionService;
//import com.intramirror.web.common.GetPostRequestUtil;

import pk.shoplus.DBConnector;
import pk.shoplus.enums.ApiParamterEnum;
import pk.shoplus.enums.CloudStoreEnum;
import pk.shoplus.model.OrderRefund;
import pk.shoplus.parameter.StatusType;

/**
 * @author wzh 
 * @date 17-09-12
 *
 */
@Service("tongCreateOrder")
public class TongCreateOrder extends BuyerSystemCall{
	
    private static Logger logger = Logger.getLogger(TongCreateOrder.class);

	@Autowired
	IOrderExceptionService orderExceptionServiceImpl;
	
	@Autowired
	ILogisticsProductService logisticsProductServiceImpl;
	

	 @Override
	 public String createOrder(Long vendorId, Long logisticsProductId) throws Exception {
		//TODO
        logger.info("buyersys Tong createOrder 买手店: "+vendorId +" 通过EDS系统创建订单: " +logisticsProductId);
        String result = "SUCCESS";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
        	Properties props = new Properties();
        	InputStream in = TongCreateOrder.class.getResourceAsStream("/orderapi-cofig.properties");
        	props.load(in);
        	
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("vendor_id", vendorId);
        	conditionMap.put("logistics_product_id", logisticsProductId);
        	
        	logger.info(MessageFormat.format("buyersys Tong createOrder 获取需要处理的订单   入参:{0}", new Gson().toJson(conditionMap)));
        	Map<String, Object> orderInfoMap= logisticsProductServiceImpl.getOrderInfoByVendorId(conditionMap);
        	
        	//获取需要处理的订单
            logger.info(MessageFormat.format("buyersys Tong createOrder 获取需要处理的订单   result:{0}", new Gson().toJson(orderInfoMap)));
            if (orderInfoMap == null || orderInfoMap.size() == 0){
            	logger.info("buyersys Tong createOrder 获取到的订单信息为空    入参:"+new Gson().toJson(conditionMap));
            	return "ERROR";
            }
            
			Map<String,Object> params = new HashMap<>();
			params.put(ApiParamterEnum.MERCHANT_ID.getCode(),props.getProperty("merchant_id"));
			params.put(ApiParamterEnum.TOKEN.getCode(),props.getProperty("token"));
			
			OrderMager orderMager =ConverMapToOrderMager.convertMapToBean(orderInfoMap);
			
			JSONObject orderJSON=new JSONObject();
        	JSONObject orderItemJSON=new JSONObject();
        	JSONObject shippingInfoJSON=new JSONObject();
        	JSONObject billingInfoJSON=new JSONObject();
        	JSONObject shippingAddressJSON=new JSONObject();
			JSONArray orderItemsArray = new JSONArray();
			
			//订单参数
			orderJSON.put(CloudStoreEnum.MERCHANTID.getCode(), params.get(CloudStoreEnum.MERCHANTID.getCode()));
			orderJSON.put(CloudStoreEnum.TOKEN.getCode(), params.get(CloudStoreEnum.TOKEN.getCode()));
			orderJSON.put("shopOrderId", orderMager.getOrderNumber());
			orderJSON.put("status", orderMager.getStatus());
			orderJSON.put("statusDate", sdf.format(orderMager.getUpdatedTime()));
			orderJSON.put("orderDate", sdf.format(orderMager.getCreateTime()));
			orderJSON.put("orderTotalPrice", orderMager.getTotalPrice());
			
			//子订单参数
			orderItemJSON.put("sku",orderMager.getSku());
			orderItemJSON.put("qty", orderMager.getItemsCount());
			orderItemJSON.put("cur", 1);
			orderItemJSON.put("price", orderMager.getPrice().multiply(new BigDecimal(orderMager.getItemsCount())));
			
			//shippingAddressJSON
			shippingAddressJSON.put("lastname", orderMager.customerName);
			shippingAddressJSON.put("firstname", 0);
			shippingAddressJSON.put("street", orderMager.getAddress());
			shippingAddressJSON.put("hn", 0);
			shippingAddressJSON.put("zip", orderMager.getZipcode());
			shippingAddressJSON.put("city", orderMager.getCity());
			shippingAddressJSON.put("province", orderMager.getProvince());
			shippingAddressJSON.put("state", orderMager.getCountry());
			
			//shippingInfo
			shippingInfoJSON.put("address", shippingAddressJSON);
			shippingInfoJSON.put("fees", 0);
			
			//billingInfo
        	billingInfoJSON.put("address", new JSONObject(shippingAddressJSON));
        	billingInfoJSON.put("total", 0);
        	billingInfoJSON.put("paymentMethod", 7);
			
			//拼接json
			orderItemsArray.add(orderItemJSON);
			orderJSON.put("items", orderItemsArray);
			orderJSON.put("shippingInfo", shippingInfoJSON);
			orderJSON.put("billingInfo", billingInfoJSON);
			String url = props.getProperty("tongUrl");
			
			//调用http请求,创建订单，返回状态
			logger.info("buyersys Tong createOrder 调用tong 接口下单  URL:"+url+",入参:"+orderJSON.toJSONString());
//			GetPostRequestUtil requestGet = new GetPostRequestUtil();
//			String json = requestGet.requestMethod("POST", url, orderJSON.toJSONString());
			Map<String, Object> resultMap = this.httpPost(url,orderJSON.toJSONString());
			
//			if(StringUtils.isBlank(json)){
			if(resultMap == null || resultMap.size() == 0 ){
				logger.info(MessageFormat.format("buyersys Tong createOrder 推送订单  调用接口下单返回结果为空  入参:{0}",new Gson().toJson(orderJSON)));
				return "ERROR";
			}
        	
			//转换成json格式，处理结果
			JSONObject resultJson = JSONObject.parseObject(resultMap.get("resultMeesage").toString());
			if(resultJson != null && "ok".equals(resultJson.getString("status"))){
				logger.info(MessageFormat.format("buyersys Tong createOrder 推送订单成功 ,返回的结果:{0}",resultJson.toString()));
			}else if(resultJson != null && "ko".equals(resultJson.getString("status"))){
				logger.info(MessageFormat.format("buyersys Tong createOrder 推送订单失败 ,返回的结果:{0}",resultJson.toString()));
				
				//下单失败,执行退款
				OrderRefund orderRefund = new OrderRefund();
				if (orderRefund != null){
					
					//调用退款
					logger.info(MessageFormat.format("buyersys Tong createOrder 推送订单失败 ,返回的结果:{0}",resultJson.toString()));
					Map<String, Object> refundMap = orderRefund.orderRefund(orderMager.getOrderId());
					logger.info(MessageFormat.format("buyersys Tong createOrder 推送订单失败,调用退款 接口,返回的结果:{0}",new Gson().toJson(refundMap)));
					
					//如果退款失败，记录失败信息
					Integer refundStatus = Integer.parseInt(refundMap.get("status").toString());
    				if(refundStatus != null && refundStatus.equals(StatusType.FAILURE)){
    					logger.info(MessageFormat.format("buyersys Tong createOrder 推送订单失败,返回结果:{0},调用退款接口失败，返回错误信息:{1}",new Gson().toJson(resultJson),new Gson().toJson(refundMap)));
    				}
    				
				}
				
				
				Map<String, Object> exception = new HashMap<String, Object>();
				exception.put("logistics_product_id", logisticsProductId);
				exception.put("comments", resultJson.toJSONString());
				//保存错误信息
				exception.put("order_exception_type_id", Long.parseLong("3"));
				exception.put("status", 1);
				exception.put("created_at", sdf.format(new Date()));
				//系统处理用户为0
				exception.put("created_by_user_id", Long.parseLong("0"));
				logger.info("buyersys Tong createOrder orderExceptionServiceImpl 记录错误信息  入参:" + new Gson().toJson(exception));
				int num = orderExceptionServiceImpl.saveOrderComments(exception);
				logger.info("buyersys Tong createOrder orderExceptionServiceImpl 记录错误信息  result: "+num);
				result = "ERROR";

			}else{
				logger.info("buyersys Tong createOrder info 返回结果:"+new Gson().toJson(resultJson));
			}
			
			
			
        } catch (Exception e) {
            logger.error("buyersys Tong createOrder 买手店: "+vendorId +" 通过tong系统创建订单: " +logisticsProductId +" 异常:" + e.toString());
            result = "ERROR";
            throw e;
        }

        return result;
    }

	 
	    /**
	     * post请求
	     * @param url         url地址
	     * @param jsonParam     参数
	     * @return
	     */
	    public Map<String, Object> httpPost(String url,String jsonParam){
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost method = new HttpPost(url);
	        Map<String, Object> resultMap = new HashMap<>();
	        resultMap.put("resultMeesage", "");
	        //post请求返回结果
	        try {
	        	
	            if (null != jsonParam) {
	                //解决中文乱码问题
	                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
	                entity.setContentEncoding("UTF-8");
	                entity.setContentType("application/x-www-form-urlencoded");
	                method.setEntity(entity);
	            }
	            HttpResponse result = httpClient.execute(method);
	            url = URLDecoder.decode(url, "UTF-8");

	            //请求发送成功，并得到响应
	            if (result.getStatusLine().getStatusCode() == 200) {
	                //标识状态为成功
	            	resultMap.put("status", StatusType.SUCCESS);
	                try {
	                	InputStream is = result.getEntity().getContent();
	                	String resultMeesage = convertStreamToString(is);
	                    logger.info(resultMeesage);
	                    resultMap.put("resultMeesage", resultMeesage);
	                } catch (Exception e) {
	                	logger.error("buyersys Tong createOrder post请求提交失败:" + url, e);
	                	resultMap.put("resultMeesage", "error");
	                }
	                
	            }else{
	            	InputStream is = result.getEntity().getContent();
	            	String resultMeesage = convertStreamToString(is);
	            	
	            	resultMap.put("status", StatusType.FAILURE);
	            	resultMap.put("resultMeesage", resultMeesage);
	            	logger.error("buyersys Tong createOrder 下单失败,result:"+resultMeesage);
	            	logger.error("buyersys Tong createOrder 入参:"+jsonParam);
	            }
	            
	        } catch (IOException e) {
	        	logger.info("buyersys Tong createOrder post请求提交失败:" + url, e);
	        	resultMap.put("status", StatusType.FAILURE);
	        	resultMap.put("resultMeesage", e.getMessage());
	        	logger.error("buyersys Tong createOrder 下单失败,result:"+e.getMessage());
	        	logger.error("buyersys Tong createOrder 入参:"+jsonParam);
	        }
	        return resultMap;
	    }
	    
	    
	    public String convertStreamToString(InputStream is) {      
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
	         StringBuilder sb = new StringBuilder();      
	     
	         String line = null;      
	        try {      
	            while ((line = reader.readLine()) != null) {      
	                 sb.append(line + "\n");      
	             }      
	         } catch (IOException e) {      
	             e.printStackTrace();      
	         } finally {      
	            try {      
	                 is.close();      
	             } catch (IOException e) {      
	                 e.printStackTrace();      
	             }      
	         }      
	     
	        return sb.toString();      
	     } 

	
}
