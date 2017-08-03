package pk.shoplus.model;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;


/**
 * CloudStore 获取订单状态MQ消息队列  获取更新订单状态
 * @author wzh
 *
 */
public class OrderCloudStoreUpdateStatusMapping implements IMapping{

    private final Logger LOGGER = Logger.getLogger(OrderCloudStoreUpdateStatusMapping.class);

    /**
     * CloudStore 根据orderLineNum 修改订单状态
     * @param mqData
     * @return
     */
    @Override
	public Map<String, Object> handleMappingAndExecute(String mqData){
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("status", StatusType.SUCCESS);
		
		try {
	        OrderUpdateStatusMapping orderUpdateStatus = new OrderUpdateStatusMapping();
                
			/** 获取 队列中message 消息 **/
			JSONObject orderJson =JSON.parseObject(mqData);
			
			//获取订单参数，更改订单状态
			if(orderJson != null){
				String orderLineNum = orderJson.getString("orderNumber");
				String status = orderJson.getString("status");
				LOGGER.info(MessageFormat.format("调用更新订单,入参  orderLineNum:{0},status:{1}", orderLineNum,status));
				
				//CloudStore 订单状态转换成本地订单状态
				LOGGER.info(MessageFormat.format("调用更新订单 orderUpdateStatus.updateOrderStatus:入参  orderLineNum:{0},status:{1}", orderLineNum,status));
				if(status != null && StringUtils.isNotBlank(orderLineNum)){
					return orderUpdateStatus.updateOrderStatus(orderLineNum, Integer.parseInt(status));
				}else{
					dataMap.put("info", "订单状态跟 orderNumber不能为空");
				}

			}

			

			
		} catch (Exception e) {
            dataMap.put("status", StatusType.FAILURE);
            dataMap.put("info", "系统异常: "+e.getMessage());
			LOGGER.error(e.toString());
            e.printStackTrace();
        }


        return dataMap;
    }
   
    

}
