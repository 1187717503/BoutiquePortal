package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.ApiErrorType;
import com.intramirror.product.api.service.IApiErrorTypeService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ApiErrorTypeMapper;
import org.springframework.stereotype.Service;

/**
 * Created by dingyifan on 2017/8/24.
 */
@Service
public class ApiErrorTypeServiceImpl extends BaseDao implements IApiErrorTypeService{

    private ApiErrorTypeMapper apiErrorTypeMapper ;

    @Override
    public void init() {
        apiErrorTypeMapper = this.getSqlSession().getMapper(ApiErrorTypeMapper.class);
    }

    @Override
    public ApiErrorType selectByName(String errorTypeName) {
        return apiErrorTypeMapper.selectByName(errorTypeName);
    }

    @Override
    public int insert(ApiErrorType record) {
        return apiErrorTypeMapper.insert(record);
    }
}
