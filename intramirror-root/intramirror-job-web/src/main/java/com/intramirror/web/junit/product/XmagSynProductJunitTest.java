package com.intramirror.web.junit.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.junit.BaseJunit4Test;
import com.intramirror.web.mapping.impl.XmagSynProductMapping;

public class XmagSynProductJunitTest extends BaseJunit4Test {
	
	@Autowired
	private XmagSynProductMapping XmagSynProductMapping ; 

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
    	
    	Map<String,Object> items = new HashMap<String, Object>();
    	List<Map<String,Object>> item = new ArrayList<>();
    	Map<String,Object> item1 = new HashMap<String, Object>();;
    	
        mqData.put("product_MadeIn", "Made in Italy");
        mqData.put("product_Note", "Medusa Belt from Versace: Medusa Belt with round end, silver-tone Medusa buckle and adjustable fit.");
        mqData.put("product_detail", "Calf Leather 100% Calf Leather Made in Italy ");
        mqData.put("description", "Versace Medusa Belt");
        mqData.put("supply_price", "320");
        mqData.put("type", "ACCESSORI DONNA");
        mqData.put("product_Material", "Calf Leather 100% Calf Leather");
        mqData.put("product_name", "DCDD259_DVT2");
        mqData.put("url", "http://185.58.119.172/theapartment/ObjImg//22092016/22092016_321.jpg");
        mqData.put("product_Measure", "");
        mqData.put("product_id", "12");
        mqData.put("CarryOver", "CO");
        mqData.put("SubCategory", "");
        mqData.put("producer_id", "VERSACE");
        mqData.put("season", "079");
        mqData.put("category", "Pantaloni");
        
        item1.put("color", "D41P");
        item1.put("country_size", " ");
        item1.put("item_id", "23");
        item1.put("web", "S");
        item1.put("stock", "1");
        item1.put("item_size", "90");
        item1.put("barcode", "1000011000030");
        item1.put("last_modified", "24/03/2017");
        item1.put("base_color", "");
        item1.put("pictures", "[\"http://185.58.119.172/theapartment/ObjImg//22092016/22092016_321.jpg\"]");
        item.add(item1);
        
        items.put("number_of_items", "1");
        items.put("item", item);
        
        
        mqData.put("items", items);
        
        
        data.put("store_code", "");
        data.put("vendor_id", 17);
        data.put("api_configuration_id", 98);
        data.put("product", mqData);
        System.out.println("Test assume");  
        System.out.println(new Gson().toJson(mqData));
        System.out.println(new Gson().toJson(data));
        Map<String,Object> resultMap = XmagSynProductMapping.handleMappingAndExecute(new Gson().toJson(data));
        System.out.println("-----------"+new Gson().toJson(resultMap));
//        Assert.assertEquals(StatusType.SUCCESS,resultMap.get("status"));
//        throw new RuntimeException("error create product fail");
    }  
}
