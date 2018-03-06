package com.intramirror.order.core.mapper;

import com.intramirror.order.api.model.ProductPropertyVO;

import java.util.List;
import java.util.Map;

/**
 * Created by caowei on 2018/3/6.
 */
public interface ProductPropertyMapper {

    List<ProductPropertyVO> getProductProperty(Map<String,Object> map);
}
