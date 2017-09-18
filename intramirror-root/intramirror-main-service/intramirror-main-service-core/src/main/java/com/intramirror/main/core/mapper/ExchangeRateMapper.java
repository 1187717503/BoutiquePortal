package com.intramirror.main.core.mapper;

import com.intramirror.main.api.model.ExchangeRate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExchangeRateMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exchange_rate
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long exchangeRateId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exchange_rate
     *
     * @mbggenerated
     */
    int insert(ExchangeRate record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exchange_rate
     *
     * @mbggenerated
     */
    int insertSelective(ExchangeRate record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exchange_rate
     *
     * @mbggenerated
     */
    ExchangeRate selectByPrimaryKey(Long exchangeRateId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exchange_rate
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(ExchangeRate record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table exchange_rate
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ExchangeRate record);

    List<Map<String, Object>> getShipFeeByCityId(@Param("from_country") String from_country, @Param("to_country") String to_country);
}