package com.intramirror.order.api.service;

import com.intramirror.order.api.model.LogisticsProduct;

/**
 * Created by caowei on 2018/6/5.
 */
public interface KafkaUtilService {

    void saveOrderFinance(LogisticsProduct logisticsProduct);
}
