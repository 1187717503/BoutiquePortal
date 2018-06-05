package com.intramirror.product.api.service.price;

import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.model.ImPriceAlgorithm;
import com.intramirror.product.api.model.PriceChangeRule;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/17.
 */
public interface IPriceChangeRule {

    /**
     * 定时job修改vendor价格  update in_price
     *
     * @return true, false
     * @throws Exception
     */
    boolean updateVendorPrice(int categoryType, String startTime, String endTime) throws Exception;

    boolean updateVendorPrice(Long vendor_id, int categoryType, Long price_change_rule_id) throws Exception;

    /**
     * 定时job修改shop价格 update im_price -> shop_product_sku.sale_price
     *
     * @return true, false
     * @throws Exception
     */
    boolean updateShopPrice() throws Exception;

    /**
     * 定时job修改admin价格 update im_price
     *
     * @return true, false
     * @throws Exception
     */
    boolean updateAdminPrice(int categoryType, String startTime, String endTime) throws Exception;

    boolean updateAdminPrice(Long vendor_id, int categoryType, Long price_change_rule_id) throws Exception;

    void updateDefaultPrice(PriceChangeRuleEnum.PriceType priceType, Map<String, Object> paramsMap);

    List<Map<String, Object>> selectNowActiveRule(Map<String, Object> params);

    /**
     * 手动触发修改 product.preview_im_price
     *
     * @return true, false
     * @throws Exception
     */
    boolean updatePreviewPrice(Long vendor_id, Long preview_status, Integer category_type, Long price_change_rule_id, String flag) throws Exception;

    public String checkSeasonExists(Map<String, Object> params, String season);

    /**
     * 定时job修改shop_product.max_sale_price,shop_product.min_sale_price
     *
     * @return
     * @throws Exception
     */
    boolean updateShopProductSalePrice() throws Exception;

    boolean updateProductImPrice() throws Exception;

    /**
     * 定时job修改product.retail_price
     *
     * @return
     * @throws Exception
     */
    boolean updateProductRetailPrice() throws Exception;

    boolean updateProductBoutiquePrice() throws Exception;

    int deleteByPrimaryKey(Long priceChangeRuleId);

    int insert(PriceChangeRule record);

    int insertSelective(PriceChangeRule record);

    int updateSkuImPrice();

    PriceChangeRule selectByPrimaryKey(Long priceChangeRuleId);

    int updateByPrimaryKeySelective(PriceChangeRule record);

    ResultMessage copyRuleByVendor(Map<String, Object> params) throws Exception;

    ResultMessage copyRuleBySeason(Map<String, Object> params) throws Exception;

    ResultMessage copyRule(Map<String, Object> params) throws Exception;

    List<PriceChangeRule> selectByName(String name, Long vendorId);

    List<Map<String, Object>> selRuleByVendorPriceType(Map<String, Object> params);

    int deleteSnapshot(Long price_change_rule_id);

    List<ImPriceAlgorithm> selectAlgorithmsByConditions(Map<String, Object> params);
    ImPriceAlgorithm getAlgorithmById(Long id);

    /**
     * boutique pending 页面 open / close preview
     * @param vendor_id
     * @param preview_status
     * @param category_type
     * @param price_change_rule_id
     * @param flag
     * @return
     * @throws Exception
     */
    boolean updatePreviewPriceByBoutique(Long vendor_id, Long preview_status, Integer category_type, Long price_change_rule_id, String flag) throws Exception;
}
