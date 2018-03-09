package com.intramirror.web.properties;

import org.springframework.beans.factory.InitializingBean;

/**
 * Created on 2018/2/5.
 *
 * @author YouFeng.Zhu
 */

public class MicroProperties implements InitializingBean {

    private String baseUrl;

    private String username;

    private String password;

    private String bootstrapServers;

    private String topicProductRawData;
    private String topicStockRawData;
    private String topicResultData;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getTopicProductRawData() {
        return topicProductRawData;
    }

    public void setTopicProductRawData(String topicProductRawData) {
        this.topicProductRawData = topicProductRawData;
    }

    public String getTopicStockRawData() {
        return topicStockRawData;
    }

    public void setTopicStockRawData(String topicStockRawData) {
        this.topicStockRawData = topicStockRawData;
    }

    public String getTopicResultData() {
        return topicResultData;
    }

    public void setTopicResultData(String topicResultData) {
        this.topicResultData = topicResultData;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty("bootstrap.servers", this.bootstrapServers);
        System.setProperty("kafka.topic.productRawData", this.topicProductRawData);
        System.setProperty("kafka.topic.stockRawData", this.topicStockRawData);
        System.setProperty("kafka.topic.resultData", this.topicResultData);
    }

}
