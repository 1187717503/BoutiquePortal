package com.intramirror.web.mq;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by dingyifan on 2017/9/11.
 */
public class MqProperties {
    private String accessKey;
    private String secretKey;
    private String topic;
    private String ONSAddr;
    private List<MqName> mqNames;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getONSAddr() {
        return ONSAddr;
    }

    public void setONSAddr(String ONSAddr) {
        this.ONSAddr = ONSAddr;
    }

    public List<MqName> getMqNames() {
        return mqNames;
    }

    public void setMqNames(List<MqName> mqNames) {
        this.mqNames = mqNames;
    }

}
