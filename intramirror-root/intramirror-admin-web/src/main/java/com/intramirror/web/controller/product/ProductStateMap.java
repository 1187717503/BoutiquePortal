package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.ProductStatusEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
public class ProductStateMap {

    private final static Map<String, ProductStatusEnum> statusEnumMap = new HashMap<>();
    private final static Map<String, ProductOperationEnum> operationEnumMap = new HashMap<>();

    static {
        statusEnumMap.put("new", ProductStatusEnum.NEW);
        statusEnumMap.put("processing", ProductStatusEnum.PROCESSING);
        statusEnumMap.put("trash", ProductStatusEnum.TRASH);
        statusEnumMap.put("readytosell", ProductStatusEnum.READY_TO_SELL);
        //TODO: how to handle old processing
        statusEnumMap.put("oldprocessing", ProductStatusEnum.OLD_PROCESSING);
        statusEnumMap.put("shopprocessing", ProductStatusEnum.SHOP_PROCESSING);
        statusEnumMap.put("shopsoldout", ProductStatusEnum.SHOP_SOLD_OUT);
        statusEnumMap.put("shopreadytosale", ProductStatusEnum.SHOP_READY_TO_SALE);
        statusEnumMap.put("shoponsale", ProductStatusEnum.SHOP_ON_SALE);
        statusEnumMap.put("shopremoved", ProductStatusEnum.SHOP_REMOVED);
    }

    static {
        operationEnumMap.put("approve", ProductOperationEnum.APPROVE);
        operationEnumMap.put("addtoshop", ProductOperationEnum.ADD_TO_SHOP);
        operationEnumMap.put("offsale", ProductOperationEnum.OFF_SALE);
        operationEnumMap.put("onsale", ProductOperationEnum.ON_SALE);
        operationEnumMap.put("process", ProductOperationEnum.PROCESS);
        operationEnumMap.put("remove", ProductOperationEnum.REMOVE);
    }

    public static ProductStatusEnum getStatus(String status) {
        return statusEnumMap.get(status) == null ? ProductStatusEnum.ALL : statusEnumMap.get(status);
    }

    public static ProductOperationEnum getOperation(String operation) {
        return operationEnumMap.get(operation);
    }
}
