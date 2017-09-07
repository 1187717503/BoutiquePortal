package pk.shoplus.model;



import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import pk.shoplus.DBConnector;
import pk.shoplus.common.Helper;
import pk.shoplus.enums.ApiParamterEnum;
import pk.shoplus.enums.CloudStoreEnum;
import pk.shoplus.model.OrderManagement.OrderMager;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.ApiEndpointService;
import pk.shoplus.service.ApiParameterService;
import pk.shoplus.service.LogisticsProductService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
/**
 * CloudStore 获取订单MQ消息队列  转换成对应json格式  调用http请求下单
 * @author wzh
 *
 */
public class OrderCloudStoreMapping implements IMapping {

    private final Logger LOGGER = Logger.getLogger(OrderCloudStoreMapping.class);


    /**
     * 调用接口 CloudStore 下单
     * @param mqData
     * @return
     */
    @Override
	public Map<String, Object> handleMappingAndExecute(String mqData,String queueNameEnum){
        Map<String, Object> dataMap = new HashMap<String, Object>();
    	dataMap.put("status", StatusType.FAILURE);
    	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Connection conn = null;
		try {
			conn = DBConnector.sql2o.beginTransaction();
	        ApiEndpointService apiEndpointService = new ApiEndpointService(conn);
	        ApiParameterService apiParameterService = new ApiParameterService(conn);
	        LogisticsProductService logisticsProductService = new LogisticsProductService(conn);
	        
	        //获取对应的请求参数
	        List<Map<String, Object>> apiEndpointList = apiEndpointService.getapiEndpointInfoByCondition("cloudstore", "CloudStoreCreateOrder");
	        if(apiEndpointList != null && apiEndpointList.size() > 0){
	        	Map<String, Object>  apiEndpointMap = apiEndpointList.get(0);
	  
				// get api_parameter
				List<Map<String,Object>> apiParamMap =  apiParameterService.getapiParameterByCondition(apiEndpointMap.get("api_end_point_id").toString());

			    //获取 token merchantId
				Map<String,Object> params = new HashMap<>();
				for(Map map : apiParamMap) {
					String param_key = map.get("param_key").toString();
					String param_value = map.get("param_value").toString();

					if(ApiParamterEnum.MERCHANT_ID.getCode().equalsIgnoreCase(param_key)) {
						params.put(ApiParamterEnum.MERCHANT_ID.getCode(),param_value);
					} else if(ApiParamterEnum.TOKEN.getCode().equalsIgnoreCase(param_key)){
						params.put(ApiParamterEnum.TOKEN.getCode(),param_value);
					}
				}
	        	
                
				/** 获取 队列中message 消息 **/
				OrderMager orderMager = new Gson().fromJson(mqData,OrderMager.class);
				
				
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
//				orderJSON.put("statusDate", sdf.format(Helper.getCurrentTimeToUTCWithDate(orderMager.getUpdatedTime())));
//				orderJSON.put("orderDate", sdf.format(Helper.getCurrentTimeToUTCWithDate(orderMager.getCreateTime())));
				orderJSON.put("statusDate", sdf.format(orderMager.getUpdatedTime()));
				orderJSON.put("orderDate", sdf.format(orderMager.getCreateTime()));
				orderJSON.put("orderTotalPrice", orderMager.getTotalPrice());
				
				

				//子订单参数
				orderItemJSON.put("sku",orderMager.getSku());
//				orderItemJSON.put("sku","391698W9132_1000-TU");
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
				System.out.println(orderJSON.toJSONString());
				
				String url = apiEndpointMap.get("url").toString();
				LOGGER.info(MessageFormat.format("开始调用CloudStore接口,推送订单 ,URL:{0},data:{1}",url,orderJSON.toString()));
				
				//调用http请求,创建订单，返回状态
				IGetPostRequest RequestService = new GetPostRequestService();
				String result = RequestService.requestMethod("POST", url, orderJSON.toJSONString());
				
				if(StringUtils.isNotBlank(result)){
				
					//转换成json格式，处理结果
					JSONObject resultJson = JSONObject.parseObject(result);
//					int result = this.httpPost(url, "order="+orderJSON.toString());
					if(resultJson != null && "ok".equals(resultJson.getString("status"))){
						//修改订单状态为COMFIRMED
						LogisticsProduct logisticsProduct = logisticsProductService.getLogisticsProductById(Long.parseLong(orderMager.getOrderId()));
						logisticsProductService.updateOrderLogisticsByOrderLogisticsId(logisticsProduct,2, false);
						
						//标识成功
						dataMap.put("status", StatusType.SUCCESS);
						
					}else if(resultJson != null && "ko".equals(resultJson.getString("status"))){
						dataMap.put("info", resultJson.getString("messages"));
					}else{
						dataMap.put("info", "返回结果:"+resultJson.toString());
					}
				
				}else{
					dataMap.put("info","RequestService.requestMethod 返回结果为空");
				}
	        }
	        
	        conn.commit();
		} catch (Exception e) {
			if(conn != null) {
				conn.rollback();
				conn.close();
			}
            e.printStackTrace();
            LOGGER.error(e.toString());
			dataMap.put("status", StatusType.FAILURE);
			dataMap.put("info", "系统异常: "+e.getMessage());
        }


        return dataMap;
    }
    
    
    public Map<String, Object> getUrl (Map<String, Object> apiEndpoint, Connection conn) throws Exception {
        ApiParameterService apiParameterService = new ApiParameterService(conn);
        StringBuffer urlBuffer = new StringBuffer();
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> apiParameterList = null;
        String apiEndPointId = "";

        if (null != apiEndpoint) {
            urlBuffer.append(apiEndpoint.get("url"));
            apiEndPointId = apiEndpoint.get("api_end_point_id").toString();
        }

        try {
            apiParameterList = apiParameterService.getapiParameterByCondition(apiEndPointId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url =  urlBuffer.toString();

        map.put("url", url);
        map.put("storeCode", apiEndpoint.get("store_code"));

        return map;
    }
    
    
    /**
     * post请求
     * @param url         url地址
     * @param jsonParam     参数
     * @return
     */
    public int httpPost(String url,String jsonParam){
    	
//    	CloseableHttpClient httpclient = HttpClients.createDefault();
//    	HttpPost httpPost = new HttpPost(url);
//	    //拼接参数
//	    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
//	    nvps.add(new BasicNameValuePair("username", "vip"));
//	    nvps.add(new BasicNameValuePair("password", "secret"));
//	    httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//	    CloseableHttpResponse response2 = httpclient.execute(httpPost);
//
//	    try {
//	        System.out.println(response2.getStatusLine());
//	        HttpEntity entity2 = response2.getEntity();
//	        // do something useful with the response body
//	        // and ensure it is fully consumed
//	        //消耗掉response
//	        EntityUtils.consume(entity2);
//	    } finally {
//	        response2.close();
//	    }
    	
    	

//        DefaultHttpClient httpClient = new DefaultHttpClient();
//        HttpPost method = new HttpPost(url);
//        int resultStatus = StatusType.FAILURE; //默认失败
//        
//        //post请求返回结果
//        JSONObject jsonResult = null;
//        //HttpGet get = new HttpGet("http://nugnes.edstema.it/api/v3.1/orders?limit=10&max=100&offset=1&storeCode=X3ZMV");
//        
//        try {
//        	
//            if (null != jsonParam) {
//                //解决中文乱码问题
//                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
//                entity.setContentEncoding("UTF-8");
//                entity.setContentType("application/x-www-form-urlencoded");
//                method.setEntity(entity);
//            }
//            
//            HttpResponse result = httpClient.execute(method);
//            url = URLDecoder.decode(url, "UTF-8");
//            
//            
//            //请求发送成功，并得到响应
//            if (result.getStatusLine().getStatusCode() == 200) {
//                //标识状态为成功
//                resultStatus = StatusType.SUCCESS;
//                String str = "";
//                
//                try {
//                    //读取服务器返回过来的json字符串数据
//                    str = EntityUtils.toString(result.getEntity());
//                    //把json字符串转换成json对象
//                    jsonResult = JSONObject.parseObject(str);
//                    System.out.println(jsonResult);
//                    
//                } catch (Exception e) {
//                	LOGGER.error("post请求提交失败:" + url, e);
//                }
//                
//            }else{
//            	LOGGER.error("下单失败,result:"+new Gson().toJson(result.getEntity()));
//            	LOGGER.error("入参:"+jsonParam);
//            }
//            
//        } catch (IOException e) {
//        	LOGGER.info("post请求提交失败:" + url, e);
//        }
        return 0;
    }


}
