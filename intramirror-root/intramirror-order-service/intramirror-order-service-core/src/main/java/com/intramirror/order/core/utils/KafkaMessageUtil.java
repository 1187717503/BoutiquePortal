package com.intramirror.order.core.utils;

import com.intramirror.common.IKafkaService;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhongyu on 2018/4/10.
 */
public class KafkaMessageUtil {

    public static String kafkaServer;
    public static String orderStatusChangeTopic;
    public static String orderFinanceTopic;

    private final static Logger LOGGER = Logger.getLogger(KafkaMessageUtil.class);

    public static void sendMsgToKafka(String sMsg, String kafkaTopic, String kafkaServer, IKafkaService kafkaService) {
        try {
            //IKafkaService kafkaService = Facility.getInstance().getKafkaService();
            kafkaService.sendMsgToKafka(sMsg, kafkaTopic, kafkaServer);
        }catch (Exception e){
            LOGGER.error("message send error, kafkaTopic=" + kafkaTopic + ", msg=" + sMsg );
        }

    }

    public static void sendMsgToOrderChangeKafka(String msg, IKafkaService kafkaService) {
        sendMsgToKafka(msg, orderStatusChangeTopic, kafkaServer, kafkaService);
    }

    public static String getOrderStatusChangeTopic() {
        return orderStatusChangeTopic;
    }

    public static void setOrderStatusChangeTopic(String orderStatusChangeTopic) {
        KafkaMessageUtil.orderStatusChangeTopic = orderStatusChangeTopic;
    }

    public static String getKafkaServer() {
        return kafkaServer;
    }

    public static void setKafkaServer(String kafkaServer) {
        KafkaMessageUtil.kafkaServer = kafkaServer;
    }

    public static String getOrderFinanceTopic() {
        return orderFinanceTopic;
    }

    public static void setOrderFinanceTopic(String orderFinanceTopic) {
        KafkaMessageUtil.orderFinanceTopic = orderFinanceTopic;
    }
}
