package com.intramirror.common;

/**
 * Created by dingyifan on 2017/10/17.
 */
public class CommonProperties {

    //admin2调用APP接口下单
    private String appOrderUrl;

    private String appCheckUrl;

    private String ruleExcelPath;

    private String kafakaServer;
    private String kafakaTopicSnapshot;

    public String getKafakaServer() {
        return kafakaServer;
    }

    public void setKafakaServer(String kafakaServer) {
        this.kafakaServer = kafakaServer;
    }

    public String getKafakaTopicSnapshot() {
        return kafakaTopicSnapshot;
    }

    public void setKafakaTopicSnapshot(String kafakaTopicSnapshot) {
        this.kafakaTopicSnapshot = kafakaTopicSnapshot;
    }

    public String getAppOrderUrl() {
        return appOrderUrl;
    }

    public void setAppOrderUrl(String appOrderUrl) {
        this.appOrderUrl = appOrderUrl;
    }

    public String getAppCheckUrl() {
        return appCheckUrl;
    }

    public void setAppCheckUrl(String appCheckUrl) {
        this.appCheckUrl = appCheckUrl;
    }

    public String getRuleExcelPath() {
        return ruleExcelPath;
    }

    public void setRuleExcelPath(String ruleExcelPath) {
        this.ruleExcelPath = ruleExcelPath;
    }
}
