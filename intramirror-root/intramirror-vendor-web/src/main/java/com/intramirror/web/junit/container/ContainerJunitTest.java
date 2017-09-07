package com.intramirror.web.junit.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.web.controller.order.ContainerController;
import com.intramirror.web.junit.BaseJunit4Test;

public class ContainerJunitTest extends BaseJunit4Test {
	
	@Autowired
	private ContainerController containerController;

	@Before  
	public void setUp() throws Exception {
		System.out.println("setUp");
	}

	@After  
	public void tearDown() throws Exception {
		System.out.println("tearDown");
	}
	
    @Test  
    public void testAssume(){
    	Map<String, Object> saveMap = new HashMap<>();
    	saveMap.put("shipmentId", 179);
    	saveMap.put("shipToGeography", "European Union");
    	ResultMessage SaveMessage = containerController.saveContainer(saveMap);
    	System.out.println(SaveMessage.getData());
    	
    	Map<String, Object> updateMap = new HashMap<>();
    	updateMap.put("length", 200);
    	updateMap.put("width", 200);
    	updateMap.put("height", 200);
    	updateMap.put("barcode", "CTN1000002");
    	ResultMessage updateMessage = containerController.updateContainerBybarcode(updateMap);
    	System.out.println(updateMessage.getData());
    	
    	
    	Map<String, Object> barcodeMap = new HashMap<>();
    	barcodeMap.put("barcode", "CTN1000002");
    	barcodeMap.put("status", 1);
    	ResultMessage barcodeMessage = containerController.getContainerBybarcode(barcodeMap);
    	System.out.println(barcodeMessage.getData());
    	
    	
    	Map<String, Object> noMap = new HashMap<>();
    	noMap.put("barcode", "CTN1000002");
    	noMap.put("status", 1);
    	ResultMessage noMessage = containerController.getShipmentNo(noMap);
    	System.out.println(noMessage.getData());
    	
    	
    	Map<String, Object> updatecsMap = new HashMap<>();
    	updatecsMap.put("containerId", 311);
    	updatecsMap.put("status", 2);
    	ResultMessage updatecsMessage = containerController.updateContainerStatus(updatecsMap);
    	System.out.println(updatecsMessage.getData());
    	
    	
    	Map<String, Object> deleteMap = new HashMap<>();
    	deleteMap.put("containerId", 3111);
    	ResultMessage delelteMessage = containerController.updateContainerStatus(deleteMap);
    	System.out.println(delelteMessage.getData());
    	
    	ResultMessage getBarcodeMessage = containerController.getBarcode();
    	System.out.println(getBarcodeMessage.getData());
    	
    	Map<String, Object> printBarcodeMap = new HashMap<>();
    	printBarcodeMap.put("barcode", "CTN1000002");
    	ResultMessage printBarcodeMessage = containerController.printBarcode(printBarcodeMap);
    	System.out.println(printBarcodeMessage.getData());
    	
    	Map<String, Object> AllShipment = new HashMap<>();
    	AllShipment.put("shipmentId", 179);
    	ResultMessage AllShipmentMessage = containerController.getContainersByShipmentId(AllShipment);
    	System.out.println(AllShipmentMessage.getData());
    	
    	
    	Map<String, Object> updateAllContainer= new HashMap<>();
    	List<Map<String, Object>> list= new ArrayList<>();
    	Map<String, Object> map= new HashMap<>();
    	map.put("container_id", 1);
    	map.put("length", 179);
    	map.put("width", 179);
    	list.add(map);
    	updateAllContainer.put("containerList", list);
    	System.out.println(new Gson().toJson(updateAllContainer));
    	ResultMessage updateAllContainerMessage = containerController.updateAllContainer(updateAllContainer);
    	System.out.println(updateAllContainerMessage.getData());
    	
    }  
}
