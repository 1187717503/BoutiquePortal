package com.intramirror.product.api.service;

import com.intramirror.product.api.model.Product;
import com.intramirror.product.api.model.ProductWithBLOBs;

import java.util.List;
import java.util.Map;

public interface IProductService {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table product
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long productId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table product
     *
     * @mbggenerated
     */
    int insert(ProductWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table product
     *
     * @mbggenerated
     */
    int insertSelective(ProductWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table product
     *
     * @mbggenerated
     */
    ProductWithBLOBs selectByPrimaryKey(Long productId);

    /**
     * 根据条件查询product 信息
     */
    ProductWithBLOBs selectByParameter(ProductWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table product
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(ProductWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table product
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(ProductWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table product
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Product record);

    List<Map<String, Object>> getVendorCodeBySku(Long shopProductSkuId);

    int batchUpdateProductStatus(int status, List<Long> productIds);

    List<ProductWithBLOBs> listProductByProductIds(List<Long> productIds);

    List<Map<String, Object>> selectDayNoUpdateSum(Map<String,Object> params);
    List<Map<String, Object>> selectDayUpdateSum(Map<String,Object> params);

    List<ProductWithBLOBs> getProductByParameter(ProductWithBLOBs record);



    List<Map<String,Object>> getProductByBrandIDAndColorCode(Map<String,Object> params);

}
