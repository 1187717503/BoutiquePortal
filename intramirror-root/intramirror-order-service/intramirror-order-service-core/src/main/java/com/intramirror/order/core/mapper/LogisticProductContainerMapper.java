package com.intramirror.order.core.mapper;

import com.intramirror.order.api.model.LogisticProductContainer;
import com.intramirror.order.api.model.LogisticProductContainerExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LogisticProductContainerMapper {
    /**
     * @mbg.generated
     */
    long countByExample(LogisticProductContainerExample example);

    /**
     * @mbg.generated
     */
    int insert(LogisticProductContainer record);

    /**
     * @mbg.generated
     */
    int insertSelective(LogisticProductContainer record);

    /**
     * @mbg.generated
     */
    List<LogisticProductContainer> selectByExample(LogisticProductContainerExample example);

    /**
     * @mbg.generated
     */
    LogisticProductContainer selectByPrimaryKey(Long id);

    /**
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LogisticProductContainer record, @Param("example") LogisticProductContainerExample example);

    /**
     * @mbg.generated
     */
    int updateByExample(@Param("record") LogisticProductContainer record, @Param("example") LogisticProductContainerExample example);

    /**
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LogisticProductContainer record);

    /**
     * @mbg.generated
     */
    int updateByPrimaryKey(LogisticProductContainer record);
}