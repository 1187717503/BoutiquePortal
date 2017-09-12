package com.intramirror.web.mq.vo;

import java.util.List;

/**
 * Created by dingyifan on 2017/9/11.
 */
public class MqProperties {
    private String accessKey;
    private String secretKey;
    private String ONSAddr;
    private List<Region> regions;

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

    public String getONSAddr() {
        return ONSAddr;
    }

    public void setONSAddr(String ONSAddr) {
        this.ONSAddr = ONSAddr;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }
}
