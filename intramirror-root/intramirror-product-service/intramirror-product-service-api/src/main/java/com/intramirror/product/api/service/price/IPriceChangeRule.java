package com.intramirror.product.api.service.price;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.model.PriceChangeRule;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/17.
 */
public interface IPriceChangeRule {

    /**
     * 定时job修改vendor价格
     * @return true,false
     * @throws Exception
     */
    boolean updateVendorPrice() throws Exception;

    /**
     * 定时job修改shop价格
     * @return true,false
     * @throws Exception
     */
    boolean updateShopPrice() throws Exception;

    /**
     * 定时job修改admin价格
     * @return true,false
     * @throws Exception
     */
    boolean updateAdminPrice() throws Exception;

    int deleteByPrimaryKey(Long priceChangeRuleId);

    int insert(PriceChangeRule record);

    int insertSelective(PriceChangeRule record);
    
    PriceChangeRule selectByPrimaryKey(Long priceChangeRuleId);

    int updateByPrimaryKeySelective(PriceChangeRule record);

    ResultMessage copyRuleByVendor(Map<String,Object> params) throws Exception;

    ResultMessage copyRuleBySeason(Map<String,Object> params) throws Exception;
    
    /**
     * 根据name 模糊查询
     * @param priceChangeRuleId
     * @return
     */
    List<PriceChangeRule> selectByName(String name);

}
