package com.intramirror.web.controller.buyersys;

import org.apache.log4j.Logger;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class BuyerSystemCall {

    public static Logger logger = Logger.getLogger(BuyerSystemCall.class);

    /**
     * 调用买手店系统接口创建订单
     * @param vendorId
     * @param logisticsProductId
     * @return
     * @throws Exception
     */
    public abstract String createOrder (Long vendorId, Long logisticsProductId) throws Exception;

}
