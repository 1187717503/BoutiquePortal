package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.SearchCondition;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/10/30.
 *
 * @author YouFeng.Zhu
 */
public interface ProductManagementMapper {

    List<Map<String, Object>> listProductDetailInfo(SearchCondition searchCondition);

    Map<String, Object> getProductStateByProductId(Long product_id);

    List<Map<String, Object>> listPriceByProductList(List<Map<String, Object>> products);

    List<Map<String, Object>> listAllProductCountGounpByState(SearchCondition searchCondition);

    List<Map<String, Object>> listProductStateByProductIds(List<Long> productIds);

    List<Map<String, Object>> listProductException(List<Map<String,Object>> productIds);

    List<Map<String, Object>> listShopProductIdMappingByProductIds(List<Long> productIds);

    Integer countBoutiqueException(Integer type);

}
