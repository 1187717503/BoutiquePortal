package com.intramirror.web.controller.buyersys;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.intramirror.web.common.SpringContextUtils;


/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:wzh
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SystemCallFactory {
    private static Logger logger = Logger.getLogger(SystemCallFactory.class);
    private static Map<Long,String> nameMap = new HashMap<Long, String>();

    public SystemCallFactory () {
        nameMap.put(9L, "eDSCreateOrder");
        nameMap.put(16L, "TongCreateOrder");
        nameMap.put(21L, "eDSCreateOrder");
    }

    public static String createOrder (Long vendorId,Long logisticsProductId) {
    	logger.info("create order SystemCallFactory createOrder 入参 vendorId:"+vendorId+",logisticsProductId:"+logisticsProductId);
    	String result = "";
    	
    	//参数校验
    	if(vendorId == null || logisticsProductId == null || vendorId == 0 || logisticsProductId == 0){
    		result = "create order SystemCallFactory  参数校验失败   参数不能为null";
    		logger.info("create order SystemCallFactory  参数校验失败   参数不能为null");
    		return result;
    	}
    	

        String className = nameMap.get(vendorId).toString();
        logger.info("create order SystemCallFactory createOrder 获取到的实例名称   className:"+className);
        
        //通过spring应用上下文容器,根据名称获取bean
        BuyerSystemCall buyerSystemCall =(BuyerSystemCall) SpringContextUtils.getContext().getBean(className);
        
        //调用对应的实例执行下单操作
        try {
        	if(buyerSystemCall != null){
            	result = buyerSystemCall.createOrder(vendorId, logisticsProductId);
        	}else{
        		result = "获取对应的实例失败,bean 名称:"+className;
        		logger.info("create order SystemCallFactory 获取对应的实例失败,bean 名称:"+className);
        	}

		} catch (Exception e) {
			logger.error("create order SystemCallFactory error:"+e.getMessage());
			e.printStackTrace();
		}
        return result;
    }
}
