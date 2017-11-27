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
     * 定时job修改vendor价格  update in_price
     * @return true,false
     * @throws Exception
     */
    boolean updateVendorPrice() throws Exception;

    /**
     * 定时job修改shop价格 update im_price -> shop_product_sku.sale_price
     * @return true,false
     * @throws Exception
     */
    boolean updateShopPrice() throws Exception;

    /**
     * 定时job修改admin价格 update im_price
     * @return true,false
     * @throws Exception
     */
    boolean updateAdminPrice() throws Exception;

    /**
     * 定时job修改shop_product.max_sale_price,shop_product.min_sale_price
     * @return
     * @throws Exception
     */
    boolean updateShopProductSalePrice() throws Exception;

    /**
     * 定时job修改product.retail_price
     * @return
     * @throws Exception
     */
    boolean updateProductRetailPrice() throws Exception;

    boolean updateProductBoutiquePrice() throws Exception;

    int deleteByPrimaryKey(Long priceChangeRuleId);

    int insert(PriceChangeRule record);

    int insertSelective(PriceChangeRule record);
    
    PriceChangeRule selectByPrimaryKey(Long priceChangeRuleId);

    int updateByPrimaryKeySelective(PriceChangeRule record);

    ResultMessage copyRuleByVendor(Map<String,Object> params) throws Exception;

    ResultMessage copyRuleBySeason(Map<String,Object> params) throws Exception;

    ResultMessage copyRule(Map<String,Object> params) throws Exception;
    
    List<PriceChangeRule> selectByName(String name,Long vendorId);
    
    List<Map<String,Object>> selRuleByVendorPriceType(Map<String,Object> params);

}
