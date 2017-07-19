package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Brand;

import java.util.List;
import java.util.Map;

public interface BrandMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table brand
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long brandId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table brand
     *
     * @mbggenerated
     */
    int insert(Brand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table brand
     *
     * @mbggenerated
     */
    int insertSelective(Brand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table brand
     *
     * @mbggenerated
     */
    Brand selectByPrimaryKey(Long brandId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table brand
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Brand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table brand
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(Brand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table brand
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Brand record);

    List<Map<String,Object>> selBrandByConditions(Brand brand);
}