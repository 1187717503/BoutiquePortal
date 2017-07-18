package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.ProductGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductGroupMapper {

    List<ProductGroup> getProductGroupListByGroupType(@Param("groupType") String groupType);
}