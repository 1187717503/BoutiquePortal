package com.intramirror.order.core.mapper;

import com.intramirror.order.api.model.LogisticsMilestone;
import org.apache.ibatis.annotations.Param;

public interface LogisticsMilestoneMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table logistics_milestone
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table logistics_milestone
     *
     * @mbggenerated
     */
    int insert(LogisticsMilestone record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table logistics_milestone
     *
     * @mbggenerated
     */
    int insertSelective(LogisticsMilestone record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table logistics_milestone
     *
     * @mbggenerated
     */
    LogisticsMilestone selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table logistics_milestone
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(LogisticsMilestone record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table logistics_milestone
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(LogisticsMilestone record);

    int setDeleteByOrderAndType(@Param("orderLineNum") String orderLineNum, @Param("type")int type);
}