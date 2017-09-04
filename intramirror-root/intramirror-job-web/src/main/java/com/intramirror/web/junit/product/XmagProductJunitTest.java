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
//    @Transactional   //标明此方法需使用事务  
//    @Rollback(true)  //标明使用完此方法后事务回滚,true时为回滚  
    public void testAssume(){
    	Map<String,Object> mqData = new HashMap<String, Object>();
    	Map<String,Object> data = new HashMap<String, Object>();
    	
        mqData.put("orderId", "82");
        mqData.put("orderNumber", "2017042521400078100");
        mqData.put("createTime", "Apr25,201712: 13: 23PM");
        mqData.put("updatedTime", "Apr25,201712: 13: 23PM");
        mqData.put("itemsCount", "1");
        mqData.put("productCode", "6000009451");
        mqData.put("sku", "F752R01665AC34_01-S");
        mqData.put("size", "S");
        mqData.put("price", 302.4590);
        mqData.put("totalPrice", 302.4590);
        mqData.put("status", "pending");
        mqData.put("customerName", "ssads");
        mqData.put("address", "dss");
        mqData.put("area", "西城区");
        mqData.put("city", "北京");
        mqData.put("province", "北京市");
        mqData.put("country", "中国大陆");
        mqData.put("zipcode", "100000");
        mqData.put("mobilePhone", "12312312312");
        mqData.put("orderLineId", "2017042521400078100");
        mqData.put("barcode", "#");

        
        
        
        data.put("store_code", "");
        data.put("mqName", "OrderSetCheck");
        data.put("vendor_id", 7);
        data.put("orderId", 78);
        data.put("api_configuration_id", 102);
        data.put("orderMager", mqData);
        System.out.println("Test assume");  
        System.out.println(new Gson().toJson(mqData));
        Map<String,Object> resultMap = xmagOrderMapping.handleMappingAndExecute(new Gson().toJson(data));
        Assert.assertEquals(StatusType.SUCCESS,resultMap.get("status"));
//        throw new RuntimeException("error create product fail");
    }  
    

}
