package com.intramirror.order.core.impl;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.common.Helper;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticsProductMapper;

@Service
public class LogisticsProductServiceImpl extends BaseDao implements ILogisticsProductService {

    private static Logger logger = LoggerFactory.getLogger(LogisticsProductServiceImpl.class);

    private LogisticsProductMapper logisticsProductMapper;

    public void init() {
        logisticsProductMapper = this.getSqlSession().getMapper(LogisticsProductMapper.class);
    }


    /**
     * 根据logistics_product_id 修改相关信息
     *
     * @param logistics_product_id
     * @return
     */
    public int updateOrderLogisticsStatusById(Long logistics_product_id, int status) {

        //根据id获取对象信息
        LogisticsProduct logisticsProduct = this.selectById(logistics_product_id);

        //修改状态
        logisticsProduct.setStatus(status);
        //如果修改状态为shipped修改shippedat
        if (status == 3) {
            logisticsProduct.setShipped_at(Helper.getCurrentUTCTime());
        }
        return logisticsProductMapper.updateByLogisticsProduct(logisticsProduct);


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
     *
     * @param logistics_product_id
     * @return
     */
    public LogisticsProduct selectById(Long logistics_product_id) {
        return logisticsProductMapper.selectById(logistics_product_id);
    }

    /**
     * 根据condition map 来获取 OrderLogistics list
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    public List<LogisticsProduct> getLogisticsProductListByCondition(Map<String, Object> conditionMap) {
        return logisticsProductMapper.getLogisticsProductListByCondition(conditionMap);
    }


    @Override
    public int updateByLogisticsProduct(LogisticsProduct logisticsProduct) {
        return logisticsProductMapper.updateByLogisticsProduct(logisticsProduct);
    }


    @Override
    public List<LogisticsProduct> selectByCondition(
            Map<String, Object> conditionMap) {

        return logisticsProductMapper.selectByCondition(conditionMap);
    }


    @Override
    public int updateByContainerId(Map<String, Object> conditionMap) {

        return logisticsProductMapper.updateByContainerId(conditionMap);
    }


    @Override
    public Map<String, Object> selectLogisProShipmentById(Long logistics_product_id) {
        List<Map<String, Object>> map = logisticsProductMapper.selectLogisProShipmentById(logistics_product_id);
        if (map != null && map.size() > 0) {
            return map.get(0);
        }
        return null;
    }


    @Override
    public int updateContainerById(Long order_logistics_id) {
        return logisticsProductMapper.updateContainerById(order_logistics_id);
    }

    @Override
    public LogisticsProduct createLogisticsProduct(LogisticsProduct logisticsProduct) {
        logisticsProductMapper.createLogisticsProduct(logisticsProduct);
        return logisticsProductMapper.selectById(logisticsProduct.getLogistics_product_id());
    }



	@Override
	public Map<String, Object> getOrderInfoByVendorId(
			Map<String, Object> conditionMap) {
		return logisticsProductMapper.getOrderInfoByVendorId(conditionMap);
	}

    @Override
    public int invalidOrderById(Long logisticsProductId) {
        return  logisticsProductMapper.invalidOrderById(logisticsProductId);
    }


}
