package com.intramirror.web.vo;

/**
 * Created by dingyifan on 2017/8/31.
 */
public class ParamsVo {
    private String mqName;
    private String paramKey;
    private String paramValue;

    public String getMqName() {
        return mqName;
    }

    public void setMqName(String mqName) {
        this.mqName = mqName;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
