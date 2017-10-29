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
import com.intramirror.web.common.Response;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
    private IProductService productService;

    @Autowired
    private ShopProductService shopProductService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ShopProductSkuService shopProductSkuService;

    @PutMapping(value = "/{action}")
    public Response operateProduct(@PathVariable(value = "action") String action, @RequestParam(value = "product_id") String productId,
            @RequestParam(value = "shop_product_id", required = false) String shopProductId) {
        Map<String, Object> currentState = productService.getProductStateByProductId(Long.parseLong(productId));
        LOGGER.info("[{}] product with [{}]", action, currentState);
        StateMachineCoreRule.validate(currentState, action);
        updateProductState(currentState, action, Long.parseLong(productId), shopProductId == null ? null : Long.parseLong(shopProductId));
        return Response.success();
    }

    @Transactional
    private void updateProductState(Map<String, Object> currentState, String action, Long productId, Long shopProductId) {
        OperationEnum operation = ProductStateOperationMap.getOperation(action);
        StateEnum currentStateEnum = StateMachineCoreRule.map2StateEnum(currentState);
        StateEnum newStateEnum = StateMachineCoreRule.getNewState(currentStateEnum, operation);
        LOGGER.info("[{}] -> [{}] -> [{}]", currentStateEnum.name(), operation.name(), newStateEnum.name());

        updateProductStatus(newStateEnum.getProductStatus(), productId);
        if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() == -1) {
            //product -> product
        } else if (currentStateEnum.getShopProductStatus() == -1 && newStateEnum.getShopProductStatus() != -1) {
            //product -> shop product
            createShopProductStatus(newStateEnum.getShopProductStatus(), productId);
        } else if (currentStateEnum.getShopProductStatus() != -1 && newStateEnum.getShopProductStatus() != -1) {
            //shop product -> shop product
            updateShopProductStatus(newStateEnum.getShopProductStatus(), productId);
        } else {
            //shop product -> product
            if (shopProductId == null) {
                LOGGER.error("shopProductId missed");
                return;
            }
            disableShopProductStatus(shopProductId);
        }

    }

    private void updateProductStatus(int status, Long productId) {
        ProductWithBLOBs product = new ProductWithBLOBs();
        product.setStatus((byte) status);
        product.setProductId(productId);
        productService.updateByPrimaryKeySelective(product);
    }

    private void updateShopProductStatus(int status, Long productId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setStatus((byte) status);
        shopProduct.setProductId(productId);
        shopProductService.updateShopProductByProductId(shopProduct);
    }

    private void disableShopProductStatus(Long shopProductId) {
        disableShopProductSku(shopProductId);
        disableShopProduct(shopProductId);
    }

    private void disableShopProductSku(Long shopProductId) {
        ShopProductSku shopProductSku = new ShopProductSku();
        shopProductSku.setEnabled(false);
        shopProductSku.setShopProductId(shopProductId);
        shopProductSkuService.updateByShopProductId(shopProductSku);
    }

    private void disableShopProduct(Long shopProductId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setEnabled(false);
        shopProduct.setShopProductId(shopProductId);
        shopProductService.updateByPrimaryKey(shopProduct);
    }

    private void createShopProductStatus(int status, Long productId) {
        ProductWithBLOBs product = productService.selectByPrimaryKey(productId);
        List<Sku> skuList = skuService.listSkuInfoByProductId(productId);
        if (skuList.isEmpty()) {
            LOGGER.error("Fail to get sku of product_id [{}]", productId);
            // throw exception?
        }
        Long shopProductId = createShopProduct(status, product, skuList.get(0));
        insertShopProductSkus(skuList, shopProductId);

    }

    private Long createShopProduct(int status, ProductWithBLOBs product, Sku sku) {
        ShopProductWithBLOBs shopProduct = new ShopProductWithBLOBs();
        shopProduct.setEnabled(true);
        shopProduct.setShopId(shopId);
        shopProduct.setStatus((byte) status);
        shopProduct.setProductId(product.getProductId());
        shopProduct.setName(product.getName());
        shopProduct.setShopCategoryId(product.getCategoryId());
        shopProduct.setCoverpic(product.getCoverImg());
        shopProduct.setIntroduction(product.getDescription());
        shopProduct.setMinSalePrice(sku.getImPrice());
        shopProduct.setMaxSalePrice(sku.getImPrice());
        return shopProductService.insertAndGetId(shopProduct);
    }

    private void insertShopProductSkus(List<Sku> skuList, Long shopProductId) {
        for (Sku sku : skuList) {
            ShopProductSku shopProductSku = new ShopProductSku();
            shopProductSku.setEnabled(true);
            shopProductSku.setShopProductId(shopProductId);
            shopProductSku.setSkuId(sku.getSkuId());
            shopProductSku.setName(sku.getName());
            shopProductSku.setCoverpic(sku.getCoverpic());
            shopProductSku.setIntroduction(sku.getIntroduction());
            shopProductSku.setSalePrice(sku.getImPrice());
            shopProductSku.setShopId(shopId);
            shopProductSkuService.insertSelective(shopProductSku);
        }

    }
}
