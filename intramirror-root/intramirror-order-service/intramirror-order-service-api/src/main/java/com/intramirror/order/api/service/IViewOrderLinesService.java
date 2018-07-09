/**
 * 
 */
package com.intramirror.order.api.service;

import com.intramirror.order.api.vo.ViewOrderLinesVO;

import java.util.List;

/**
 * @author 123
 *
 */
public interface IViewOrderLinesService {

	/**
	 * 获取运单数据
	 * @param shipmentNo
	 * @return
	 */
	List<ViewOrderLinesVO> getShipmentListByShipmentNo(String shipmentNo);
}
