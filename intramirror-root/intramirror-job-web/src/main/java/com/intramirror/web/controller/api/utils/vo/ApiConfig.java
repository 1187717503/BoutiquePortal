package com.intramirror.web.controller.api.utils.vo;

import com.intramirror.web.util.ApiDataFileUtils;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by dingyifan on 2018/1/12.
 */
public class ApiConfig {
    private String url;

    private Long vendor_id;

    private String vendorName;

    private int threadNum;

    private String eventName;

    private ThreadPoolExecutor executor;

    private ApiDataFileUtils fileUtils;

    private int index;

    /* Matgento */
    private String originPath ;
    private String revisedPath ;
    private String originName ;
    private String revisedName ;
    private int limit ;
    /* Matgento */

    public String getUrl() {
        return url;
    }

    public ApiConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public Long getVendor_id() {
        return vendor_id;
    }

    public ApiConfig setVendor_id(Long vendor_id) {
        this.vendor_id = vendor_id;
        return this;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public ApiConfig setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public ApiConfig setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public ApiConfig setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
        return this;
    }

    public ApiDataFileUtils getFileUtils() {
        return fileUtils;
    }

    public ApiConfig setFileUtils(ApiDataFileUtils fileUtils) {
        this.fileUtils = fileUtils;
        return this;
    }

    public String getVendorName() {
        return vendorName;
    }

    public ApiConfig setVendorName(String vendorName) {
        this.vendorName = vendorName;
        return this;
    }

    public String getOriginPath() {
        return originPath;
    }

    public ApiConfig setOriginPath(String originPath) {
        this.originPath = originPath;
        return this;
    }

    public String getRevisedPath() {
        return revisedPath;
    }

    public ApiConfig setRevisedPath(String revisedPath) {
        this.revisedPath = revisedPath;
        return this;
    }

    public String getOriginName() {
        return originName;
    }

    public ApiConfig setOriginName(String originName) {
        this.originName = originName;
        return this;
    }

    public String getRevisedName() {
        return revisedName;
    }

    public ApiConfig setRevisedName(String revisedName) {
        this.revisedName = revisedName;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public ApiConfig setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public ApiConfig setIndex(int index) {
        this.index = index;
        return this;
    }
}
