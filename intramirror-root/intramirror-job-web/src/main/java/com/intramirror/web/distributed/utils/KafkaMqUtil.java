package com.intramirror.web.distributed.utils;

import java.util.Properties;
import org.apache.commons.lang.StringUtils;
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
    private static Producer<String, String> producer;

    static {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("bootstrap.servers"));
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        try {
            producer = new KafkaProducer<>(properties);
        } catch (Exception e) {
            LOGGER.error("Fail to init kafka producer.", e);
        }
    }

    private static final String IMAGE_QUEUE = "image_url_data";
    private static final String PRODUCT_RESULT_QUEUE = StringUtils.isBlank(
            System.getProperty("kafka.topic.productResultData")) ? "resultData" : System.getProperty("kafka.topic.productResultData");
    private static final String STOCK_RESULT_QUEUE = StringUtils.isBlank(System.getProperty("kafka.topic.stockResultData")) ? "resultData" : System.getProperty(
            "kafka.topic.stockResultData");

    public static void sendImageMessage(String key, String value) {
        producer.send(new ProducerRecord<>(IMAGE_QUEUE, key, value));
    }

    public static void sendProductResultMessage(String key, String value) {
        producer.send(new ProducerRecord<>(PRODUCT_RESULT_QUEUE, key, value));
    }

    public static void sendStockResultMessage(String key, String value) {
        producer.send(new ProducerRecord<>(STOCK_RESULT_QUEUE, key, value));
    }

}
