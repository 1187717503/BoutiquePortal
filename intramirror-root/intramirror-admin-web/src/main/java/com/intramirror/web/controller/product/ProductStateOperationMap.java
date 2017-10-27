package com.intramirror.web.controller.product;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
public class ProductStateOperationMap {

    private final static Map<String, StateEnum> statusEnumMap = new HashMap<>();
    private final static Map<String, OperationEnum> operationEnumMap = new HashMap<>();

    static {
        statusEnumMap.put("new", StateEnum.NEW);
        statusEnumMap.put("processing", StateEnum.PROCESSING);
        statusEnumMap.put("trash", StateEnum.TRASH);
        statusEnumMap.put("readytosell", StateEnum.READY_TO_SELL);
        //TODO: how to handle old processing
        statusEnumMap.put("oldprocessing", StateEnum.OLD_PROCESSING);
        statusEnumMap.put("shopprocessing", StateEnum.SHOP_PROCESSING);
        statusEnumMap.put("shopsoldout", StateEnum.SHOP_SOLD_OUT);
        statusEnumMap.put("shopreadytosale", StateEnum.SHOP_READY_TO_SALE);
        statusEnumMap.put("shoponsale", StateEnum.SHOP_ON_SALE);
        statusEnumMap.put("shopremoved", StateEnum.SHOP_REMOVED);
    }

    static {
        operationEnumMap.put("approve", OperationEnum.APPROVE);
        operationEnumMap.put("addtoshop", OperationEnum.ADD_TO_SHOP);
        operationEnumMap.put("offsale", OperationEnum.OFF_SALE);
        operationEnumMap.put("onsale", OperationEnum.ON_SALE);
        operationEnumMap.put("process", OperationEnum.PROCESS);
        operationEnumMap.put("remove", OperationEnum.REMOVE);
    }

    public static StateEnum getStatus(String status) {
        return statusEnumMap.get(status) == null ? StateEnum.ALL : statusEnumMap.get(status);
    }

    public static OperationEnum getOperation(String operation) {
        return operationEnumMap.get(operation);
    }
}
