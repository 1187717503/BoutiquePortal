package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.ApiErrorProcessing;
import com.intramirror.product.api.service.IApiErrorProcessingService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ApiErrorProcessingMapper;
import org.springframework.stereotype.Service;

/**
 * Created by dingyifan on 2017/8/24.
 */
@Service
public class ApiErrorProcessingServiceImpl extends BaseDao implements IApiErrorProcessingService {

    private ApiErrorProcessingMapper apiErrorProcessingMapper;

    @Override
    public void init() {
        apiErrorProcessingMapper = this.getSqlSession().getMapper(ApiErrorProcessingMapper.class);
    }

    @Override
    public int insert(ApiErrorProcessing record) {
        return apiErrorProcessingMapper.insert(record);
    }

    @Override
    public int deleteApiErrorProcessing(String createTime) {
        apiErrorProcessingMapper.deleteApiErrorProcessing(createTime);
        return 0;
    }

}
