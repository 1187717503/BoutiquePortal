/**
 * 
 */
package com.intramirror.order.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.ShipmentMapper;

/**
 * 订单装箱service
 * @author yuan
 *
 */
@Service
public class ShipmentServiceImpl extends BaseDao implements IShipmentService{

	private static Logger logger = LoggerFactory.getLogger(ShipmentServiceImpl.class);
	
	private ShipmentMapper shipmentMapper;
	@Override
	public void init() {
		shipmentMapper = this.getSqlSession().getMapper(ShipmentMapper.class);
	}
	
	/**
	 * Confirmed的Order生成Shipment 新的Shipment默认有一个carton
	 */
	@Override
	public Shipment saveShipmentByOrderId(Shipment shipment) {
		return shipmentMapper.saveShipmentByOrderId(shipment);
	}


}
