package com.intramirror.web.controller.product;

import com.intramirror.web.Exception.ErrorResponse;
import com.intramirror.web.Exception.ValidateException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
public class StateMachineCoreRule {
    private static Map<StateEnum, Map<OperationEnum, StateEnum>> stateCoreRule = new EnumMap<>(StateEnum.class);

    static {
        Map<OperationEnum, StateEnum> newOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.NEW, newOperationMap);
        newOperationMap.put(OperationEnum.APPROVE, StateEnum.READY_TO_SELL);
        newOperationMap.put(OperationEnum.REMOVE, StateEnum.TRASH);
        newOperationMap.put(OperationEnum.PROCESS, StateEnum.PROCESSING);

        Map<OperationEnum, StateEnum> processingOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.PROCESSING, processingOperationMap);
        processingOperationMap.put(OperationEnum.APPROVE, StateEnum.READY_TO_SELL);
        processingOperationMap.put(OperationEnum.REMOVE, StateEnum.TRASH);

        Map<OperationEnum, StateEnum> oldProcessingOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.OLD_PROCESSING, oldProcessingOperationMap);
        oldProcessingOperationMap.put(OperationEnum.APPROVE, StateEnum.READY_TO_SELL);
        oldProcessingOperationMap.put(OperationEnum.REMOVE, StateEnum.TRASH);

        Map<OperationEnum, StateEnum> trashOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.TRASH, trashOperationMap);
        trashOperationMap.put(OperationEnum.APPROVE, StateEnum.READY_TO_SELL);
        trashOperationMap.put(OperationEnum.PROCESS, StateEnum.PROCESSING);

        Map<OperationEnum, StateEnum> readyToSellOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.READY_TO_SELL, readyToSellOperationMap);
        readyToSellOperationMap.put(OperationEnum.REMOVE, StateEnum.TRASH);
        readyToSellOperationMap.put(OperationEnum.PROCESS, StateEnum.PROCESSING);
        readyToSellOperationMap.put(OperationEnum.ADD_TO_SHOP, StateEnum.SHOP_READY_TO_SELL);

        Map<OperationEnum, StateEnum> shopReadyToSaleOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.SHOP_READY_TO_SELL, shopReadyToSaleOperationMap);
        shopReadyToSaleOperationMap.put(OperationEnum.REMOVE, StateEnum.SHOP_REMOVED);
        shopReadyToSaleOperationMap.put(OperationEnum.REMOVE_FROM_SHOP, StateEnum.READY_TO_SELL);
        shopReadyToSaleOperationMap.put(OperationEnum.PROCESS, StateEnum.SHOP_PROCESSING);
        shopReadyToSaleOperationMap.put(OperationEnum.ON_SALE, StateEnum.SHOP_ON_SALE);

        Map<OperationEnum, StateEnum> shopProcessingOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.SHOP_PROCESSING, shopProcessingOperationMap);
        shopProcessingOperationMap.put(OperationEnum.REMOVE, StateEnum.SHOP_REMOVED);
        shopProcessingOperationMap.put(OperationEnum.APPROVE, StateEnum.SHOP_READY_TO_SELL);

        Map<OperationEnum, StateEnum> shopRemovedOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.SHOP_REMOVED, shopRemovedOperationMap);
        shopRemovedOperationMap.put(OperationEnum.APPROVE, StateEnum.SHOP_READY_TO_SELL);

        Map<OperationEnum, StateEnum> shopSoldOutOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.SHOP_SOLD_OUT, shopSoldOutOperationMap);
        shopSoldOutOperationMap.put(OperationEnum.PROCESS, StateEnum.SHOP_PROCESSING);
        shopSoldOutOperationMap.put(OperationEnum.REMOVE, StateEnum.SHOP_REMOVED);
        //TODO: auto on sale after  stock > 0

        Map<OperationEnum, StateEnum> shopOnSaleOperationMap = new EnumMap<>(OperationEnum.class);
        stateCoreRule.put(StateEnum.SHOP_ON_SALE, shopOnSaleOperationMap);
        shopOnSaleOperationMap.put(OperationEnum.PROCESS, StateEnum.SHOP_PROCESSING);
        shopOnSaleOperationMap.put(OperationEnum.REMOVE, StateEnum.SHOP_REMOVED);
        shopOnSaleOperationMap.put(OperationEnum.OFF_SALE, StateEnum.SHOP_READY_TO_SELL);
        //TODO: auto off sale after  stock <= 0
    }

    public static StateEnum map2StateEnum(Map<String, Object> map) {
        for (StateEnum stateEnum : StateEnum.values()) {
            if (map.get("product_status") == null) {
                break;
            }
            if (stateEnum.getProductStatus() == Integer.parseInt(map.get("product_status").toString()) && stateEnum.getShopProductStatus() == (map.get(
                    "shop_product_status") == null ? -1 : Integer.parseInt(map.get("shop_product_status").toString()))) {
                return stateEnum;
            }
        }
        return null;
    }

    static boolean isMatch(StateEnum currentState, OperationEnum operation) {
        return stateCoreRule.get(currentState).containsKey(operation);
    }

    public static void validate(Map<String, Object> currentState, String action) {
        OperationEnum operation = ProductStateOperationMap.getOperation(action);
        if (operation == null) {
            throw new ValidateException(new ErrorResponse("Unkown operation : " + action));
        }

        StateEnum stateEnum = map2StateEnum(currentState);
        if (stateEnum == null) {
            throw new ValidateException(new ErrorResponse("Unkown product state : " + currentState.toString()));
        }
        checkMatch(stateEnum, operation);
    }

    public static void validate(StateEnum currentState, String action) {
        OperationEnum operation = ProductStateOperationMap.getOperation(action);
        if (operation == null) {
            throw new ValidateException(new ErrorResponse("Unkown operation : " + action));
        }
        checkMatch(currentState, operation);
    }

    private static void checkMatch(StateEnum currentState, OperationEnum operation) {
        if (!isMatch(currentState, operation)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Product status [").append(currentState.name()).append("]").append(" is not allowed to ").append("[").append(operation.name()).append(
                    "].");
            ErrorResponse errorResponse = new ErrorResponse(sb.toString());
            errorResponse.setDetail(currentState.toString());
            throw new ValidateException(errorResponse);
        }
    }

    public static StateEnum getNewState(StateEnum currentState, OperationEnum operation) {
        return stateCoreRule.get(currentState).get(operation);
    }

}
