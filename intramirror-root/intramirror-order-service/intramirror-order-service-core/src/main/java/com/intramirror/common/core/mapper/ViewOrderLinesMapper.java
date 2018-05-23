package com.intramirror.common.core.mapper;

import com.intramirror.order.api.vo.ViewOrderLinesVO;

import java.util.List;

public interface ViewOrderLinesMapper {

    List<ViewOrderLinesVO> getShipmentListByShipmentNo(String shipmentNo);
}