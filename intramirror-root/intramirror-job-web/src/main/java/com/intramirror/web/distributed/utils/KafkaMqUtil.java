package com.intramirror.web.distributed.utils;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2018/3/6.
 *
 * @author YouFeng.Zhu
 */
public final class KafkaMqUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaMqUtil.class);
    private static Properties properties = new Properties();
    static {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "106.15.229.248:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    }
    private static final String IMAGE_QUEUE = "image_url_data";
    private static final String RESULT_QUEUE = "result_data";

    private static Producer<String, String> producer = new KafkaProducer<>(properties);

    public static void KafkaMqUtil(String key, String value) {
        producer.send(new ProducerRecord<String, String>(IMAGE_QUEUE, key, value));
    }

    public static void sendResultMessage(String key, String value) {
        producer.send(new ProducerRecord<String, String>(RESULT_QUEUE, key, value));
    }

    public static void main(String argv[]) {
        new KafkaMqUtil();
        KafkaMqUtil.sendResultMessage("123", "77777777777");
    }

}
