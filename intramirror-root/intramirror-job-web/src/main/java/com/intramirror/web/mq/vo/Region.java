package com.intramirror.web.mq.vo;

import java.util.List;

/**
 * Created by dingyifan on 2017/9/11.
 */
public class Region {
    private String topic;
    private String producerId;
    private String consumerId;
    private List<Tag> tags;

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
