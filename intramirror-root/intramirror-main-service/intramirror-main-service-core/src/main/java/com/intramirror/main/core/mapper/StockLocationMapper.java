package com.intramirror.main.core.mapper;

import com.intramirror.main.api.model.StockLocation;
import com.intramirror.main.api.vo.StockLocationVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StockLocationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_location
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long locationId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_location
     *
     * @mbggenerated
     */
    int insert(StockLocation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_location
     *
     * @mbggenerated
     */
    int insertSelective(StockLocation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_location
     *
     * @mbggenerated
     */
    StockLocation selectByPrimaryKey(Long locationId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_location
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(StockLocation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_location
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(StockLocation record);

    /**
     * 根据vendorId获取列表
     * @param vendorId
     * @return
     */
    List<StockLocation> getStockLocationByVendorId(Long vendorId);


    List<StockLocationVO> getStockLocationByVendorIds(List<Long> vendorIds);

    /**
     * 根据shipmentId获取发货地信息
     * @param shipment
     * @return
     */
    StockLocation getShipFromLocation(Long shipment);

    List<StockLocation> getStockLocationByFrom(@Param("fromLocationId") Long fromLocationId);
}