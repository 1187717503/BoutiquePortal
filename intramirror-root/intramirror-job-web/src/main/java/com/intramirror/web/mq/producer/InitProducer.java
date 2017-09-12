package com.intramirror.web.mq.producer;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.google.gson.Gson;
import com.intramirror.web.mq.vo.MqProperties;
import com.intramirror.web.mq.vo.Region;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pk.shoplus.util.ExceptionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dingyifan on 2017/9/11.
 */
@Component
@Scope(scopeName = "singleton")
public class InitProducer implements InitializingBean{

    private static Logger logger = Logger.getLogger(InitProducer.class);

    private Map<String,OrderProducer> producerMap;

    @Autowired
    private MqProperties mqProperties;

    // 初始化生产者
    public void init(){
        producerMap = new HashMap<>();
        try {
            logger.info("InitProducerInit,start,mqProperties:"+new Gson().toJson(mqProperties));

            Properties properties = new Properties();
            properties.put(PropertyKeyConst.AccessKey, mqProperties.getAccessKey());
            properties.put(PropertyKeyConst.SecretKey, mqProperties.getSecretKey());
            properties.put(PropertyKeyConst.ONSAddr, mqProperties.getONSAddr());

            List<Region> regions = mqProperties.getRegions();
            for(Region region : regions) {
                properties.put(PropertyKeyConst.ProducerId, region.getProducerId());

                logger.info("InitProducerInit,createProducer,properties:"+new Gson().toJson(properties));
                OrderProducer producer = ONSFactory.createOrderProducer(properties);
                producer.start();
                logger.info("InitProducerInit,createProducer,producer:"+producer);

                producerMap.put(region.getProducerId(),producer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("InitProducerInit,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("InitProducerInit,end,producerMap:"+producerMap.size());
    }

    public Map<String, OrderProducer> getProducerMap() {
        return producerMap;
    }

    public void setProducerMap(Map<String, OrderProducer> producerMap) {
        this.producerMap = producerMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        this.init();
    }
}
