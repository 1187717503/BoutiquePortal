package com.intramirror.order.core.mapper;

import com.intramirror.order.api.model.PaymentOffline;

public interface PaymentOfflineMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment_offline
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long paymentOfflineId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment_offline
     *
     * @mbggenerated
     */
    int insert(PaymentOffline record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment_offline
     *
     * @mbggenerated
     */
    int insertSelective(PaymentOffline record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment_offline
     *
     * @mbggenerated
     */
    PaymentOffline selectByPrimaryKey(Long paymentOfflineId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment_offline
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PaymentOffline record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment_offline
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PaymentOffline record);
}