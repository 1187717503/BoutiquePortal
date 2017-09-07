package com.intramirror.web.junit.product;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.junit.BaseJunit4Test;
import com.intramirror.web.mapping.impl.QuadraSynProductMapping;
import com.intramirror.web.mapping.impl.XmagOrderMapping;


public class XmagProductJunitTest extends BaseJunit4Test {
	
	@Autowired
	private XmagOrderMapping xmagOrderMapping ; 
	

//	public QuadraProductJunitTest(String name) {
//	}

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
    	Map<String,Object> data = new HashMap<String, Object>();
    
    	data.put("vendorId", "7");
    	data.put("logistics_product_id", "100");
    	data.put("mqName", "OrderSetCheck");
        System.out.println("Test assume");  
        System.out.println(new Gson().toJson(data));
        Map<String,Object> resultMap = xmagOrderMapping.handleMappingAndExecute(new Gson().toJson(data));
        System.out.println(resultMap);
        Assert.assertEquals(StatusType.SUCCESS,resultMap.get("status"));
//        throw new RuntimeException("error create product fail");
    }  
    

}
