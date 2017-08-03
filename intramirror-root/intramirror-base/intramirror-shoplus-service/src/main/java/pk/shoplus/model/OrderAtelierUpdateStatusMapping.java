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
 * Atelier 更新订单状态
 * @author wzh
 *
 */
public class OrderAtelierUpdateStatusMapping implements IMapping{

    private final Logger LOGGER = Logger.getLogger(OrderAtelierUpdateStatusMapping.class);

    /**
     * Atelier 根据orderLineNum 更新订单状态
     * @param mqData
     * @return
     */
    @Override
	public Map<String, Object> handleMappingAndExecute(String mqData){
		LOGGER.info("Atelier mapping 更新订单状态入参:"+mqData);
        Map<String, Object> dataMap = new HashMap<String, Object>();
    	dataMap.put("status", StatusType.FAILURE);
        
		try {
	        OrderUpdateStatusMapping orderUpdateStatus = new OrderUpdateStatusMapping();
	        
			/** 获取 队列中message 消息 **/
			JSONObject orderJson =JSON.parseObject(mqData);
			
			//获取订单参数，更改订单状态
			if(orderJson != null){
				String orderLineNum = orderJson.getString("orderNumber");
				String status = orderJson.getString("status");
				String vendorId = orderJson.getString("vendorId");
				LOGGER.info(MessageFormat.format("调用更新订单,入参  orderLineNum:{0},status:{1}", orderLineNum,status));
				

				if(status != null && StringUtils.isNotBlank(orderLineNum)){
					
					//atelier 订单状态转换成本地订单状态
					LOGGER.info(MessageFormat.format("调用更新订单 orderUpdateStatus.updateOrderStatus:入参  orderLineNum:{0},status:{1}", orderLineNum,status));
					return orderUpdateStatus.updateOrderStatus(orderLineNum, Integer.parseInt(status));
					
				}else{
					dataMap.put("info", "订单状态跟 orderNumber不能为空");
				}

			}
			

		} catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
			dataMap.put("info", "系统异常: "+e.getMessage());
        }

        return dataMap;
    }
    
    

}
