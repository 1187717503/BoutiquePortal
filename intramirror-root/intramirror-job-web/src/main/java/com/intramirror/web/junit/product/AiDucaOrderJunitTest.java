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
import com.intramirror.web.junit.BaseJunit4Test;
import com.intramirror.web.mapping.impl.AlDucaOrderMapping;


public class AiDucaOrderJunitTest extends BaseJunit4Test {
	
	@Autowired
	private AlDucaOrderMapping alDucaOrderMapping ; 

	@Before  
	public void setUp() throws Exception {
		System.out.println("setUp");
	}

	@After  
	public void tearDown() throws Exception {
		System.out.println("tearDown");
	}
	
    @Test  
    public void test(){
    	Map<String,Object> data = new HashMap<String, Object>();
    
    	data.put("vendorId", "7");
    	data.put("logisticsProductId", "100");
    	data.put("mqName", "AiDucaCreateOrder");
        System.out.println(new Gson().toJson(data));
        Map<String,Object> resultMap = alDucaOrderMapping.handleMappingAndExecute(new Gson().toJson(data));
        System.out.println(resultMap);
//        Assert.assertEquals(StatusType.SUCCESS,resultMap.get("status"));
    }  
    

}
