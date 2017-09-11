/**
 *
 */
package com.intramirror.order.core.impl;

import com.intramirror.order.api.model.OrderLogistics;
import com.intramirror.order.api.model.ShippingProvider;
import com.intramirror.order.api.service.IShippingProviderService;
import com.intramirror.order.api.service.OrderLogisticsService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.OrderLogisticsMapper;
import com.intramirror.order.core.mapper.ShippingProviderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 订单装箱service
 *
 * @author yuan
 */
@Service
public class OrderLogisticsServiceImpl extends BaseDao implements OrderLogisticsService {

    private static Logger logger = LoggerFactory.getLogger(OrderLogisticsServiceImpl.class);


    private OrderLogisticsMapper orderLogisticsMapper;


    @Override
    public void init() {
        orderLogisticsMapper = this.getSqlSession().getMapper(OrderLogisticsMapper.class);
    }

    @Override
    public OrderLogistics createOrderLogistics(OrderLogistics orderLogistics) {
        int orderLogisticsId = orderLogisticsMapper.insert(orderLogistics);
        return orderLogisticsMapper.selectByPrimaryKey((long) orderLogisticsId);
    }
}
