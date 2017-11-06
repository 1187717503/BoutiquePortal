package com.intramirror.web.controller.product;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.web.Exception.ErrorResponse;
import com.intramirror.web.Exception.ValidateException;
import com.intramirror.web.common.response.BatchResponseItem;
import com.intramirror.web.common.response.BatchResponseMessage;
import com.intramirror.web.common.response.Response;
import static com.intramirror.web.controller.product.StateMachineCoreRule.map2StateEnum;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/product/operate")
public class StateMachineController {
    private final static Logger LOGGER = LoggerFactory.getLogger(StateMachineController.class);

    private final static long shopId = 65;

    @Autowired
    private ProductManagementService productManagementService;

    @Autowired
    private ISkuStoreService iSkuStoreService;

    @PutMapping(value = "/single/{action}", consumes = "application/json")
    public Response operateProduct(@PathVariable(value = "action") String action, @RequestBody Map<String, Object> body) {
        Long productId = Long.parseLong(body.get("productId").toString());
        Long shopProductId = body.get("shopProductId") == null ? null : Long.parseLong(body.get("shopProductId").toString());
        Map<String, Object> currentState = productManagementService.getProductStateByProductId(productId);
        LOGGER.info("[{}] product with [{}]", action, currentState);
        StateMachineCoreRule.validate(currentState, action);
        updateProductState(currentState, action, productId, shopProductId);
        return Response.success();
    }

    private void updateProductState(Map<String, Object> currentState, String action, Long productId, Long shopProductId) {
        OperationEnum operation = ProductStateOperationMap.getOperation(action);
        StateEnum currentStateEnum = map2StateEnum(currentState);
        StateEnum newStateEnum = StateMachineCoreRule.getNewState(currentStateEnum, operation);
        LOGGER.info("Product [{}] Start [{}] -> [{}] -> [{}]", productId, currentStateEnum.name(), operation.name(), newStateEnum.name());
        if (operation == OperationEnum.ON_SALE) {
            if (!isInStock(productId)) {
                newStateEnum = StateEnum.SHOP_SOLD_OUT;
                LOGGER.info("Change Product [{}] as Start [{}] -> [{}] -> [{}]", productId, currentStateEnum.name(), operation.name(), newStateEnum.name());
            }
        }
        if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() == -1) {
            productManagementService.updateProductStatus(newStateEnum.getProductStatus(), productId);
        } else if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() != -1) {
            productManagementService.updateProductStatusAndNewShopProduct(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId);
        } else if (currentStateEnum.getShopProductStatus() != -1) {
            if (shopProductId == null) {
                LOGGER.error("shopProductId missed.");
                throw new ValidateException(new ErrorResponse("Parameter shopProductId missed"));
            }
            if (newStateEnum.getShopProductStatus() != -1) {
                productManagementService.updateProductAndShopProductStatus(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId,
                        shopProductId);
            } else if (newStateEnum.getShopProductStatus() == -1) {
                productManagementService.updateProductStatusAndDisableShopProduct(newStateEnum.getProductStatus(), productId, shopProductId);
            }
        }
        LOGGER.info("Product: [{}]  [{}] -> [{}] -> [{}] SUCCESSFUL", productId, currentStateEnum.name(), operation.name(), newStateEnum.name());
    }

    private boolean isInStock(Long productId) {
        Long totalStock = iSkuStoreService.getTotalStockByProductId(productId);
        if (totalStock == null || totalStock <= 0) {
            return false;
        }
        return true;
    }

    @PutMapping(value = "/batch/{action}", consumes = "application/json")
    public Response batchOperateProduct(@PathVariable(value = "action") String action, @RequestBody Map<String, Object> body) {
        if (body.get("ids") == null) {
            throw new ValidateException(new ErrorResponse("Parameter missed"));
        }
        if (body.get("originalState") == null) {
            throw new ValidateException(new ErrorResponse("Parameter originalState missed"));
        }
        StateEnum originalState = ProductStateOperationMap.getStatus(body.get("originalState").toString());
        if (originalState == null) {
            throw new ValidateException(new ErrorResponse("Unkown original state : " + body.get("originalState").toString()));
        }
        StateMachineCoreRule.validate(originalState, action);

        BatchResponseMessage responseMessage = new BatchResponseMessage();
        Map<Long, Long> validIdsMap = batchValidate(originalState, (List) body.get("ids"), action, responseMessage);

        batchUpdateProductState(originalState, action, validIdsMap, responseMessage);

        return Response.status(StatusType.SUCCESS).data(responseMessage);
    }

    private Map<Long, Long> batchValidate(StateEnum originalState, List<Map<String, Object>> idsList, String action, BatchResponseMessage responseMessage) {
        Map<Long, Long> idsMap = listMap2Map(idsList);

        List<Map<String, Object>> stateList = productManagementService.listProductStateByProductIds(new LinkedList<Long>(idsMap.keySet()));
        for (Map<String, Object> state : stateList) {
            StateEnum productState = map2StateEnum(state);
            Long productId = Long.parseLong(state.get("product_id").toString());
            //Long shopProductId = state.get("shop_product_id") == null ? null : Long.parseLong(state.get("shop_product_id").toString());
            Long shopProductId = idsMap.get(productId);
            if (productState == null) {
                responseMessage.getFailed().add(new BatchResponseItem(productId, shopProductId, "Unknown state : " + state.toString()));
                idsMap.remove(productId);
                continue;
            }
            if (isStateSame(productState, originalState)) {
                responseMessage.getFailed().add(new BatchResponseItem(productId, shopProductId, "Product has changed to : " + productState.name()));
                idsMap.remove(productId);
                continue;
            }
            if (originalState.getShopProductStatus() != -1 && shopProductId == null) {
                Long realShopProductId = state.get("shop_product_id") == null ? null : Long.parseLong(state.get("shop_product_id").toString());
                //Maybe this logic is not necessary
                if (realShopProductId == null) {
                    responseMessage.getFailed().add(new BatchResponseItem(productId, shopProductId, "Parameter shopProductId missed"));
                    idsMap.remove(productId);
                } else {
                    LOGGER.info("No shop_product_id in body, get it from db by productId [{}].", productId);
                    idsMap.put(productId, realShopProductId);
                }
                continue;
            }
        }
        return idsMap;
    }

    private boolean isStateSame(StateEnum actual, StateEnum original) {
        StateEnum tmp = actual;
        if (StateEnum.OLD_PROCESSING == tmp) {
            tmp = StateEnum.PROCESSING;
        } else if (StateEnum.OLD_SHOP_PROCESSING == tmp) {
            tmp = StateEnum.SHOP_PROCESSING;
        }
        return tmp == original ? true : false;
    }

    private void batchUpdateProductState(StateEnum currentStateEnum, String action, Map<Long, Long> validIdsMap, BatchResponseMessage responseMessage) {
        if (validIdsMap.size() == 0) {
            return;
        }

        OperationEnum operation = ProductStateOperationMap.getOperation(action);
        StateEnum newStateEnum = StateMachineCoreRule.getNewState(currentStateEnum, operation);

        LOGGER.info("Product [{}] Start [{}] -> [{}] -> [{}]", idsMapToString(validIdsMap), currentStateEnum.name(), operation.name(), newStateEnum.name());
        if (operation == OperationEnum.ON_SALE) {
            Map<Long, Long> outOfStockMap = batchCheckInStock(validIdsMap);
            LOGGER.info("Out of stock : change Product [{}] as Start [{}] -> [{}] -> [{}]", idsMapToString(outOfStockMap), currentStateEnum.name(),
                    operation.name(), newStateEnum.name());
            batchUpdateProcess(currentStateEnum, StateEnum.SHOP_SOLD_OUT, outOfStockMap, responseMessage);
        }
        batchUpdateProcess(currentStateEnum, newStateEnum, validIdsMap, responseMessage);
        LOGGER.info("Product: [{}]  [{}] -> [{}] -> [{}] SUCCESSFUL", idsMapToString(validIdsMap), currentStateEnum.name(), operation.name(),
                newStateEnum.name());

        addSuccessToResponse(validIdsMap, responseMessage);
    }

    private void batchUpdateProcess(StateEnum currentStateEnum, StateEnum newStateEnum, Map<Long, Long> idsMap, BatchResponseMessage responseMessage) {
        if (idsMap.size() == 0) {
            return;
        }

        List<Long> productIds = new LinkedList<>(idsMap.keySet());
        List<Long> shopProductIds = new LinkedList<>(idsMap.values());
        if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() == -1) {
            productManagementService.batchUpdateProductStatus(newStateEnum.getProductStatus(), productIds);
        } else if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() != -1) {
            productManagementService.batchUpdateProductStatusAndNewShopProduct(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(),
                    productIds);
        } else if (currentStateEnum.getShopProductStatus() != -1) {

            if (newStateEnum.getShopProductStatus() != -1) {
                productManagementService.batchUpdateProductAndShopProductStatus(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(),
                        productIds, shopProductIds);
            } else if (newStateEnum.getShopProductStatus() == -1) {
                productManagementService.batchUpdateProductStatusAndDisableShopProduct(newStateEnum.getProductStatus(), productIds, shopProductIds);
            }
        }
    }

    private Map<Long, Long> batchCheckInStock(Map<Long, Long> idsMap) {
        List<Long> productIds = new LinkedList<>(idsMap.keySet());
        List<Map<String, Object>> productStockList = iSkuStoreService.listTotalStockByProductIds(productIds);
        Map<Long, Long> productOutOfStockMap = new HashMap<>();
        for (Map<String, Object> productStock : productStockList) {
            if (productStock.get("total_stock") == null || Long.parseLong(productStock.get("total_stock").toString()) <= 0) {
                Long productId = Long.parseLong(productStock.get("product_id").toString());
                productOutOfStockMap.put(productId, idsMap.get(productId));
                idsMap.remove(productId);
            }
        }
        return productOutOfStockMap;
    }

    private Map<Long, Long> listMap2Map(List<Map<String, Object>> idsList) {
        Map<Long, Long> idsMap = new HashMap<>();
        for (Map<String, Object> ids : idsList) {
            Long productId = Long.parseLong(ids.get("productId").toString());
            Long shopProductId = (ids.get("shopProductId") == null || StringUtils.isEmpty(ids.get("shopProductId").toString())) ? null : Long.parseLong(
                    ids.get("shopProductId").toString());
            idsMap.put(productId, shopProductId);
        }
        return idsMap;
    }

    private void addSuccessToResponse(Map<Long, Long> successIdsMap, BatchResponseMessage responseMessage) {
        for (Map.Entry<Long, Long> entry : successIdsMap.entrySet()) {
            responseMessage.getSuccess().add(new BatchResponseItem(entry.getKey(), entry.getValue()));
        }
    }

    private String idsMapToString(Map<Long, Long> validIdsMap) {
        StringBuilder sb = new StringBuilder("[");
        for (Map.Entry<Long, Long> entry : validIdsMap.entrySet()) {
            sb.append("{ productId:").append(entry.getKey());
            if (entry.getValue() != null) {
                sb.append(",shopProductId:").append(entry.getValue());
            }
            sb.append("},");
        }
        return sb.substring(0, sb.length() - 1);
    }

    //    private validateParameters()

}
