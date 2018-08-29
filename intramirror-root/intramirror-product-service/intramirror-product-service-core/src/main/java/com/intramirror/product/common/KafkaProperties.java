package com.intramirror.product.common;

/**
 * Created on 2018/4/12.
 * @author 123
 */

public class KafkaProperties {

    private String serverName;

    private String topic;

    private String productTopic;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProductTopic() {
        return productTopic;
    }

    public void setProductTopic(String productTopic) {
        this.productTopic = productTopic;
    }
}
