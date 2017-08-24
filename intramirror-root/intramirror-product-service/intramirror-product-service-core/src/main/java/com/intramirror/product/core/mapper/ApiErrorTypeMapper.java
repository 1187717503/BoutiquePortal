package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.ApiErrorType;

public interface ApiErrorTypeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_error_type
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long apiErrorTypeId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_error_type
     *
     * @mbggenerated
     */
    int insert(ApiErrorType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_error_type
     *
     * @mbggenerated
     */
    int insertSelective(ApiErrorType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_error_type
     *
     * @mbggenerated
     */
    ApiErrorType selectByPrimaryKey(Long apiErrorTypeId);

    ApiErrorType selectByName(String errorTypeName);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_error_type
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(ApiErrorType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_error_type
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ApiErrorType record);
}