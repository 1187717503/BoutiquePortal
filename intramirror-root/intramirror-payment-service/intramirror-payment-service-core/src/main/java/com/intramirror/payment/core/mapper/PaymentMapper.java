package com.intramirror.payment.core.mapper;

import com.intramirror.payment.api.model.Payment;

public interface PaymentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long paymentId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment
     *
     * @mbggenerated
     */
    int insert(Payment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment
     *
     * @mbggenerated
     */
    Long insertSelective(Payment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment
     *
     * @mbggenerated
     */
    Payment selectByPrimaryKey(Long paymentId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Payment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table payment
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Payment record);
}