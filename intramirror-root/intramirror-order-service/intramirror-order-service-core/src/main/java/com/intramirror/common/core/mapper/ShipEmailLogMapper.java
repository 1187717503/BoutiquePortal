package com.intramirror.common.core.mapper;

import java.util.List;

import com.intramirror.order.api.model.ShipEmailLog;
import com.intramirror.order.api.model.ShipEmailLogExample;
import org.apache.ibatis.annotations.Param;

public interface ShipEmailLogMapper {
    /**
     * @mbg.generated
     */
    long countByExample(ShipEmailLogExample example);

    /**
     * @mbg.generated
     */
    int insert(ShipEmailLog record);

    /**
     * @mbg.generated
     */
    int insertSelective(ShipEmailLog record);

    /**
     * @mbg.generated
     */
    List<ShipEmailLog> selectByExample(ShipEmailLogExample example);

    /**
     * @mbg.generated
     */
    ShipEmailLog selectByPrimaryKey(Long id);

    /**
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") ShipEmailLog record, @Param("example") ShipEmailLogExample example);

    /**
     * @mbg.generated
     */
    int updateByExample(@Param("record") ShipEmailLog record, @Param("example") ShipEmailLogExample example);

    /**
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ShipEmailLog record);

    /**
     * @mbg.generated
     */
    int updateByPrimaryKey(ShipEmailLog record);
}