package com.intramirror.web.mq.consumer;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.*;
import com.google.gson.Gson;
import com.intramirror.web.mq.vo.MqProperties;
import com.intramirror.web.mq.vo.Region;
import com.intramirror.web.mq.vo.Tag;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * Created by dingyifan on 2017/9/11.
 */
@Component
@Scope(scopeName = "singleton")
public class InitConsumer implements InitializingBean {

    private static Logger logger = Logger.getLogger(InitConsumer.class);

    @Autowired
    private MqProperties mqProperties;

    // 初始化消费者
    public void init() {
        try {
            logger.info("InitProducerInit,start,mqProperties:"+new Gson().toJson(mqProperties));

            Properties properties = new Properties();
            properties.put(PropertyKeyConst.AccessKey, mqProperties.getAccessKey());
            properties.put(PropertyKeyConst.SecretKey, mqProperties.getSecretKey());

            List<Region> regions = mqProperties.getRegions();
            for(final Region region : regions) {
                properties.put(PropertyKeyConst.ConsumerId, region.getConsumerId());

                logger.info("InitProducerInit,createOrderedConsumer,properties:"+new Gson().toJson(properties));
                OrderConsumer consumer = ONSFactory.createOrderedConsumer(properties);
                logger.info("InitProducerInit,createOrderedConsumer,consumer:"+consumer);
                List<Tag> tags = region.getTags();
                for(Tag tag : tags) {
                    final String tagCode = tag.getCode();
                    String topic = region.getTopic();
                    logger.info("InitProducerInit,startSubscribe,region:"+new Gson().toJson(region)+",tag:"+new Gson().toJson(tag));
                    consumer.subscribe(
                            topic,
                            tagCode,
                            new MessageOrderListener() {
                                @Override
                                public OrderAction consume(Message message, ConsumeOrderContext context) {
                                    logger.info("InitProducerConsume,region:"+new Gson().toJson(region));
                                    logger.info("InitProducerConsume,message:"+message);
                                    logger.info("InitProducerConsume,context:"+message);
                                    System.out.println(new String(message.getBody()));
                                    logger.info("InitProducerConsume,tag:"+tagCode);
                                    return OrderAction.Success;
                                }
                            });
                    consumer.start();
                    logger.info("InitProducerInit,endSubscribe,region:"+new Gson().toJson(region)+",tag:"+new Gson().toJson(tag));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        this.init();
    }
}
