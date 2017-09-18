package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.ShopProductSku;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShopProductSkuMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_product_sku
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long shopProductSkuId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_product_sku
     *
     * @mbggenerated
     */
    int insert(ShopProductSku record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_product_sku
     *
     * @mbggenerated
     */
    int insertSelective(ShopProductSku record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_product_sku
     *
     * @mbggenerated
     */
    ShopProductSku selectByPrimaryKey(Long shopProductSkuId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_product_sku
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(ShopProductSku record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_product_sku
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(ShopProductSku record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_product_sku
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ShopProductSku record);

    List<Map<String, Object>> getSkuByShopProductId(Long shopProductId);

    Map<String, Object> getSkuBySkuId(Long skuId);

    List<Map<String, Object>> getSkuIdByShopProductSkuId(@Param("shopProductSkuIds") String[] shopProductSkuIds);
}