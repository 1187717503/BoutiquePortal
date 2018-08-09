package com.intramirror.order.api.vo;

import java.util.List;

/**
 * Created by caowei on 2018/7/24.
 */
public class TransportationRouteVo {
    private String routingName;
    private List<ProviderVo> providerVoList;

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
