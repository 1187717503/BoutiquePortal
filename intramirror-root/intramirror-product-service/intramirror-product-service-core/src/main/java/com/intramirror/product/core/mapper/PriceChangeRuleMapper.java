package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PriceChangeRule;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface PriceChangeRuleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long priceChangeRuleId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     * @mbggenerated
     */
    int insert(PriceChangeRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     * @mbggenerated
     */
    int insertSelective(PriceChangeRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     * @mbggenerated
     */
    PriceChangeRule selectByPrimaryKey(Long priceChangeRuleId);

    /**
     * 根据vendorId name 模糊查询
     * @param priceChangeRuleId
     * @return
     */
    List<PriceChangeRule> selectByName(Map<String, Object> map);

    List<Map<String,Object>> selectRuleInActive(Map<String,Object> map);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PriceChangeRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     * @mbggenerated
     */
    int updateByPrimaryKey(PriceChangeRule record);

    int updatePriceChangeRuleByVendorFirstCategory();

    int updatePriceChangeRuleByVendorAllCategory();

    int updatePriceChangeRuleByVendorProductGroup();

    int updatePriceChangeRuleByVendorProduct();

    int updatePriceChangeRuleByShopFirstCategory();

    int updatePriceChangeRuleByShopAllCategory();

    int updatePriceChangeRuleByShopProductGroup();

    int updatePriceChangeRuleByShopProduct();

    List<Map<String, Object>> selectActiveSeasonGroupRule(Map<String, Object> params);

    List<Map<String, Object>> selectSeasonGroupRule(Map<String, Object> params);

    List<Map<String, Object>> selectSecondCategoryRule(Map<String, Object> params);

    List<Map<String, Object>> selectAllCategoryRule(Map<String, Object> params);

    List<Map<String, Object>> selectProductGroupRule(Map<String, Object> params);

    List<Map<String, Object>> selectProductRule(Map<String, Object> params);

    int updateSkuPriceByVendor(Map<String, Object> map);

    int updateSkuPriceByAdmin(Map<String, Object> map);

    int updateProductPreviewImPrice(Map<String, Object> map);

    int updateSkuPriceByShop(List<Map<String, Object>> paramsList);

    List<Map<String, Object>> selNowRule(Map<String, Object> params);

    List<Map<String, Object>> selRuleByVendorPriceType(Map<String, Object> params);

    int updateRuleActive(Map<String, Object> params);

    int updateRuleInActive(Map<String, Object> params);

    int updateDefaultPriceByVendor(Map<String, Object> params);

    int updateDefaultPriceByAdmin(Map<String, Object> params);

    int updateSkuPriceByImPrice();

    /** 修改shop_product.min_sale_price,shop_product.max_sale_price */
    int updateShopProductSalePrice();

    /** 修改product.retail_price */
    int updateProductRetailPrice();

    /** 修改product.boutique_price */
    int updateProductBoutiquePrice();

    /** 修改product.im_price */
    int updateProductImPrice();

    /** 修改product.preview_im_price */
    int updateProductPreviewPrice(Long vendor_id);

    int updateSkuImPrice();

    int clearProductPreviewPrice(@Param(value = "vendor_id") Long vendor_id, @Param(value = "category_type") Integer category_type);

    int updatePriceChangeRulePreviewStatus(@Param(value = "vendor_id") Long vendor_id, @Param(value = "preview_status") Long preview_status,
            @Param(value = "category_type") Integer category_type);

}