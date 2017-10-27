package com.intramirror.web.controller.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.web.common.CommonProperties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.order.api.vo.InputCreateOrder;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.OrderInputCreateOrderService;
import pk.shoplus.service.request.impl.GetPostRequestService;


@Controller
@RequestMapping("/order")
public class OrderInputCreateOrderController extends BaseController {

	private static Logger logger = Logger.getLogger(OrderInputCreateOrderController.class);

	@Autowired
	private OrderInputCreateOrderService orderInputCreateOrderService;

	@Autowired
	private CommonProperties commonProperties;

	/**
	 * Wang
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/input_create_order", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> orderInputCreateOrder(@RequestBody InputCreateOrder paramMap,
			HttpServletRequest httpRequest) throws Exception {

		logger.info("/input_create_order request come.");
		Map<String, Object> results = new HashMap<>();

		User user = this.getUserInfo(httpRequest);
		if (user == null) {
			results.put("error", "Please log in again");
			return results;
		}

		// 调用service 事物管理
		try {

			logger.info("OrderInputCreateOrderController,orderInputCreateOrder,start,paramMap:"+ JSONObject.toJSONString(paramMap)+",user:"+JSONObject.toJSONString(user));
			results = orderInputCreateOrderService.orderInputCreateOrder(paramMap, user);
			logger.info("OrderInputCreateOrderController,orderInputCreateOrder,end,paramMap:"+ JSONObject.toJSONString(paramMap)+",user:"+JSONObject.toJSONString(user)+",results:"+JSONObject.toJSONString(results));

			if(results.get("status").toString().equals("1")) {
				if(results.get("sendEmailList") != null) {

					List<LogisticsProduct> sendEmailList = (List<LogisticsProduct>)results.get("sendEmailList");

					for(LogisticsProduct logisticsProduct : sendEmailList){
						GetPostRequestService getPostRequestService = new GetPostRequestService();
						String url = commonProperties.getAppOrderUrl()+"?logisticProductId="+logisticsProduct.getLogistics_product_id();
						logger.info("OrderInputCreateOrderServiceOrderInputCreateOrder,start,requestMethod,url:"+url);
						String response = getPostRequestService.requestMethod(GetPostRequestService.HTTP_POST,url,null);
						logger.info("OrderInputCreateOrderServiceOrderInputCreateOrder,end,requestMethod,url:"+url+",response:"+response);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			results.put("info", "create order fail ");
			return results;
		}
		logger.info("接口执行结束,响应数据,result:" + new Gson().toJson(results));
		return results;
	}

}
