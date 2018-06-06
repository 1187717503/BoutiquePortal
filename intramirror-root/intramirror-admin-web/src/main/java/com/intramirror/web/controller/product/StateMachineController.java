package com.intramirror.web.controller.product;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.exception.ValidateException;
import com.intramirror.core.common.response.ErrorResponse;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.model.TagProductRel;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.product.api.service.content.ContentManagementService;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.web.common.response.BatchResponseItem;
import com.intramirror.web.common.response.BatchResponseMessage;
import static com.intramirror.web.controller.product.StateMachineCoreRule.map2StateEnum;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * Created on 2017/10/25.
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

    @Autowired
    private ITagService iTagService;

    @Autowired
    private SkuService skuService;
    @Autowired
    private ContentManagementService contentManagementService;

    @PutMapping(value = "/single/{action}", consumes = "application/json")
    public Response operateProduct(@SessionAttribute(value = "sessionStorage", required = false) Long userId, @PathVariable(value = "action") String action,
            @RequestBody Map<String, Object> body) throws Exception {
        Long productId = Long.parseLong(body.get("productId").toString());
        Long shopProductId = body.get("shopProductId") == null ? null : Long.parseLong(body.get("shopProductId").toString());
        Map<String, Object> currentState = productManagementService.getProductStateByProductId(productId);
        LOGGER.info("[{}] product with [{}]", action, currentState);
        StateMachineCoreRule.validate(currentState, action);
        StateEnum originalState = map2StateEnum(currentState);
        if ((originalState == StateEnum.TRASH || originalState == StateEnum.NEW) && ProductStateOperationMap.getOperation(action) != OperationEnum.REMOVE) {
            List<Sku> skuList = skuService.listSkuInfoByProductId(productId);
            if (skuList.size() <= 0) {
                throw new ValidateException(new ErrorResponse("Sku hasn't created."));
            }
        }
        updateProductState((Long) userId, currentState, action, productId, shopProductId);
        return Response.success();
    }

    private void updateProductState(Long userId, Map<String, Object> currentState, String action, Long productId, Long shopProductId) throws Exception {
        OperationEnum operation = ProductStateOperationMap.getOperation(action);
        StateEnum currentStateEnum = map2StateEnum(currentState);
        StateEnum newStateEnum = StateMachineCoreRule.getNewState(currentStateEnum, operation);
        LOGGER.info("{} : Product [{}] Start [{}] -> [{}] -> [{}]", userId, productId, currentStateEnum.name(), operation.name(), newStateEnum.name());
        if (operation == OperationEnum.ON_SALE) {
            if (!isInStock(productId)) {
                newStateEnum = StateEnum.SHOP_SOLD_OUT;
                LOGGER.info("Auto change Product [{}] as Start [{}] -> [{}] -> [{}] due to out of stock", productId, currentStateEnum.name(), operation.name(),
                        newStateEnum.name());
            }
        }

        LogicOperationEnum logicOperationEnum = StateMachineCoreRule.mapLogicOperation(currentStateEnum, operation);

        switch (logicOperationEnum) {
        case REMOVE:
            productManagementService.remove(newStateEnum.getProductStatus(), productId);
            break;
        case APPROVE:
            productManagementService.approve(newStateEnum.getProductStatus(), productId);
            break;
        case PROCESS:
            productManagementService.process(newStateEnum.getProductStatus(), productId);
            break;
        case ADD_TO_SHOP:
            productManagementService.addToShop(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId);
            break;
        case REMOVE_FROM_SHOP:
            productManagementService.removeFromShop(newStateEnum.getProductStatus(), productId, shopProductId);
            break;

        case ON_SALE:
            productManagementService.onSale(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId, shopProductId);
            break;
        case OFF_SALE:
            productManagementService.offSale(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId, shopProductId);
            break;
        case SHOP_APPROVE:
            productManagementService.shopApprove(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId, shopProductId);
            break;
        case SHOP_PROCESS:
            productManagementService.shopProcess(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId, shopProductId);
            break;
        case SHOP_REMOVE:
            productManagementService.shopRemove(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId, shopProductId);
            break;

        default:
            break;
        }

        //        if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() == -1) {
        //            productManagementService.updateProductStatus(newStateEnum.getProductStatus(), productId);
        //        } else if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() != -1) {
        //            productManagementService.updateProductStatusAndNewShopProduct(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId);
        //        } else if (currentStateEnum.getShopProductStatus() != -1) {
        //            if (shopProductId == null) {
        //                LOGGER.error("shopProductId missed.");
        //                throw new ValidateException(new ErrorResponse("Parameter shopProductId missed"));
        //            }
        //            if (newStateEnum.getShopProductStatus() != -1) {
        //                if (operation == OperationEnum.ON_SALE) {
        //                    productManagementService.onSale(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId, shopProductId);
        //                } else {
        //                    productManagementService.updateProductAndShopProductStatus(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId,
        //                            shopProductId);
        //                }
        //
        //            } else if (newStateEnum.getShopProductStatus() == -1) {
        //
        //                productManagementService.updateProductStatusAndDisableShopProduct(newStateEnum.getProductStatus(), productId, shopProductId);
        //            }
        //        }
        LOGGER.info("{} : Product: [{}]  [{}] -> [{}] -> [{}] SUCCESSFUL", userId, productId, currentStateEnum.name(), operation.name(), newStateEnum.name());
    }

    private boolean isInStock(Long productId) {
        Long totalStock = iSkuStoreService.getTotalStockByProductId(productId);
        if (totalStock == null || totalStock <= 0) {
            return false;
        }
        return true;
    }

    @PutMapping(value = "/batch/{action}", consumes = "application/json")
    public Response batchOperateProduct(@SessionAttribute(value = "sessionStorage", required = false) Long userId, HttpServletRequest request,
            @PathVariable(value = "action") String action, @RequestBody Map<String, Object> body) {

        if(body.get("ids") == null){
            throw new ValidateException(new ErrorResponse("Parameter missed"));
        }
        if(body.get("tagId") == null){
            throw new ValidateException(new ErrorResponse("Parameter missed"));
        }
        Long tagId = Long.valueOf(body.get("tagId").toString());
        if(action.equals("addToGroup") || action.equals("removeGroup")){
            int result = -1;
            if(action.equals("addToGroup")){
                Tag tag = iTagService.selectTagByTagId(tagId);
                if(tag == null){
                    throw new ValidateException(new ErrorResponse("no product group by tagId: : " + body.get("tagId").toString()));
                }
                Map<Long, Long> listMap2Map = listMap2Map((List) body.get("ids"));
                List<Long> ids = null;
                if(listMap2Map.size()>0){
                    ids = new ArrayList<>(listMap2Map.keySet());
                }

                Map<String, Object> map = new HashMap<>();
                map.put("productIdList", ids);
                map.put("tag_id", tagId);
                map.put("sort_num", -1);
                map.put("tagType",tag.getTagType());
                result = iTagService.saveTagProductRel(map);
            }else { // removeGroup

                List<TagProductRel> tagProductRelList = new ArrayList<>();
                Map<Long, Long> listMap2Map = listMap2Map((List) body.get("ids"));
                List<Long> ids = null;
                if(listMap2Map.size()>0){
                    ids = new ArrayList<>(listMap2Map.keySet());
                }

                if(CollectionUtils.isNotEmpty(ids)){
                    for(Long id : ids){
                        TagProductRel rel = new TagProductRel();
                        rel.setTagId(tagId);
                        rel.setTagProductId(id);
                        tagProductRelList.add(rel);
                    }
                }
                contentManagementService.batchDeleteByTagIdAndProductId(tagProductRelList);

            }
            if(result == 0){
                return Response.status(StatusType.SUCCESS).data("The product group does not match the supplier");
            }
            return Response.status(StatusType.SUCCESS).data("update success");
        }
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
        OperationEnum operation = ProductStateOperationMap.getOperation(action);

        BatchResponseMessage responseMessage = new BatchResponseMessage();
        Map<Long, Long> validIdsMap = batchValidate(originalState, (List) body.get("ids"), operation, responseMessage);
        validIdsMap = batchFilterIncompleteProductIds(originalState, validIdsMap, operation, responseMessage);
        batchUpdateProductState(userId, originalState, action, validIdsMap, responseMessage);

        return Response.status(StatusType.SUCCESS).data(responseMessage);
    }

    private Map<Long, Long> batchValidate(StateEnum originalState, List<Map<String, Object>> idsList, OperationEnum operation,
            BatchResponseMessage responseMessage) {
        Map<Long, Long> idsMap = listMap2Map(idsList);

        List<Map<String, Object>> stateList = productManagementService.listProductStateByProductIds(new LinkedList<Long>(idsMap.keySet()));
        for (Map<String, Object> state : stateList) {
            StateEnum productState = map2StateEnum(state);
            Long productId = Long.parseLong(state.get("product_id").toString());
            Long shopProductId = idsMap.get(productId);
            if (productState == null) {
                responseMessage.getFailed().add(
                        new BatchResponseItem(productId, shopProductId, "Product " + state.get("boutique_id") + " has unknown state : " + state.toString()));
                idsMap.remove(productId);
                continue;
            }
            if (!isStateSame(productState, originalState)) {
                responseMessage.getFailed().add(
                        new BatchResponseItem(productId, shopProductId, "Product " + state.get("boutique_id") + " has changed to : " + productState.name()));
                idsMap.remove(productId);
                continue;
            }
            if (originalState.getShopProductStatus() != -1 && shopProductId == null) {
                Long realShopProductId = state.get("shop_product_id") == null ? null : Long.parseLong(state.get("shop_product_id").toString());
                //Maybe this logic is not necessary
                if (realShopProductId == null) {
                    responseMessage.getFailed().add(
                            new BatchResponseItem(productId, shopProductId, "Product " + state.get("boutique_id") + " shopProductId parameter missed"));
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

    private Map<Long, Long> batchFilterIncompleteProductIds(StateEnum originalState, Map<Long, Long> validIdsMap, OperationEnum operation,
            BatchResponseMessage responseMessage) {
        if (validIdsMap.size() <= 0) {
            return validIdsMap;
        }
        if ((originalState == StateEnum.TRASH || originalState == StateEnum.NEW) && operation != OperationEnum.REMOVE) {
            List<Long> productIds = new LinkedList<>(validIdsMap.keySet());
            List<Sku> skuList = skuService.listSkuInfoByProductIds(productIds);
            Map<Long, Sku> skuMap = new HashMap<>();
            for (Sku sku : skuList) {
                skuMap.put(sku.getProductId(), sku);
            }
            for (Long productId : productIds) {
                if (!skuMap.containsKey(productId)) {
                    validIdsMap.remove(productId);
                    responseMessage.getFailed().add(new BatchResponseItem(productId, null, "Sku hasn't created."));
                }
            }
        }
        return validIdsMap;
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

    private void batchUpdateProductState(Long userId, StateEnum currentStateEnum, String action, Map<Long, Long> validIdsMap,
            BatchResponseMessage responseMessage) {
        if (validIdsMap.size() == 0) {
            return;
        }

        OperationEnum operation = ProductStateOperationMap.getOperation(action);
        StateEnum newStateEnum = StateMachineCoreRule.getNewState(currentStateEnum, operation);

        LOGGER.info("{} : Product [{}] Start [{}] -> [{}] -> [{}]", userId, idsMapToString(validIdsMap), currentStateEnum.name(), operation.name(),
                newStateEnum.name());

        if (/*currentStateEnum == StateEnum.SHOP_READY_TO_SELL &&*/ operation == OperationEnum.ON_SALE) {
            Map<Long, Long> outOfStockMap = batchCheckInStock(validIdsMap);
            if (outOfStockMap.size() > 0) {
                LOGGER.info("{} : Out of stock : change Product [{}] as Start [{}] -> [{}] -> [{}]", userId, idsMapToString(outOfStockMap),
                        currentStateEnum.name(), operation.name(), newStateEnum.name());
                batchUpdateProcess(currentStateEnum, operation, StateEnum.SHOP_SOLD_OUT, outOfStockMap, responseMessage);
            }
        } //else ?

        batchUpdateProcess(currentStateEnum, operation, newStateEnum, validIdsMap, responseMessage);
        LOGGER.info("{} : Product: [{}]  [{}] -> [{}] -> [{}] SUCCESSFUL", userId, idsMapToString(validIdsMap), currentStateEnum.name(), operation.name(),
                newStateEnum.name());

        addSuccessToResponse(validIdsMap, responseMessage);
    }

    private void batchUpdateProcess(StateEnum currentStateEnum, OperationEnum operation, StateEnum newStateEnum, Map<Long, Long> idsMap,
            BatchResponseMessage responseMessage) {
        if (idsMap.size() == 0) {
            return;
        }

        List<Long> productIds = new LinkedList<>(idsMap.keySet());
        List<Long> shopProductIds = new LinkedList<>(idsMap.values());

        LogicOperationEnum logicOperationEnum = StateMachineCoreRule.mapLogicOperation(currentStateEnum, operation);

        switch (logicOperationEnum) {
        case REMOVE:
            productManagementService.batchRemove(newStateEnum.getProductStatus(), productIds);
            break;
        case APPROVE:
            productManagementService.batchApprove(newStateEnum.getProductStatus(), productIds);
            break;
        case PROCESS:
            productManagementService.batchProcess(newStateEnum.getProductStatus(), productIds);
            break;

        case ADD_TO_SHOP:
            List<ProductWithBLOBs> failedProducts = productManagementService.batchAddToShop(newStateEnum.getProductStatus(),
                    newStateEnum.getShopProductStatus(), productIds);
            for (ProductWithBLOBs product : failedProducts) {
                BatchResponseItem item = new BatchResponseItem(product.getProductId(), null,
                        "vendorId:" + product.getVendorId() + ",[" + product.getDesignerId() + "]-[" + product.getColorCode() + "] Add to shop failed ");
                responseMessage.getFailed().add(item);
            }
            break;
        case REMOVE_FROM_SHOP:
            productManagementService.batchRemoveFromShop(newStateEnum.getProductStatus(), productIds, shopProductIds);
            break;
        case ON_SALE:
            productManagementService.batchOnSale(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productIds, shopProductIds);
            break;
        case OFF_SALE:
            productManagementService.batchOffSale(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productIds, shopProductIds);
            break;
        case SHOP_APPROVE:
            productManagementService.batchShopRemove(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productIds, shopProductIds);
            break;
        case SHOP_PROCESS:
            productManagementService.batchShopProcess(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productIds, shopProductIds);
            break;
        case SHOP_REMOVE:
            productManagementService.batchShopRemove(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productIds, shopProductIds);
            break;

        default:
            break;
        }

        //        if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() == -1) {
        //            productManagementService.batchUpdateProductStatus(newStateEnum.getProductStatus(), productIds);
        //        } else if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() != -1) {
        //            productManagementService.batchUpdateProductStatusAndNewShopProduct(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(),
        //                    productIds);
        //        } else if (currentStateEnum.getShopProductStatus() != -1) {
        //            if (newStateEnum.getShopProductStatus() != -1) {
        //                if (operation == OperationEnum.ON_SALE) {
        //                    productManagementService.batchOnSale(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productIds, shopProductIds);
        //                } else {
        //                    productManagementService.batchUpdateProductAndShopProductStatus(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(),
        //                            productIds, shopProductIds);
        //                }
        //            } else if (newStateEnum.getShopProductStatus() == -1) {
        //                productManagementService.batchUpdateProductStatusAndDisableShopProduct(newStateEnum.getProductStatus(), productIds, shopProductIds);
        //            }
        //        }
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
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Long, Long> entry : validIdsMap.entrySet()) {
            sb.append("{ productId:").append(entry.getKey());
            if (entry.getValue() != null) {
                sb.append(",shopProductId:").append(entry.getValue());
            }
            sb.append("},");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    //    private validateParameters()

}
