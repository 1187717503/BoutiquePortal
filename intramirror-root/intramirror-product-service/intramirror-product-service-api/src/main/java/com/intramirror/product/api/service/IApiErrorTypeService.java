package com.intramirror.product.api.service;

import com.intramirror.product.api.model.ApiErrorType;

/**
 * Created by dingyifan on 2017/8/24.
 */
public interface IApiErrorTypeService {
    ApiErrorType selectByName(String errorTypeName);

    int insert(ApiErrorType record);
}
