package com.intramirror.order.core.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticsProductMapper;

@Service
public class LogisticsProductServiceImpl extends BaseDao implements ILogisticsProductService{
	
    private static Logger logger = LoggerFactory.getLogger(LogisticsProductServiceImpl.class);

    private LogisticsProductMapper logisticsProductMapper;

    public void init() {
    	logisticsProductMapper = this.getSqlSession().getMapper(LogisticsProductMapper.class);
    }


    
	/**
	 * 根据logistics_product_id 修改相关信息
	 * @param LogisticsProduct
	 * @return
	 */
	public Long updateOrderLogisticsStatusById(Long logistics_product_id,int status) {
		
    	//根据id获取对象信息
        LogisticsProduct logisticsProduct = this.selectById(logistics_product_id);
			
		//修改状态
        logisticsProduct.setStatus(status);
		return logisticsProductMapper.updateById(logisticsProduct);
      
        
		

	}
	
//	/**
//	 * 根据logistics_product_id 修改相关信息
//	 * @param LogisticsProduct
//	 * @return
//	 */
//	public Map<String, Object> updateOrderLogisticsStatusById(Long logistics_product_id,int status) {
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		
//    	//根据id获取当前数据库旧的对象信息
//        LogisticsProduct oldLogisticsProduct = this.selectById(logistics_product_id);
//        
//        // 修改状态
//    	if (oldLogisticsProduct != null) {
//    		logger.info(MessageFormat.format("当前订单状态:{0},需要修改后的订单状态:{1}",oldLogisticsProduct.getStatus(),status));
//    		
//	        //状态一致，无需修改
//	        if(oldLogisticsProduct.getStatus() == status){
//	        	resultMap.put("status",StatusType.SUCCESS);
//	        	resultMap.put("info", "The order status is consistent, without modification");
//	        	return resultMap;
//	        }
//	        
//	        //获取当前状态的上一个状态，校验状态机
//	        int lastStatus= OrderStatusType.getLastStatus(status);
//	        
//			
//			//校验是否按状态机流转,如果不是则提示错误
//			if(lastStatus != oldLogisticsProduct.getStatus()){
//				resultMap.put("info","The status check failed, please modify the status in order of order");
//				return resultMap;
//			}
//			
//			
//			oldLogisticsProduct.setStatus(status);
//			//校验通过，修改状态
//			logisticsProductMapper.updateById(oldLogisticsProduct);
//            resultMap.put("status",StatusType.SUCCESS);
//        }
//		
//		return resultMap;
//	}

	

	/**
	 * 根据 logistics_product_id 查询详情
	 * @param logistics_product_id
	 * @return
	 */
	public LogisticsProduct selectById(Long logistics_product_id) {
		return logisticsProductMapper.selectById(logistics_product_id);
	}
}