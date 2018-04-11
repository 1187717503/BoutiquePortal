package com.intramirror.order.core.utils;

import com.intramirror.common.IKafkaService;

/**
 * Created on 2017/12/24.
 *
 * @author Shang
 */
public class Facility {

    private static Facility facility = new Facility();

    private IKafkaService kafkaService;

    private Facility() {

    }

    public static Facility getInstance() {
        return facility;
    }

    public IKafkaService getKafkaService() {
        return kafkaService;
    }

    public void setKafkaService(IKafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }
}
