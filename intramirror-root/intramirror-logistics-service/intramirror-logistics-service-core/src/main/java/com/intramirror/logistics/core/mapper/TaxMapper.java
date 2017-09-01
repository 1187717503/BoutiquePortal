package com.intramirror.logistics.core.mapper;

import com.intramirror.logistics.api.model.Tax;

public interface TaxMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tax
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer taxId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tax
     *
     * @mbggenerated
     */
    int insert(Tax record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tax
     *
     * @mbggenerated
     */
    int insertSelective(Tax record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tax
     *
     * @mbggenerated
     */
    Tax selectByPrimaryKey(Integer taxId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tax
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Tax record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tax
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Tax record);

    Tax getTaxByAddressCountryId(Long addressCountryId);
}