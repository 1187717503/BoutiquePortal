package com.intramirror.order.core.impl;

import com.intramirror.common.core.mapper.ViewOrderLinesMapper;
import com.intramirror.order.api.service.IViewOrderLinesService;
import com.intramirror.order.api.vo.ViewOrderLinesVO;
import com.intramirror.order.core.dao.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ViewOrderLines service
 *
 * @author yaods
 */
@Service
public class ViewOrderLinesServiceImpl extends BaseDao implements IViewOrderLinesService {

    private static Logger logger = LoggerFactory.getLogger(ViewOrderLinesServiceImpl.class);

    private ViewOrderLinesMapper viewOrderLinesMapper;

    @Override
    public void init() {
        viewOrderLinesMapper = this.getSqlSession().getMapper(ViewOrderLinesMapper.class);
    }

    @Override
    public List<ViewOrderLinesVO> getShipmentListByShipmentNo(String shipmentNo) {
        return viewOrderLinesMapper.getShipmentListByShipmentNo(shipmentNo);
    }

    @Override
    public List<ViewOrderLinesVO> getOrderListToDeclare(String shipmentNo) {
        return viewOrderLinesMapper.getOrderListToDeclare(shipmentNo);
    }
}
