package com.intramirror.product.api.service;

import com.intramirror.product.api.model.ProductGroup;

import java.util.List;

public interface IProductGroupService {

    List<ProductGroup> getProductGroupListByGroupTypeAndVendorId(String groupType, Long vendorId);
}
