package pk.shoplus.model;



import java.io.IOException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import pk.shoplus.DBConnector;
import pk.shoplus.common.Helper;
import pk.shoplus.model.OrderManagement.OrderMager;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.ApiEndpointService;
import pk.shoplus.service.ApiParameterService;
import pk.shoplus.service.mapping.api.IMapping;
/**
 * eds 获取订单MQ消息队列  转换成对应json格式  调用http请求下单
 * @author wzh
 *
 */
public class OrderEDSMapping implements IMapping {

    private final Logger LOGGER = Logger.getLogger(OrderEDSMapping.class);


    /**
     * 调用eds接口  下单
     * @param mqData
     * @return
     */
    @Override
	public Map<String, Object> handleMappingAndExecute(String mqData){
        Map<String, Object> dataMap = new HashMap<String, Object>();
    	dataMap.put("status", StatusType.FAILURE);
    	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Connection conn = null;
		try {
			conn = DBConnector.sql2o.beginTransaction();
	        ApiEndpointService apiEndpointService = new ApiEndpointService(conn);
	        //OrderManagement orderManagement = new OrderManagement();
	        
	        //获取对应的请求参数
	        List<Map<String, Object>> apiEndpointList = apiEndpointService.getapiEndpointInfoByCondition("eds", "placeOrder");
	        if(apiEndpointList != null && apiEndpointList.size() > 0){
	        	
	        	Map<String, Object>  apiEndpointMap = apiEndpointList.get(0);
                
				/** 获取 队列中message 消息 **/
				//MessageInfo message = new MessageInfo();
				OrderMager orderMager = new Gson().fromJson(mqData,OrderMager.class);
				
//				//模拟数据
//				orderMager = orderManagement.new OrderMager();
//				orderMager.setSize("M");
//				orderMager.setOrderNumber("2017051222600124100");
//				orderMager.setProductCode("5731f20f2b33300afbc3de50");
//				orderMager.setItemsCount("1");
//				orderMager.setPrice(new BigDecimal("375"));
				
				
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
				System.out.println(orderJSON.toJSONString());
				
				String url = apiEndpointMap.get("url").toString();
				url+="?storeCode="+apiEndpointMap.get("store_code").toString();
				LOGGER.info(MessageFormat.format("开始调用接口,推送订单 ,URL:{0},data:{1}",url,orderJSON.toString()));
				
				//调用http请求,创建订单，返回状态
				int result = this.httpPost(url, "order="+orderJSON.toString());
				if(result == StatusType.SUCCESS){
					//标识成功
					dataMap.put("status", StatusType.SUCCESS);
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

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);
        int resultStatus = StatusType.FAILURE; //默认失败
        
        //post请求返回结果
        JSONObject jsonResult = null;
        //HttpGet get = new HttpGet("http://nugnes.edstema.it/api/v3.1/orders?limit=10&max=100&offset=1&storeCode=X3ZMV");
        
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
                resultStatus = StatusType.SUCCESS;
                String str = "";
                
                try {
                    //读取服务器返回过来的json字符串数据
                    str = EntityUtils.toString(result.getEntity());
                    //把json字符串转换成json对象
                    jsonResult = JSONObject.parseObject(str);
                    System.out.println(jsonResult);
                    
                } catch (Exception e) {
                	LOGGER.error("post请求提交失败:" + url, e);
                }
                
            }else{
            	LOGGER.error("下单失败,result:"+new Gson().toJson(result.getEntity()));
            	LOGGER.error("入参:"+jsonParam);
            }
            
        } catch (IOException e) {
        	LOGGER.info("post请求提交失败:" + url, e);
        }
        return resultStatus;
    }


}
