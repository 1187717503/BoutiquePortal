package com.intramirror.order.core.mapper;

import com.intramirror.order.api.model.Product;
import com.intramirror.order.api.model.ProductWithBLOBs;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {
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

    String getCompositionByProductId(@Param("productId") Long productId);
}