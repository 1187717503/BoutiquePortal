/**
 *
 */
package com.intramirror.order.core.impl;

import com.intramirror.order.api.model.Logistics;
import com.intramirror.order.api.model.OrderLogistics;
import com.intramirror.order.api.service.LogisticsService;
import com.intramirror.order.api.service.OrderLogisticsService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticsMapper;
import com.intramirror.order.core.mapper.OrderLogisticsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 订单装箱service
 *
 * @author yuan
 */
@Service
public class LogisticsServiceImpl extends BaseDao implements LogisticsService {

    private static Logger logger = LoggerFactory.getLogger(LogisticsServiceImpl.class);


    private LogisticsMapper logisticsMapper;


    @Override
    public void init() {
        logisticsMapper = this.getSqlSession().getMapper(LogisticsMapper.class);
    }

    @Override
    public Logistics createLogistics(Logistics logistics){
        int logisticsId = logisticsMapper.insert(logistics);
        return logisticsMapper.selectByPrimaryKey((long) logisticsId);
    }
}
