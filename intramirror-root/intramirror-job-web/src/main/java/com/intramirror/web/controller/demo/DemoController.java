package com.intramirror.web.controller.demo;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.order.api.model.ApiMq;
import com.intramirror.web.mapping.api.IMapping;
import com.intramirror.web.mapping.impl.QuadraSynProductMapping;
import com.intramirror.web.mapping.impl.XmagSynProductMapping;

import org.sql2o.Connection;

import pk.shoplus.DBConnector;
import pk.shoplus.model.Product;
import pk.shoplus.service.ProductService;
import pk.shoplus.util.RedisUtils;

/**
 * 
 * @author dingyifan
 *
 */
@Controller
@RequestMapping("/demo_test")
public class DemoController {
	
	@Autowired
	private QuadraSynProductMapping productMappingService ; 
	
	@Autowired
	private XmagSynProductMapping xmagProductMapperService;
	
	/** 测试返回对象转换JSON !!! */
	@RequestMapping(value = "/get_api_mq",method = RequestMethod.GET)
	@ResponseBody
	public ApiMq getApiMq(){
		ApiMq apiMq = new ApiMq();
		apiMq.setName("测试返回对象转换JSON !!!");
		return apiMq;
	}
	
	/** 测试乱码 !!! */
	@RequestMapping(value = "/get_string",method = RequestMethod.GET,produces="text/html;charset=UTF-8")
	@ResponseBody
	public String getString(){
		return "测试乱码";
	}
	
	/** 测试时间格式 !!! */
	@RequestMapping(value = "/get_date",method = RequestMethod.GET,produces="text/html;charset=UTF-8")
	@ResponseBody
	public Date getDate(){
		return new Date();
	}

	@RequestMapping(value = "/get_product")
	@ResponseBody
	public Product getProduct() throws Exception {
		Connection conn = DBConnector.sql2o.beginTransaction();
		new DBConnector();
		ProductService productService = new ProductService(conn);
		Product product = productService.getProductById(341L);
		return product;
	}
	
	
	@RequestMapping(value = "/create_product")
	@ResponseBody
	public void addProduct(@RequestBody Map<String,Object> mqData) throws Exception {
		productMappingService.handleMappingAndExecute(new Gson().toJson(mqData));
	}
	
	@RequestMapping(value = "/xmagAddProduct")
	@ResponseBody
	public void xmagAddProduct(@RequestBody Map<String,Object> mqData) throws Exception {
		xmagProductMapperService.handleMappingAndExecute(new Gson().toJson(mqData));
	}
	
	
}
