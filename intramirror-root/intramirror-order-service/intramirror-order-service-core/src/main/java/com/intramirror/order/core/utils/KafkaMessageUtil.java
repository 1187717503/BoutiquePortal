package com.intramirror.order.core.utils;

import com.intramirror.common.IKafkaService;
import org.apache.log4j.Logger;

/**
 * Created by zhongyu on 2018/4/10.
 */
public class KafkaMessageUtil {

    public static String kafkaServer;
    public static String orderStatusChangeTopic;

    private final static Logger LOGGER = Logger.getLogger(KafkaMessageUtil.class);

    public static void sendMsgToKafka(String sMsg, String kafkaTopic, String kafkaServer) {
        try {
            IKafkaService kafkaService = Facility.getInstance().getKafkaService();
            kafkaService.sendMsgToKafka(sMsg, kafkaTopic, kafkaServer);
        }catch (Exception e){
            LOGGER.error("message send error, kafkaTopic=" + kafkaTopic + ", msg=" + sMsg );
        }

    }

    public static void sendMsgToOrderChangeKafka(String msg) {
        sendMsgToKafka(msg, orderStatusChangeTopic, kafkaServer);
    }
}
