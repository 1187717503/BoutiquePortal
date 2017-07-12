package com.intramirror.product.core.apimq.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.intramirror.product.api.apimq.IApiMqService;
import com.intramirror.product.api.model.ApiMq;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ApiMqMapper;

@Service
public class ApiMqServiceImpl extends BaseDao implements IApiMqService {

    private static Logger logger = LoggerFactory.getLogger(ApiMqServiceImpl.class);

    private ApiMqMapper apiMqMapper;

    public void init() {
        apiMqMapper = this.getSqlSession().getMapper(ApiMqMapper.class);
    }

    public ApiMq getApiMq() {
        ApiMq apiMq = apiMqMapper.selectByPrimaryKey(6L);
        logger.info("apiMq:{}",new Gson().toJson(apiMq));
        return apiMq;
    }

}
