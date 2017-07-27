package com.intramirror.product.api.service;

import com.intramirror.product.api.model.SystemProperty;

import java.util.List;
import java.util.Map;

public interface ISystemPropertyService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_property
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long systemPropertyId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_property
     *
     * @mbggenerated
     */
    int insert(SystemProperty record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_property
     *
     * @mbggenerated
     */
    int insertSelective(SystemProperty record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_property
     *
     * @mbggenerated
     */
    SystemProperty selectByPrimaryKey(Long systemPropertyId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_property
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(SystemProperty record);
    
    /**
     * 
     * 根据name 修改value值
     */
    int updateByNameSelective(SystemProperty record);
    

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_property
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(SystemProperty record);

    List<Map<String,Object>> selectSystemProperty() throws Exception;

}
