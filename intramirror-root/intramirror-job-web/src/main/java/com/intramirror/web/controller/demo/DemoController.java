package com.intramirror.web.controller.demo;

import java.util.Date;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.order.api.model.ApiMq;
import com.intramirror.web.mapping.impl.AlDucaOrderMapping;
import com.intramirror.web.mapping.impl.QuadraSynProductMapping;
import com.intramirror.web.mapping.impl.XmagAllStockMapping;
import com.intramirror.web.mapping.impl.XmagOrderMapping;
import com.intramirror.web.mapping.impl.XmagSynProductMapping;

import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.Product;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.PriceChangeRuleService;
import pk.shoplus.service.ProductService;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.service.price.impl.PriceServiceImpl;

/**
 * 
 * @author dingyifan
 *
 */
@Controller
@RequestMapping("/demo")
public class DemoController {


	@RequestMapping(value = "/queryPriceDiscount",method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> queryPriceDiscount(@Param(value = "product_id")String product_id){

		Connection conn = null;
		try {

			conn = DBConnector.sql2o.beginTransaction();

			IPriceService priceService = new PriceServiceImpl();
			PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(conn);
			ProductService productService = new ProductService(conn);

			Product product = productService.getProductById(Long.parseLong(product_id));
			if(conn != null ){conn.close();}
			return priceChangeRuleService.getCurrentChangeRuleByProduct(product);

		} catch (Exception e) {
			e.printStackTrace();
			if(conn != null ){conn.close();}
		}
		if(conn != null ){conn.close();}
		return null;
	}
}
