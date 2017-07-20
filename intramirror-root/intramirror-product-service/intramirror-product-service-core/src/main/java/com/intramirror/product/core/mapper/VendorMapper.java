package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Vendor;

public interface VendorMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table vendor
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long vendorId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table vendor
     *
     * @mbggenerated
     */
    int insert(Vendor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table vendor
     *
     * @mbggenerated
     */
    int insertSelective(Vendor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table vendor
     *
     * @mbggenerated
     */
    Vendor selectByPrimaryKey(Long vendorId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table vendor
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Vendor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table vendor
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(Vendor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table vendor
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Vendor record);
}