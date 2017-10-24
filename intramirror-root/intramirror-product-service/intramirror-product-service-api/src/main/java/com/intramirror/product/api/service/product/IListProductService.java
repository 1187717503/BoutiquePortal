package com.intramirror.product.api.service.product;

import com.intramirror.product.api.model.SearchCondition;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/10/20.
 * @author YouFeng.Zhu
 */
public interface IListProductService {
    List<Map<String, Object>> listProductService(SearchCondition searchCondition);
}
