package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.ApiMq;

import java.util.List;

public interface ApiMqMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_mq
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long apiMqId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_mq
     *
     * @mbggenerated
     */
    int insert(ApiMq record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_mq
     *
     * @mbggenerated
     */
    int insertSelective(ApiMq record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_mq
     *
     * @mbggenerated
     */
    ApiMq selectByPrimaryKey(Long apiMqId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_mq
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(ApiMq record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table api_mq
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ApiMq record);

    List<ApiMq> selectAllActiveData();
}