package com.intramirror.order.core.mapper;

import com.intramirror.order.api.model.MemberPointsErrorLog;
import com.intramirror.order.api.model.MemberPointsErrorLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberPointsErrorLogMapper {
    /**
     * @mbg.generated
     */
    long countByExample(MemberPointsErrorLogExample example);

    /**
     * @mbg.generated
     */
    int insert(MemberPointsErrorLog record);

    /**
     * @mbg.generated
     */
    int insertSelective(MemberPointsErrorLog record);

    /**
     * @mbg.generated
     */
    List<MemberPointsErrorLog> selectByExample(MemberPointsErrorLogExample example);

    /**
     * @mbg.generated
     */
    MemberPointsErrorLog selectByPrimaryKey(Long id);

    /**
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") MemberPointsErrorLog record, @Param("example") MemberPointsErrorLogExample example);

    /**
     * @mbg.generated
     */
    int updateByExample(@Param("record") MemberPointsErrorLog record, @Param("example") MemberPointsErrorLogExample example);

    /**
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(MemberPointsErrorLog record);

    /**
     * @mbg.generated
     */
    int updateByPrimaryKey(MemberPointsErrorLog record);
}