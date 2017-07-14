package com.intramirror.web.service;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticsProductMapper;
import com.intramirror.product.api.service.ISkuStoreService;

@Service
public class LogisticsProductService{
	
    private static Logger logger = LoggerFactory.getLogger(LogisticsProductService.class);

	@Autowired
	private ILogisticsProductService logisticsProductService;

	@Autowired
	private ISkuStoreService skuStoreService;
	
	
	/**
	 * 根据logistics_product_id 修改相关信息
	 * @param LogisticsProduct
	 * @return
	 */
	public Map<String, Object> updateOrderLogisticsStatusById(Long logistics_product_id,int status) {
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
			if(lastStatus != oldLogisticsProduct.getStatus()){
				resultMap.put("info","The status check failed, please modify the status in order of order");
				return resultMap;
			}
			
			
			oldLogisticsProduct.setStatus(status);
			//校验通过，修改状态
			logisticsProductService.updateOrderLogisticsStatusById(logistics_product_id,status);
			
			//修改库存相关信息
			Long skuId = skuStoreService.selectSkuIdByShopProductSkuId(oldLogisticsProduct.getShop_product_sku_id());
			skuStoreService.updateBySkuId(status, skuId);
			
            resultMap.put("status",StatusType.SUCCESS);
        }
		
		return resultMap;
	}

	
}
