package com.intramirror.web.mq;

/**
 * Created by dingyifan on 2017/9/11.
 */
public class MqName {
    private String producerId;
    private String consumerId;

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
}
