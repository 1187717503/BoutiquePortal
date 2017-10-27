package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.model.ShopProduct;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.ShopProductService;
import com.intramirror.web.common.Response;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired
    private IProductService productService;

    @Autowired
    private ShopProductService shopProductService;

    @RequestMapping(value = "/{action}", method = RequestMethod.PUT)
    public Response operateProduct(@PathVariable(value = "action") String action, @RequestParam(value = "product_id") String productId) {
        Map<String, Object> currentState = productService.getProductStateByProductId(Long.parseLong(productId));
        LOGGER.info("[{}] product with [{}]", action, currentState);
        StateMachineCoreRule.validate(currentState, action);
        updateProductState(currentState, action, Long.parseLong(productId));
        return Response.success();
    }

    @Transactional
    private void updateProductState(Map<String, Object> currentState, String action, Long productId) {
        OperationEnum operation = ProductStateOperationMap.getOperation(action);
        StateEnum currentStateEnum = StateMachineCoreRule.map2StateEnum(currentState);
        StateEnum newStateEnum = StateMachineCoreRule.getNewState(currentStateEnum, operation);
        LOGGER.info("[{}] -> [{}] -> [{}]", currentStateEnum.name(), operation.name(), newStateEnum.name());

        updateProductStatus(newStateEnum.getProductStatus(), productId);
        if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() == -1) {
            //product -> product
        } else if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() != -1) {
            //product -> shop product
            createShopProductStatus(newStateEnum.getProductStatus(), productId);
        } else if (currentStateEnum.getShopProductStatus() != -1 && newStateEnum.getShopProductStatus() != -1) {
            //shop product -> shop product
            updateShopProductStatus(newStateEnum.getProductStatus(), productId);
        } else {
            //shop product -> product
            disableShopProduct(newStateEnum.getProductStatus(), productId);
        }

    }

    private void updateProductStatus(int status, Long productId) {
        ProductWithBLOBs product = new ProductWithBLOBs();
        product.setStatus((byte) 1);
        product.setProductId(productId);
        productService.updateByPrimaryKeySelective(product);
    }

    private void updateShopProductStatus(int status, Long productId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setStatus((byte) 1);
        shopProduct.setProductId(productId);
        shopProductService.updateShopProductByProductId(shopProduct);
    }

    private void disableShopProduct(int status, Long productId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setEnabled(false);
        shopProduct.setProductId(productId);
        shopProductService.updateShopProductByProductId(shopProduct);

    }

    private void createShopProductStatus(int status, Long productId) {
//        shopProductService.in
    }
}
