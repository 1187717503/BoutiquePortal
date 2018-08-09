package com.intramirror.product.api.service;

public interface IKafkaManagerService {

    /**
     * 当Group发生变化时，推送消息到kafka
     *
     * @param productId
     */
    void sendGroupChanged(Long productId);

    /**
     * 当Season发生变化时，推送消息到kafka
     *
     * @param productId
     */
    void sendSeasonChanged(Long productId);

    /**
     * 当Price发生变化时，推送消息到kafka
     *
     * @param productId
     */
    void sendPriceChanged(Long productId);
}
