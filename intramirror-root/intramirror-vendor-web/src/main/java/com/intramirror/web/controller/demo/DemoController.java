package com.intramirror.web.controller.demo;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intramirror.order.api.model.ApiMq;

/**
 * 
 * @author dingyifan
 *
 */
@Controller
@RequestMapping("/demo_test")
public class DemoController {
	
	/** 测试返回对象转换JSON !!! */
	@RequestMapping(value = "/get_api_mq",method = RequestMethod.GET,produces="text/html;charset=UTF-8")
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
}
