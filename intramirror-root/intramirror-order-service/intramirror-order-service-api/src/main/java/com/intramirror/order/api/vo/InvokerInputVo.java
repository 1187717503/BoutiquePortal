package com.intramirror.order.api.vo;

/**
 * Created by caowei on 2018/7/24.
 */
public class InvokerInputVo {
    private Integer senderCountryId;
    private Integer recipientCountryId;
    private Integer invokerId;
    private Integer invokerType;  //1. 买手店  2. 质检仓

    public Integer getSenderCountryId() {
        return senderCountryId;
    }

    public void setSenderCountryId(Integer senderCountryId) {
        this.senderCountryId = senderCountryId;
    }

    public Integer getRecipientCountryId() {
        return recipientCountryId;
    }

    public void setRecipientCountryId(Integer recipientCountryId) {
        this.recipientCountryId = recipientCountryId;
    }

    public Integer getInvokerId() {
        return invokerId;
    }

    public void setInvokerId(Integer invokerId) {
        this.invokerId = invokerId;
    }

    public Integer getInvokerType() {
        return invokerType;
    }

    public void setInvokerType(Integer invokerType) {
        this.invokerType = invokerType;
    }
}
