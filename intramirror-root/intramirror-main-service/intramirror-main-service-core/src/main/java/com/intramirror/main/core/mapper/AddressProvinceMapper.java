package com.intramirror.main.core.mapper;

import com.intramirror.main.api.model.AddressProvince;

import java.util.List;
import java.util.Map;

public interface AddressProvinceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table address_province
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long addressProvinceId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table address_province
     *
     * @mbggenerated
     */
    int insert(AddressProvince record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table address_province
     *
     * @mbggenerated
     */
    int insertSelective(AddressProvince record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table address_province
     *
     * @mbggenerated
     */
    AddressProvince selectByPrimaryKey(Long addressProvinceId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table address_province
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(AddressProvince record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table address_province
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(AddressProvince record);

    List<Map<String, Object>> getAddressProvinceByCountryId(Long countryId);
}