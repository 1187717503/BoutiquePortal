package com.intramirror.web.distributed.consumer;

import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.distributed.thread.ThreadManager;
import com.intramirror.web.mapping.impl.atelier.AtelierUpdateByProductMapping;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;

/**
 * Created on 2018/3/5.
 *
 * @author YouFeng.Zhu
 */
@Service
public class ConsumerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);
    private static Properties productConsumerProps = new Properties();
    private volatile AtomicBoolean productConsumerRunning = new AtomicBoolean(false);

    private CountDownLatch shutDownCount;

    @Autowired
    @Qualifier("atelierUpdateByProductMapping")
    private AtelierUpdateByProductMapping atelierUpdateByProductMapping;

    static {

        productConsumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "106.15.229.248:9092");
        productConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "group_raw_data");
        productConsumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        productConsumerProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        productConsumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        productConsumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

    }

    public void stopConsumerProduct() {
        if (!productConsumerRunning.compareAndSet(true, false)) {
            LOGGER.info("Consumer Product is not running");
            return;
        }
        try {
            if (shutDownCount != null) {
                shutDownCount.await(60, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Consumer Product Thread shutdown time out", e);
        }
    }

    @PostConstruct
    public void startDefaultConsumerProduct() {
        startConsumerProduct(3);
    }

    public void startConsumerProduct(int concurrency) {
        if (productConsumerRunning.compareAndSet(false, true)) {
            LOGGER.info("Consumer Product already started");
        }
        ExecutorService consumerThreadPool = Executors.newFixedThreadPool(concurrency);
        shutDownCount = new CountDownLatch(concurrency);
        for (int i = 0; i < concurrency; i++) {
            consumerThreadPool.submit(() -> {
                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(productConsumerProps);
                consumer.subscribe(Arrays.asList("rawData"));
                while (productConsumerRunning.get()) {
                    ConsumerRecords<String, String> records = consumer.poll(500);
                    for (ConsumerRecord<String, String> record : records) {
                        LOGGER.info("partition = {} , offset = {}, key = {}, value = {}", record.partition(), record.offset(), record.key(), record.value());
                        Map<String, Object> productContext = JsonTransformUtil.readValue(record.value(), Map.class);
                        if (productContext == null) {
                            LOGGER.error("ProductContext format error: {}", record.value());
                        }
                        Long vendorId = Long.parseLong(productContext.get("vendorId").toString());
                        String eventName = productContext.get("eventName").toString();
                        String vendorName = productContext.get("vendorName").toString();
                        Map<String, Object> data = (Map<String, Object>) productContext.get("data");

                        ProductEDSManagement.ProductOptions productOptions = atelierUpdateByProductMapping.mapping(data);
                        ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement.VendorOptions();
                        vendorOptions.setVendorId(vendorId);
                        ThreadManager.submit(new UpdateProductThread(productOptions, vendorOptions, new ApiDataFileUtils(vendorName, eventName), data));
                    }
                }
                consumer.close();
                shutDownCount.countDown();

            });
        }
    }

}
