package com.intramirror.product.api.service.price;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.model.PriceChangeRule;

import java.util.Map;

/**
 * Created by dingyifan on 2017/7/17.
 */
public interface IPriceChangeRule {
    boolean updateVendorPrice() throws Exception;

    boolean updateShopPrice() throws Exception;

    boolean updateAdminPrice() throws Exception;

    int deleteByPrimaryKey(Long priceChangeRuleId);

    int insert(PriceChangeRule record);

    int insertSelective(PriceChangeRule record);
    
    PriceChangeRule selectByPrimaryKey(Long priceChangeRuleId);

    int updateByPrimaryKeySelective(PriceChangeRule record);

    ResultMessage copyRule(Map<String,Object> params);
}
