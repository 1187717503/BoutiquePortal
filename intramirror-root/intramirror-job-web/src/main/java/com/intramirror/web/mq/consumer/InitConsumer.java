package com.intramirror.web.mq.consumer;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.*;
import com.google.gson.Gson;
import com.intramirror.web.mq.MqName;
import com.intramirror.web.mq.MqProperties;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
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

    public void init() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, mqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, mqProperties.getSecretKey());

        List<MqName> mqNames = mqProperties.getMqNames();
        for(final MqName mqName : mqNames) {
            properties.put(PropertyKeyConst.ConsumerId, mqName.getConsumerId());

            OrderConsumer consumer = ONSFactory.createOrderedConsumer(properties);
            consumer.subscribe(
                    mqProperties.getTopic(),
                    "*",
                    new MessageOrderListener() {
                        @Override
                        public OrderAction consume(Message message, ConsumeOrderContext context) {
                            System.out.println(message);
                            System.out.println(message.getBody());
                            System.out.println(mqName+"------"+new String(message.getBody()));
                            System.out.println(mqName);
                            return OrderAction.Success;
                        }
                    });
            consumer.start();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }
}
