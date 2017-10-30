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
}
