package com.intramirror.order.core.impl;

import com.intramirror.order.api.service.ITaxService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticsProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by caowei on 2018/11/12.
 */
@Service
public class TaxServiceImpl extends BaseDao implements ITaxService {

    private LogisticsProductMapper logisticsProductMapper;

    @Override
    public void init() {
        logisticsProductMapper = this.getSqlSession().getMapper(LogisticsProductMapper.class);
    }

    @Override
    public BigDecimal calculateTax(String orderLineNum) {
        if (orderLineNum != null){
            //获取收件国家id和发件国家id
            Map<String, Integer> countryId = logisticsProductMapper.queryCountryId(orderLineNum);
        }
        return null;
    }
}
