package com.intramirror.user.core.apimq.impl;

import com.intramirror.user.api.model.OrderNotificationEmail;
import com.intramirror.user.api.service.vendor.OrderNotificationEmailService;
import com.intramirror.user.core.dao.BaseDao;
import com.intramirror.user.core.mapper.OrderNotificationEmailMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderNotificationEmailServiceImpl extends BaseDao implements OrderNotificationEmailService {

    private OrderNotificationEmailMapper orderNotificationEmailMapper;

    @Override
    public void init() {
        orderNotificationEmailMapper = this.getSqlSession().getMapper(OrderNotificationEmailMapper.class);
    }


    @Override
    public List<OrderNotificationEmail> selectByVendorId(Long vendorId) {
        return orderNotificationEmailMapper.selectByVendorId(vendorId);
    }

    @Override
    public void modify(OrderNotificationEmail orderNotificationEmail) {
        orderNotificationEmailMapper.updateByPrimaryKeySelective(orderNotificationEmail);
    }

    @Override
    public void create(OrderNotificationEmail orderNotificationEmail) {
        orderNotificationEmailMapper.insertSelective(orderNotificationEmail);
    }
}
