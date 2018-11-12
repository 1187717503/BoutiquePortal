package com.intramirror.main.core.impl;


import com.intramirror.main.api.model.Tax;
import com.intramirror.main.api.service.TaxService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.TaxMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class TaxServiceImpl extends BaseDao implements TaxService {

    private static final Logger logger = LoggerFactory.getLogger(TaxServiceImpl.class);

    private TaxMapper taxMapper;


    public void init() {
        taxMapper = this.getSqlSession().getMapper(TaxMapper.class);
    }


    @Override
    public List<Map<String, Object>> getTaxRateListById(String fromCountryId, String taxType) {
        return taxMapper.getTaxRateListById(fromCountryId, taxType);
    }

    @Override
    public List<Map<String, Object>> getTaxByCategoryId(String taxType, String[] categoryIds) {
        return taxMapper.getTaxByCategoryId(taxType, categoryIds);
    }

    @Override
    public Tax getTaxByAddressCountryId(Long addressCountryId) {
        List<Tax> taxByAddressCountryId = taxMapper.getTaxByAddressCountryId(addressCountryId);
        if(taxByAddressCountryId != null && taxByAddressCountryId.size() > 0){
            return taxByAddressCountryId.get(0);
        }

        return new Tax();
    }

    @Override
    public BigDecimal calculateTax(String orderLineNum) {
        if (orderLineNum != null){
            //获取收件国家id和发件国家id
            Map<String, Object> countryInformation = taxMapper.queryCountryId(orderLineNum);
            if (countryInformation != null){
                String consignorCountryId = countryInformation.get("consignorCountryId").toString();
                String consignorGeographyId = countryInformation.get("consignorGeographyId").toString();
                String consigneeCountryId = countryInformation.get("consigneeCountryId").toString();
                String consigneeGeographyId = countryInformation.get("consigneeGeographyId").toString();
                BigDecimal taxRate = countryInformation.get("taxRate") == null ? new BigDecimal(0) : new BigDecimal(countryInformation.get("taxRate").toString());
                logger.info("orderLineNum：{},发货国家id：{}，发货国家大区：{}，收件国家id：{}，收件国家大区：{}，发货国家税率：{}"
                        ,orderLineNum,consignorCountryId,consignorGeographyId,consigneeCountryId,consigneeGeographyId,taxRate);
                if ("3".equals(consignorGeographyId)){
                    //如果发货国家是欧盟大区
                    if ("3".equals(consigneeGeographyId)){
                        //如果收件国家是欧盟大区
                        return taxRate;
                    }else {
                        //如果不是欧盟大区
                        //不需要税
                        return new BigDecimal(0);
                    }
                }else {
                    //如果发货国家不是欧盟大区
                    if (consigneeCountryId.equals(consignorCountryId)){
                        //如果发货国和收货国是同一国家
                        return taxRate;
                    }else {
                        //如果发货国和收货国不是同一国家
                        //不需要税
                        return new BigDecimal(0);
                    }
                }
            }
        }
        return new BigDecimal(0);
    }
}
