/**
 * 
 */
package com.intramirror.web.junit.container;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.web.controller.order.ShipmentController;
import com.intramirror.web.junit.BaseJunit4Test;

/**
 * @author yml
 *
 */
public class ShipmentJunitTest extends BaseJunit4Test{

	@Autowired
	private ShipmentController shipmentController;
	
	@Before  
	public void setUp() throws Exception {
		System.out.println("setUp");
	}

	@After  
	public void tearDown() throws Exception {
		System.out.println("tearDown");
	}
	
	@Test
	public void Test(){
		Map<String, Object> saveMap = new HashMap<>();
		saveMap.put("orderNumber", "123122");
		saveMap.put("shipmentId", 123);
		ResultMessage saveMessage = shipmentController.saveShipmentByVendor(saveMap);
		System.out.println(saveMessage.getData());
		
		Map<String, Object> listMap = new HashMap<>();
		listMap.put("vendorId", "7");
		listMap.put("shipToGeography", "test");
		listMap.put("status", "1");
		ResultMessage list = shipmentController.getShipmentList(listMap);
		System.out.println(list.getData());
		
		
		Map<String, Object> shipmentTypeMap = new HashMap<>();
		shipmentTypeMap.put("shipmentId", 123);
		ResultMessage shipmentTypeMessage = shipmentController.getShipmentTypeById(shipmentTypeMap);
		System.out.println(shipmentTypeMessage.getData());
		
		Map<String, Object> updateShipmentMap = new HashMap<>();
		updateShipmentMap.put("shipmentId", 123);
		updateShipmentMap.put("status", 1);
		ResultMessage updateShipmentMessage = shipmentController.updateShipmentById(updateShipmentMap);
		System.out.println(updateShipmentMessage.getData());
		
		Map<String, Object> newShipmentMap = new HashMap<>();
		newShipmentMap.put("shipmentId", 123);
		newShipmentMap.put("container_id", 111);
		ResultMessage newShipmentMapMessage = shipmentController.newShipment(newShipmentMap);
		System.out.println(newShipmentMapMessage.getData());
		
		
	}
	
}
