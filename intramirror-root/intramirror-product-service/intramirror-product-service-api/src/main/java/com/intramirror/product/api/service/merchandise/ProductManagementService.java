package com.intramirror.product.api.service.merchandise;

import com.intramirror.product.api.model.SearchCondition;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/10/30.
 *
 * @author YouFeng.Zhu
 */
public interface ProductManagementService {

    Map<String, Object> getProductStateByProductId(Long product_id);

    List<Map<String, Object>> listProductService(SearchCondition searchCondition);

    void updateProductStatus(int status, Long productId);

    void updateProductStatusAndNewShopProduct(int status, int shopStatus, Long productId);

    void updateProductAndShopProductStatus(int status, int shopStatus, Long productId, Long shopProductId);

    void updateProductStatusAndDisableShopProduct(int status, Long productId, Long shopProductId);

    void batchUpdateProductStatus(int status, List<Long> productIds);

    void batchUpdateProductStatusAndNewShopProduct(int status, int shopStatus, List<Long> productIds);

    void batchUpdateProductAndShopProductStatus(int status, int shopStatus, List<Long> productIds, List<Long> shopProductId);

    void batchUpdateProductStatusAndDisableShopProduct(int status, List<Long> productIds, List<Long> shopProductId);

    List<Map<String, Object>> listPriceByProductList(List<Map<String, Object>> products);

    List<Map<String, Object>> listAllProductCountGounpByState(SearchCondition searchCondition);

    List<Map<String, Object>> listProductStateByProductIds(List<Long> productIds);

    List<Map<String, Object>> listShopProductIdMappingByProductIds(List<Long> productIds);

    //TODO: refactor try to escope as action like process , approve , on_sale , shop_process , shop_approve
    void onSale(int status, int shopStatus, Long productId, Long shopProductId);

    void batchOnSale(int status, int shopStatus, List<Long> productIds, List<Long> shopProductId);
}
