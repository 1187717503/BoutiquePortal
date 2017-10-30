package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.model.ShopProduct;
import com.intramirror.product.api.model.ShopProductSku;
import com.intramirror.product.api.model.ShopProductWithBLOBs;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.ShopProductService;
import com.intramirror.product.api.service.ShopProductSkuService;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.web.common.Response;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PutMapping(value = "/{action}", consumes = "application/json")
    public Response operateProduct(@PathVariable(value = "action") String action, @RequestBody Map<String, Object> body) {
        LOGGER.info("body : {}", body);
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
        StateEnum currentStateEnum = StateMachineCoreRule.map2StateEnum(currentState);
        StateEnum newStateEnum = StateMachineCoreRule.getNewState(currentStateEnum, operation);
        LOGGER.info("Starting [{}] -> [{}] -> [{}]", currentStateEnum.name(), operation.name(), newStateEnum.name());

        if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() == -1) {
            productManagementService.updateProductStatus(newStateEnum.getProductStatus(), productId);
        } else if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() != -1) {
            //product -> shop product
            productManagementService.updateProductStatusAndNewShopProduct(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId);
        } else if (currentStateEnum.getShopProductStatus() != -1 && newStateEnum.getShopProductStatus() != -1) {
            //shop product -> shop product
            if (shopProductId == null) {
                LOGGER.error("shopProductId missed");
            }
            productManagementService.updateProductAndShopProductStatus(newStateEnum.getProductStatus(), newStateEnum.getShopProductStatus(), productId,
                    shopProductId);
        } else {
            //shop product -> product
            if (shopProductId == null) {
                LOGGER.error("shopProductId missed");
            }
            productManagementService.updateProductStatusAndDisableShopProduct(newStateEnum.getProductStatus(), productId, shopProductId);
        }

        LOGGER.info("Product: [{}]  [{}] -> [{}] -> [{}] SUCCESSFUL", productId, currentStateEnum.name(), operation.name(), newStateEnum.name());

    }

}
