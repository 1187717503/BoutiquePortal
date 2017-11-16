package com.intramirror.product.api.service;

import java.util.List;
import java.util.Map;

public interface IProductExceptionService {

    int saveProductException(Map<String, Object> map) throws Exception;

    int updateProductException(Map<String, Object> map) throws Exception;

    List<Map<String, Object>> selectByProductAndSkuId(Map<String, Object> map);

}
