package com.intramirror.order.core.impl;

import com.google.gson.Gson;
import com.intramirror.common.IKafkaService;
import com.intramirror.common.entity.FinanceEntity;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.Order;
import com.intramirror.order.api.service.KafkaUtilService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.OrderMapper;
import com.intramirror.order.core.utils.KafkaMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by caowei on 2018/6/5.
 */
@Service
public class KafkaUtilServiceImpl extends BaseDao implements KafkaUtilService {

    static final Logger logger = LoggerFactory.getLogger(KafkaUtilServiceImpl.class);

    @Autowired
    private IKafkaService kafkaService;

    @Autowired
    private KafkaMessageUtil kafkaMessageUtil;

    private OrderMapper orderMapper;

    public void init() {
        orderMapper = this.getSqlSession().getMapper(OrderMapper.class);
    }

    @Override
    public void saveOrderFinance(LogisticsProduct logisticsProduct) {
        logger.info("kafkaServer is:{}; kafkaTopic is:{}", kafkaMessageUtil.kafkaServer, kafkaMessageUtil.orderFinanceTopic);

        String sMsg ;

        Order o = orderMapper.getOrderByLpID(logisticsProduct.getLogistics_product_id());
        if (o == null) {
            logger.error("No order info founded!!");
            throw new RuntimeException("No order info founded!!");
        }

        if (kafkaService == null) {
            logger.error("KafkaProducerService could not be null!!");
            throw new RuntimeException("KafkaProducerService could not be null!!");
        }

        FinanceEntity fe = new FinanceEntity();
        fe.setDealerNo(o.getUserId());
        fe.setCurrency("CNY");
        fe.setChannelId(o.getChannelId());
        fe.setOrderNum(o.getOrderNum());
        fe.setTransNo(logisticsProduct.getOrder_line_num());

        fe.setTransCurrency("CNY");
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMM");
        Date date = new Date();
        fe.setCreateDate(dateFormater.format(date));
        fe.setsMsgType("SHIP");
        BigDecimal exchangeRate = o.getCurrentRate() == null ? new BigDecimal(7.98) : o.getCurrentRate();

        BigDecimal sum = logisticsProduct.getSale_price().add(logisticsProduct.getShipping_fee()).add(logisticsProduct.getTax_fee()).multiply(exchangeRate);
        BigDecimal total_rmb = logisticsProduct.getTotal_rmb() == null ? sum : logisticsProduct.getTotal_rmb();
        fe.setTransAmt(total_rmb.toString());
        BigDecimal imRMB = logisticsProduct.getIm_price().add(logisticsProduct.getShipping_fee()).add(logisticsProduct.getTax_fee()).multiply(exchangeRate);
        //佣金由欧元转RMB，四舍五入保留2位小数
        BigDecimal commission = total_rmb.subtract(imRMB).setScale(2, BigDecimal.ROUND_HALF_UP);
        fe.setCommission(commission.toString());
        sMsg = new Gson().toJson(fe);

        logger.info("The finance message to be sent to kafka is: " + sMsg);
        kafkaService.sendMsgToKafka(sMsg, kafkaMessageUtil.orderFinanceTopic, kafkaMessageUtil.kafkaServer);
    }


}
