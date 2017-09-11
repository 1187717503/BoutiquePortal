package com.intramirror.main.core.impl;


import com.intramirror.common.Helper;
import com.intramirror.main.api.service.TaxService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.TaxMapper;
import com.intramirror.product.core.mapper.SkuMapper;
import com.intramirror.user.core.mapper.VendorMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class TaxServiceImpl extends BaseDao implements TaxService {

    private TaxMapper taxMapper;


    public void init() {
        taxMapper = this.getSqlSession().getMapper(TaxMapper.class);
    }


    @Override
    public List<Map<String, Object>> getTaxRateListById(String fromCountryId, String taxType) {
        return taxMapper.getTaxRateListById(fromCountryId, taxType);
    }

    @Override
    public List<Map<String, Object>> getTaxByCategoryId(String taxType, String categoryIds) {
        return taxMapper.getTaxByCategoryId(taxType, categoryIds);
    }
}
