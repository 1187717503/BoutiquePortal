/**
 *
 */
package com.intramirror.order.core.impl;

import com.intramirror.order.api.model.Logistics;
import com.intramirror.order.api.model.PaymentOffline;
import com.intramirror.order.api.service.LogisticsService;
import com.intramirror.order.api.service.PaymentOfflineService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticsMapper;
import com.intramirror.order.core.mapper.PaymentOfflineMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author yuan
 */
@Service
public class PaymentOfflineServiceImpl extends BaseDao implements PaymentOfflineService {

    private static Logger logger = LoggerFactory.getLogger(PaymentOfflineServiceImpl.class);


    private PaymentOfflineMapper paymentOfflineMapper;


    @Override
    public void init() {
        paymentOfflineMapper = this.getSqlSession().getMapper(PaymentOfflineMapper.class);
    }


    @Override
    public PaymentOffline createPaymentOffline(PaymentOffline paymentOffline) {
        paymentOfflineMapper.insert(paymentOffline);
        return paymentOfflineMapper.selectByPrimaryKey(paymentOffline.getPaymentOfflineId());
    }
}
