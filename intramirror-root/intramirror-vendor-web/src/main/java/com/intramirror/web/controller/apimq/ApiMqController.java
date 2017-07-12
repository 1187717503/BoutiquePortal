package com.intramirror.web.controller.apimq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.intramirror.product.api.apimq.IApiMqService;
import com.intramirror.product.api.model.ApiMq;
import com.intramirror.web.common.CommonsProperties;

@Controller
@RequestMapping("/apimq")
public class ApiMqController {
	
	@Autowired
	private CommonsProperties commonProperties;

	@Autowired
	private IApiMqService iApiMqService;
	
	@RequestMapping("/test01")
	public ModelAndView test01(){
		ModelAndView mv = new ModelAndView("/test/test.ftl");
		return mv;
	}
	
	@RequestMapping("/test02")
	@ResponseBody
	public String test02(){
		return commonProperties.getBaseUrl();
	}

	@RequestMapping("/test03")
	@ResponseBody
	public String test03(){
		ApiMq apiMq = iApiMqService.getApiMq();
		return new Gson().toJson(apiMq);
	}
}
