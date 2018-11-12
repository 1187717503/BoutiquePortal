package com.intramirror.order.api.service;

import java.math.BigDecimal;

/**
 * Created by caowei on 2018/11/12.
 */
public interface ITaxService {

    /**
     * 根据订单获取相关税率
     * @param orderLineNum
     * @return
     */
    BigDecimal calculateTax(String orderLineNum);
}
