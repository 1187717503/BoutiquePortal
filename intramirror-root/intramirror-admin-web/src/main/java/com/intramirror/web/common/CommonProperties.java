package com.intramirror.web.common;

/**
 * Created by dingyifan on 2017/10/17.
 *
 */
public class CommonProperties {

    //admin2调用APP接口下单
    private String appOrderUrl;

    private String appCheckUrl;

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
}
