package com.intramirror.order.api.common.enums;

/**
 * Created on 2019/1/20.
 *
 * @author yfding
 */
public enum CcZhangOrderEmailActionTypeEnum {

    confirmed(1L, "Confirmed"), shipped(2L, "Shipped");

    private Long actionType;

    private String activeTypeValue;

    public Long getActionType() {
        return actionType;
    }

    public void setActionType(Long actionType) {
        this.actionType = actionType;
    }

    public String getActiveTypeValue() {
        return activeTypeValue;
    }

    public void setActiveTypeValue(String activeTypeValue) {
        this.activeTypeValue = activeTypeValue;
    }

    CcZhangOrderEmailActionTypeEnum(Long actionType, String activeTypeValue) {
        this.actionType = actionType;
        this.activeTypeValue = activeTypeValue;
    }
}
