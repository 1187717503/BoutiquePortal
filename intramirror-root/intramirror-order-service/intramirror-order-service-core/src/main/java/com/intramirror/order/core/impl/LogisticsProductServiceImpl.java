package com.intramirror.order.core.impl;

import com.google.gson.Gson;
import com.intramirror.common.Helper;
import com.intramirror.jpush.JPushService;
import com.intramirror.jpush.UnSupportActionTypeException;
import com.intramirror.jpush.entity.OrderStatusUpdateEntity;
import com.intramirror.jpush.enums.EmActionType;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.dto.OrderStatusChangeMsgDTO;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticsProductMapper;

import java.util.*;

import com.intramirror.order.core.utils.KafkaMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogisticsProductServiceImpl extends BaseDao implements ILogisticsProductService {

    @Autowired
    JPushService jpushService;

    private static Logger logger = LoggerFactory.getLogger(LogisticsProductServiceImpl.class);

    private LogisticsProductMapper logisticsProductMapper;

    public void init() {
        logisticsProductMapper = this.getSqlSession().getMapper(LogisticsProductMapper.class);
    }

    /**
     * 根据logistics_product_id 修改相关信息
     * @param logistics_product_id
     * @return
     */
    public int updateOrderLogisticsStatusById(Long logistics_product_id, int status) {

        //根据id获取对象信息
        LogisticsProduct logisticsProduct = this.selectById(logistics_product_id);

        //修改状态
        logisticsProduct.setStatus(status);

        // JPush 2017-12-28
        // 变更为picking状态不需要push
        if (status != OrderStatusType.PICKING){
            sendJPushMsg(logisticsProduct);
            logger.info("JPush 已经塞入线程池处理");
        }

        // 发送订单状态变化消息
        sendOrderStatusChangeBroadcastMsg(logisticsProduct);

        if (status == OrderStatusType.ORDERED) {
            //如果修改状态为shipped修改shippedat
            logisticsProduct.setShipped_at(Helper.getCurrentUTCTime());
        }else if(status == OrderStatusType.PICKING){
            //记录picking时间
            logisticsProduct.setPicking_at(Helper.getCurrentUTCTime());
        }
        return logisticsProductMapper.updateByLogisticsProduct(logisticsProduct);

    }

    private void sendOrderStatusChangeBroadcastMsg(LogisticsProduct logisticsProduct) {
        if(logisticsProduct == null){
            return;
        }

        Gson gson = new Gson();

        OrderStatusChangeMsgDTO orderStatusChangeMsgDTO = new OrderStatusChangeMsgDTO();
        orderStatusChangeMsgDTO.setLogisticsProductId(logisticsProduct.getLogistics_product_id());
        orderStatusChangeMsgDTO.setOrderLineNum(logisticsProduct.getOrder_line_num());
        orderStatusChangeMsgDTO.setStatus(logisticsProduct.getStatus());
        orderStatusChangeMsgDTO.setTriggerTime(new Date());

        String msg = gson.toJson(orderStatusChangeMsgDTO);
        KafkaMessageUtil.sendMsgToOrderChangeKafka(msg);
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

    /**
     * 根据condition map 来获取 OrderLogistics list
     * @param conditionMap
     * @return
     * @throws Exception
     */
    public List<LogisticsProduct> getLogisticsProductListByCondition(Map<String, Object> conditionMap) {
        return logisticsProductMapper.getLogisticsProductListByCondition(conditionMap);
    }

    @Override
    public int updateByLogisticsProduct(LogisticsProduct logisticsProduct) {
        sendJPushMsg(logisticsProduct);
        return logisticsProductMapper.updateByLogisticsProduct(logisticsProduct);
    }

    //JPush --2017-12-27
    private void sendJPushMsg(LogisticsProduct logisticsProduct) {
        logger.info("JPush通知 开始");
        if (logisticsProduct.getLogistics_product_id() == null || logisticsProduct.getStatus() == null) {
            logger.info("JPush Failed：LP入参为空");
            return;
        }

        //防止更改的不是status，先查一下老的status
        Map<String, Object> mapCond = new HashMap<>();
        mapCond.put("logistics_product_id", logisticsProduct.getLogistics_product_id());
        List<LogisticsProduct> list = logisticsProductMapper.selectByCondition(mapCond);
        if (list.size() != 1) {
            logger.info("JPush Failed：LP主键查到多条记录");
            return;
        }

        //根据主键查询，只有一条才正常
        LogisticsProduct oldLp = list.get(0);
        if (oldLp.getStatus() == logisticsProduct.getStatus()) {
            logger.info("JPush Failed： Status无变化");
            return;
        }

        List<OrderStatusUpdateEntity> listOsuEntity = new ArrayList<OrderStatusUpdateEntity>();
        OrderStatusUpdateEntity osu = new OrderStatusUpdateEntity();

        switch (logisticsProduct.getStatus()) {
        case OrderStatusType.COMFIRMED:
            osu.setEmAction(EmActionType.COMFIRMED);
            break;
        case OrderStatusType.ORDERED:
            osu.setEmAction(EmActionType.ORDERED);
            break;
        case OrderStatusType.PAYED:
            osu.setEmAction(EmActionType.PAYED);
            break;
        default:
            osu.setEmAction(null);
            break;
        }

        if (osu.getEmAction() == null) {
            return;
        }

        osu.setLogisticsProductId(logisticsProduct.getLogistics_product_id());
        listOsuEntity.add(osu);
        try {
            logger.info("JPush通知 订单的LP ID: " + logisticsProduct.getLogistics_product_id());
            jpushService.sendOrderNotification(listOsuEntity);
        } catch (UnSupportActionTypeException e) {
            logger.info("JPush Failed： sendOrderNotification异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<LogisticsProduct> selectByCondition(Map<String, Object> conditionMap) {

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
    public Map<String, Object> getOrderInfoByVendorId(Map<String, Object> conditionMap) {
        return logisticsProductMapper.getOrderInfoByVendorId(conditionMap);
    }

    @Override
    public int invalidOrderById(Long logisticsProductId) {
        return logisticsProductMapper.invalidOrderById(logisticsProductId);
    }

}
