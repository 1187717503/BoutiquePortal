package com.intramirror.product.api.service;

import com.intramirror.product.api.model.ApiMq;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/8/24.
 */
public interface IApiMqService {
    List<ApiMq> getMqs();

    int updateByMqName(ApiMq apiMq);

    List<Map<String,Object>> selectMqByName(String mqName);

}
