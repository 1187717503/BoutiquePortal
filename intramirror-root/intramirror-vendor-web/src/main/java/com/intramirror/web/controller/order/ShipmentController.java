/**
 * 
 */
package com.intramirror.web.controller.order;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.web.controller.BaseController;

/**
 * @author 袁孟亮
 *
 */
@CrossOrigin
@Controller
@RequestMapping("/shipment")
public class ShipmentController extends BaseController{

	private static Logger logger = LoggerFactory.getLogger(ShipmentController.class);

	@Autowired
	private IShipmentService iShipmentService;

	@Autowired
	private  IOrderService orderService;
	
	/**
	 * 保存shipment
	 * @author yuan
	 * @param map
	 * @return resuleMessage
	 */
	@RequestMapping(value="/saveShipment", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage saveShipmentByVendor(@RequestBody Map<String, Object> map){
		logger.info("svaeShipment param : " +new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0==map.size()){
				logger.info("parameter cannot be null");
				message.errorStatus().putMsg("error Message", "parameter is null");
			 	return message;
			}
			if(map.get("orderNumber") == null || StringUtils.isBlank(map.get("orderNumber").toString())){
				message.errorStatus().putMsg("info", "orderNumber cannot be null");
				return message;
			}
			if(map.get("shipmentId") == null || StringUtils.isBlank(map.get("shipmentId").toString())){
				message.errorStatus().putMsg("info", "shipmentId cannot be null");
				return message;
			}	
			Map<String, Object> orderResult = orderService.getShipmentDetails(map);
			//新的入参
			String result = iShipmentService.saveShipmentByOrderId(orderResult);
			if (StringUtils.isNotBlank(result)){
				message.successStatus().putMsg("info","SUCCESS").setData(result);
				return message;
			}
			message.errorStatus().putMsg("info","SUCCESS").setData(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error Mssage : " + e.getMessage() );
			message.errorStatus().putMsg("error Message", e.getMessage());
		}
		
		return message;
	}
	
	/**
	 * 根据大区，vendorId查询shipment列表
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/getShipmentList", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage getShipmentList(@RequestBody Map<String, Object> map){
		logger.info("getShipmentList parameter : "+new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0 == map.size()){
				logger.info("parameter cannot be null");
				message.errorStatus().putMsg("Info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("vendorId") || StringUtils.isBlank(map.get("vendorId").toString())){
				logger.info("vendorId cannot be null");
				message.errorStatus().putMsg("Info", "vendorId cannot be null");
				return message;
			}
			if (null == map.get("shipToGeography") || StringUtils.isBlank(map.get("shipToGeography").toString())){
				logger.info("shipToGeography cannot be null");
				message.errorStatus().putMsg("Info", "shipToGeography cannot be null");
				return message;
			}
			if (null == map.get("status") || StringUtils.isBlank(map.get("status").toString())){
				logger.info("status cannot be null");
				message.errorStatus().putMsg("Info", "status cannot be null");
				return message;
			}
			List<Map<String, Object>> list = iShipmentService.getShipmentsByVendor(map);
			logger.info("result :" + new Gson().toJson(list));
			if (null == list || 0==list.size()){
				message.errorStatus().putMsg("Info", "result is null").setData("result null");
				return message;
			}
			message.successStatus().putMsg("Info", "SUCCESS").setData(list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error Message : " + e.getMessage());
			message.errorStatus().putMsg("error Message", e.getMessage());
		}
		return message;
	}
	
	/**
	 * 根据shipmentId 查询 shipmentType
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/getShipmentTypeById", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage getShipmentTypeById(@RequestBody Map<String, Object> map){
		logger.info("getShipmentTypeById parameter : "+new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0 == map.size()){
				logger.info("parameter cannot be null");
				message.errorStatus().putMsg("Info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("shipmentId") || StringUtils.isBlank(map.get("shipmentId").toString())){
				logger.info("vendorId cannot be null");
				message.errorStatus().putMsg("Info", "shipmentId cannot be null");
				return message;
			}
			Map<String, Object> resultMap = iShipmentService.getShipmentTypeById(map);
			logger.info("result :" + new Gson().toJson(resultMap));
			if (null == resultMap || 0 == map.size()){
				message.errorStatus().putMsg("Info", "result is null").setData("result null");
				return message;
			}
			message.successStatus().putMsg("Info", "SUCCESS").setData(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error Message : " + e.getMessage());
			message.errorStatus().putMsg("error Message", e.getMessage());
		}
		return message;
	}
	
}
