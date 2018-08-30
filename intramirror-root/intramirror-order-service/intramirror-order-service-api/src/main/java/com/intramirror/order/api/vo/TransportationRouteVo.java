package com.intramirror.order.api.vo;

import java.util.List;

/**
 * Created by caowei on 2018/7/24.
 */
public class TransportationRouteVo {
    private String routingName;
    private Long routingId;
    private List<ProviderVo> providerVoList;

    public Long getRoutingId() {
        return routingId;
    }

    public void setRoutingId(Long routingId) {
        this.routingId = routingId;
    }

    public String getRoutingName() {
        return routingName;
    }

    public void setRoutingName(String routingName) {
        this.routingName = routingName;
    }

    public List<ProviderVo> getProviderVoList() {
        return providerVoList;
    }

    public void setProviderVoList(List<ProviderVo> providerVoList) {
        this.providerVoList = providerVoList;
    }
}
