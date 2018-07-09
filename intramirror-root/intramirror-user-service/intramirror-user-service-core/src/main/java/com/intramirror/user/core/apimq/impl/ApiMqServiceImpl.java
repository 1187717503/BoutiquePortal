//package com.intramirror.user.core.apimq.impl;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import com.google.gson.Gson;
//import com.intramirror.user.api.apimq.IApiMqService;
//import com.intramirror.user.api.model.UserApiMq;
//import com.intramirror.user.core.dao.BaseDao;
//import com.intramirror.user.core.mapper.UserApiMqMapper;
//
//@Service("userApiMqService")
//public class ApiMqServiceImpl extends BaseDao implements IApiMqService {
//
//    private static Logger logger = LoggerFactory.getLogger(ApiMqServiceImpl.class);
//
//    private UserApiMqMapper apiMqMapper;
//
//    public void init() {
//        apiMqMapper = this.getSqlSession().getMapper(UserApiMqMapper.class);
//    }
//
//    public UserApiMq getApiMq() {
//        UserApiMq apiMq = apiMqMapper.selectByPrimaryKey(6L);
//        logger.info("apiMq:{}",new Gson().toJson(apiMq));
//        return apiMq;
//    }
//
//}
