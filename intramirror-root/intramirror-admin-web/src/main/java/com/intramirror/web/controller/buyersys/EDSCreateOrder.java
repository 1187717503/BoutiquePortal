package com.intramirror.web.controller.buyersys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import pk.shoplus.model.OrderRefund;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.Helper;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.common.ConverMapToOrderMager;
import com.intramirror.order.api.model.OrderMager;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderExceptionService;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:wzh
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class EDSCreateOrder extends BuyerSystemCall {
    private static Logger logger = Logger.getLogger(EDSCreateOrder.class);

	@Autowired
	IOrderExceptionService orderExceptionServiceImpl;
	
	@Autowired
	ILogisticsProductService logisticsProductServiceImpl;
	

    @Override
    public String createOrder(Long vendorId, Long logisticsProductId) throws Exception {
        logger.info("buyersys eds createOrder EDS买手店: "+vendorId +" 通过EDS系统创建订单: " +logisticsProductId);

        String result = "SUCCESS";
        try {
        	//读取配置文件中接口url
        	Properties props = new Properties();
        	InputStream in = EDSCreateOrder.class.getResourceAsStream("/orderapi-cofig.properties");
        	props.load(in);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("vendor_id", vendorId);
        	conditionMap.put("logistics_product_id", logisticsProductId);
        	Map<String, Object> orderInfoMap= logisticsProductServiceImpl.getOrderInfoByVendorId(conditionMap);
 

        	//获取需要处理的订单
            logger.info(MessageFormat.format("buyersys eds createOrder 获取需要处理的订单:{0}", new Gson().toJson(orderInfoMap)));
            if (orderInfoMap == null || orderInfoMap.size() == 0){
            	logger.info("buyersys eds createOrder 获取到的订单信息为空 ");
            	return "ERROR";
            }
            
            //处理数据
            OrderMager orderMager =  ConverMapToOrderMager.convertMapToBean(orderInfoMap);
            JSONObject orderJSON=new JSONObject();
        	JSONObject orderItemJSON=new JSONObject();
			JSONArray orderItemsArray = new JSONArray();
			
			//订单参数
			orderJSON.put("items_count", "1");
			orderJSON.put("order_number",orderMager.getOrderNumber());
			//UTC时间
			orderJSON.put("date",sdf.format(Helper.getCurrentTimeToUTCWithDate()));

			//子订单参数
			orderItemJSON.put("product",orderMager.getProductCode());
			// logistics_product.amount
			orderItemJSON.put("quantity", orderMager.getItemsCount());
			orderItemJSON.put("size", orderMager.getSize());
			orderItemJSON.put("purchase_price", orderMager.getPrice());
			
			//拼接json
			orderItemsArray.add(orderItemJSON);
			orderJSON.put("items", orderItemsArray);
			
			String url = props.getProperty("ensUrl");
			String store_code = props.getProperty("store_code");
			url+="?storeCode="+store_code;
			logger.info(MessageFormat.format("buyersys eds createOrder 开始调用接口,推送订单 ,URL:{0},data:{1}",url,orderJSON.toString()));
        
			Map<String, Object> resultApi = this.httpPost(url, "order="+orderJSON.toString());
			
			String json = resultApi.get("resultMeesage").toString();
			if (StringUtils.isNotBlank(json)){
    			JSONObject jsonOjbect = JSONObject.parseObject(json);
    			String getResult = jsonOjbect.get("result").toString();
    			logger.info("buyersys eds createOrder result:"+getResult);
    			if ("Error ! Quantity Not Availble".equals(getResult)){
    				//没有库存执行退款
    				OrderRefund orderRefund = new OrderRefund();
    				if (orderRefund != null){
    					
    					//调用退款
    					logger.info(MessageFormat.format("buyersys eds createOrder 推送订单失败 ,返回的结果:{0}",getResult));
    					Map<String, Object> refundMap = orderRefund.orderRefund(logisticsProductId+"");
    					logger.info(MessageFormat.format("buyersys Tong createOrder 推送订单失败,调用退款 接口,返回的结果:{0}",new Gson().toJson(refundMap)));
    					
    					//如果退款失败，记录失败信息
    					Integer refundStatus = Integer.parseInt(refundMap.get("status").toString());
        				if(refundStatus != null && refundStatus.equals(StatusType.FAILURE)){
        					logger.info(MessageFormat.format("buyersys eds createOrder 推送订单失败,返回结果:{0},调用退款接口失败，返回错误信息:{1}",new Gson().toJson(getResult),new Gson().toJson(refundMap)));
        				}
    				}
    				result = "ERROR";
    			}
    			
    			//如果返回Fail! Invalid SKU 保存异常信息
    			if ("Error !! Barcode Not Exist.".equals(getResult)){
    				
    				Map<String, Object> exception = new HashMap<String, Object>();
    				exception.put("logistics_product_id", logisticsProductId);
    				exception.put("comments", getResult);
    				//保存错误信息
    				exception.put("order_exception_type_id", Long.parseLong("3"));
    				exception.put("status", 1);
    				exception.put("created_at", sdf.format(new Date()));
    				//系统处理用户为0
    				exception.put("created_by_user_id", Long.parseLong("0"));
    				
    				logger.info("buyersys eds createOrder orderExceptionServiceImpl 记录错误信息  入参:" + new Gson().toJson(exception));
    				int num = orderExceptionServiceImpl.saveOrderComments(exception);
    				logger.info("buyersys eds createOrder orderExceptionServiceImpl 记录错误信息  result: "+num);
    				
    				result = "ERROR";
    			}
    		}
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("buyersys eds createOrder 买手店: "+vendorId +" 通过EDS系统创建订单: " +logisticsProductId +" 异常:" + e.toString());
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
                	logger.error("buyersys eds createOrder post请求提交失败:" + url, e);
                }
                
            }else{
            	InputStream is = result.getEntity().getContent();
            	String resultMeesage = convertStreamToString(is);
            	
            	resultMap.put("status", StatusType.FAILURE);
            	resultMap.put("resultMeesage", resultMeesage);
            	logger.error("buyersys eds createOrder 下单失败,result:"+resultMeesage);
            	logger.error("buyersys eds createOrder 入参:"+jsonParam);
            }
            
        } catch (IOException e) {
        	logger.info("buyersys eds createOrder post请求提交失败:" + url, e);
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
