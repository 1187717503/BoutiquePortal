package com.intramirror.web.service;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.intramirror.order.api.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.ILogisticsProductService;

@Service
public class LogisticsProductService{
	
    private static Logger logger = LoggerFactory.getLogger(LogisticsProductService.class);

	@Autowired
	private ILogisticsProductService logisticsProductService;

	@Autowired
	//private ISkuStoreService skuStoreService;
	private IOrderService orderService;
	
	/**
	 * 根据logistics_product_id 修改相关信息
	 * @param logisticsProduct
	 * @return
	 */
	public Map<String, Object> updateOrderLogisticsStatusById(LogisticsProduct logisticsProduct,int status) {
		Long logistics_product_id = logisticsProduct.getLogistics_product_id();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("status",StatusType.FAILURE);
		
    	//根据id获取当前数据库旧的对象信息
        LogisticsProduct oldLogisticsProduct = logisticsProductService.selectById(logistics_product_id);
        
        // 修改状态
    	if (oldLogisticsProduct != null) {
    		logger.info(MessageFormat.format("当前订单状态:{0},需要修改后的订单状态:{1}",oldLogisticsProduct.getStatus(),status));
    		
	        //状态一致，无需修改
	        if(oldLogisticsProduct.getStatus() == status){
	        	resultMap.put("status",StatusType.SUCCESS);
	        	resultMap.put("info", "The order status is consistent, without modification");
	        	return resultMap;
	        }
	        
	        //获取当前状态的上一个状态，校验状态机
	        int lastStatus= OrderStatusType.getLastStatus(status);

			//校验是否按状态机流转,如果不是则提示错误
			Integer oldStatus = oldLogisticsProduct.getStatus();
			if(lastStatus != oldStatus){
				if(status == OrderStatusType.COMFIRMED
						&&(oldStatus==OrderStatusType.PENDING||oldStatus==OrderStatusType.PICKING)){
					//状态机正常流转，不处理
				}else {
					resultMap.put("info","The status check failed, please modify the status in order of order");
					return resultMap;
				}
			}
			
			//oldLogisticsProduct.setStatus(status);
			//校验通过，修改状态
			logisticsProductService.updateOrderLogisticsStatusById(logistics_product_id,status);
			//同时修改order表状态
			orderService.updateOrderByOrderLogisticsId(oldLogisticsProduct.getOrder_logistics_id(),status);
//			if(num <= 0){
//				resultMap.put("info","Status modification failed");
//				return resultMap;
//			}
            resultMap.put("status",StatusType.SUCCESS);
        }
		
		return resultMap;
	}

	public LogisticsProduct selectById(Long logisProductId) {
		return logisticsProductService.selectById(logisProductId);
	}

	
}