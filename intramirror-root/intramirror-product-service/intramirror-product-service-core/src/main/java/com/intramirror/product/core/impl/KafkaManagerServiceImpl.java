package com.intramirror.product.core.impl;

import com.intramirror.common.Constants;
import com.intramirror.common.IKafkaService;
import com.intramirror.product.api.service.IKafkaManagerService;
import com.intramirror.product.common.KafkaProperties;
import com.intramirror.utils.transform.JsonTransformUtil;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaManagerServiceImpl implements IKafkaManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaManagerServiceImpl.class);

    @Autowired
    KafkaProperties kafkaProperties;

    @Autowired
    IKafkaService kafkaService;

    @Override
    public void sendGroupChanged(Long productId) {
        String data = this.generateMsg(productId, Constants.changed_discount);
        kafkaService.sendMsgToKafka(data, kafkaProperties.getProductTopic(), kafkaProperties.getServerName());
        LOGGER.info("data:{}", data);
    }

    @Override
    public void sendSeasonChanged(Long productId) {
        String seasonData = this.generateMsg(productId, Constants.changed_season_code);
        kafkaService.sendMsgToKafka(seasonData, kafkaProperties.getProductTopic(), kafkaProperties.getServerName());
        LOGGER.info("data:{}", seasonData);

        String discountData = this.generateMsg(productId, Constants.changed_discount);
        kafkaService.sendMsgToKafka(discountData, kafkaProperties.getProductTopic(), kafkaProperties.getServerName());
        LOGGER.info("data:{}", discountData);
    }

    @Override
    public void sendPriceChanged(Long productId) {
        String data = this.generateMsg(productId, Constants.changed_discount);
        kafkaService.sendMsgToKafka(data, kafkaProperties.getProductTopic(), kafkaProperties.getServerName());
        LOGGER.info("data:{}", data);
    }

    private String generateMsg(Long productId, Integer type) {
        Map<String, Object> data = new HashMap<>();
        data.put("productId", productId);
        data.put("type", type);
        return JsonTransformUtil.toJson(data);
    }
}
