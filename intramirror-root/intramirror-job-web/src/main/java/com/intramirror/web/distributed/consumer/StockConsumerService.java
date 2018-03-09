package com.intramirror.web.distributed.consumer;

import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.distributed.thread.ThreadManager;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.properties.MicroProperties;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created on 2018/3/5.
 *
 * @author YouFeng.Zhu
 */
@Service
public class StockConsumerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(StockConsumerService.class);
    private static Properties stockConsumerProps = new Properties();
    private volatile AtomicBoolean stockConsumerRunning = new AtomicBoolean(false);

    private final static String STOCK_RAW_QUEUE = StringUtils.isBlank(System.getProperty("kafka.topic.stockRawData")) ? "stockRawData" : System.getProperty(
            "kafka.topic.stockRawData");

    private CountDownLatch shutDownCount;

    @Autowired
    @Qualifier("atelierStockMapping")
    private IStockMapping iStockMapping;

    @Autowired
    private MicroProperties properties;

    @PostConstruct
    public void init() {
        stockConsumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        stockConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "group_stock_raw_data");
        stockConsumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        stockConsumerProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        stockConsumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        stockConsumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

    }

    public void stopConsumeStock() {
        if (!stockConsumerRunning.compareAndSet(true, false)) {
            LOGGER.info("Consumer Stock is not running");
            return;
        }
        try {
            if (shutDownCount != null) {
                shutDownCount.await(60, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Consumer Stock Thread shutdown time out", e);
        }
    }

    @PostConstruct
    public void startDefaultConsumeStock() {
        startConsumeStock(3,50);
    }

    public void startConsumeStock(int concurrency,int workThreads) {
        if (stockConsumerRunning.compareAndSet(false, true)) {
            LOGGER.info("Consumer Stock already started.");
        }
        ExecutorService consumerThreadPool = Executors.newFixedThreadPool(concurrency);
        ExecutorService workThreadPool = ThreadManager.newFixedBlockingThreadPool(workThreads);
        shutDownCount = new CountDownLatch(concurrency);
        for (int i = 0; i < concurrency; i++) {
            consumerThreadPool.submit(() -> {
                try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(stockConsumerProps)) {
                    consumer.subscribe(Arrays.asList(STOCK_RAW_QUEUE));
                    while (stockConsumerRunning.get()) {
                        ConsumerRecords<String, String> records = consumer.poll(500);
                        for (ConsumerRecord<String, String> record : records) {
                            LOGGER.info("partition = {} , offset = {}, key = {}, value = {}", record.partition(), record.offset(), record.key(),
                                    record.value());
                            Map<String, Object> stockContext = JsonTransformUtil.readValue(record.value(), Map.class);
                            if (stockContext == null) {
                                LOGGER.error("stockContext format error: {}", record.value());
                                continue;
                            }
                            Long vendorId = Long.parseLong(stockContext.get("vendorId").toString());
                            String eventName = stockContext.get("eventType").toString();
                            String vendorName = stockContext.get("vendorName").toString();
                            Map<String, Object> data = (Map<String, Object>) stockContext.get("data");
                            List<Map<String, Object>> skuList = (List<Map<String, Object>>) data.get("sku");
                            for (Map<String, Object> sku : skuList) {
                                sku.put("vendor_id", vendorId);
                                StockOption stockOption = iStockMapping.mapping(sku);
                                workThreadPool.submit(new UpdateStockThread(stockOption, new ApiDataFileUtils(vendorName, eventName), sku));
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Product Consumer Thread exception.", e);
                }
                shutDownCount.countDown();

            });
        }
    }

}
