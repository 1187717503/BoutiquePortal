package com.intramirror.web.junit.product;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.controller.xmag.XmagController;
import com.intramirror.web.junit.BaseJunit4Test;
import com.intramirror.web.mapping.impl.XmagAllStockMapping;

public class XmagAllStockJunitTest extends BaseJunit4Test {
	
	@Autowired
	private XmagAllStockMapping xmagAllStockMapping ; 
	
	@Autowired
	private XmagController controller;

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
    	Map<String,Object> mqData = new HashMap<String, Object>();
    	Map<String,Object> data = new HashMap<String, Object>();
    	
    	mqData.put("Barcode", "200521494544");
        mqData.put("Qty", "1");
      
        data.put("store_code", "");
        data.put("vendor_id", 19);
        data.put("api_configuration_id", 100);
        data.put("product", mqData);
        System.out.println("Test assume");  
        System.out.println(new Gson().toJson(mqData));
        System.out.println(new Gson().toJson(data));
//        Map<String,Object> resultMap = xmagAllStockMapping.handleMappingAndExecute(new Gson().toJson(data));
//        System.out.println("-----------"+new Gson().toJson(resultMap));
//        Assert.assertEquals(StatusType.SUCCESS,resultMap.get("status"));
        
        controller.getProductByDate("XmagSynProductByDate");
        controller.getProducts("XmagSynProduct");
        controller.getAllStock("XmagAllStock");
//        throw new RuntimeException("error create product fail");
    }  
}
