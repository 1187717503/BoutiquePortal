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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.service.IShipmentService;

/**
 * @author 袁孟亮
 *
 */
@Controller
@RequestMapping("/shipment")
public class ShipmentController {

	private static Logger logger = LoggerFactory.getLogger(ShipmentController.class);
	
	@Autowired
	private IShipmentService iShipmentService;
	
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
			if(map.get("consigner_country_id") == null || StringUtils.isBlank(map.get("consigner_country_id").toString())){
				message.successStatus().putMsg("info", "consigner_country_id cannot be null");
				return message;
			}
			if(map.get("consignee_country_id") == null || StringUtils.isBlank(map.get("consignee_country_id").toString())){
				message.successStatus().putMsg("info", "consignee_country_id cannot be null");
				return message;
			}
			if(map.get("vendor_id") == null || StringUtils.isBlank(map.get("vendor_id").toString())){
				message.successStatus().putMsg("vendor_id", "status cannot be null");
				return message;
			}
			int result = iShipmentService.saveShipmentByOrderId(map);
			if (result != 0){
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
	
	
	
	@RequestMapping(value="/getShipmentByStatus", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage getShipmentByStatus(@RequestBody Map<String, Object> map){
		logger.info("getShipment param : " +new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0==map.size()){
				logger.info("parameter cannot be null");
				message.errorStatus().putMsg("error Message", "parameter is null");
			 	return message;
			}
			if(map.get("shipToCountry") == null || StringUtils.isBlank(map.get("shipToCountry").toString())){
				message.successStatus().putMsg("info", "shipToCountry cannot be null");
				return message;
			}
			if(map.get("status") == null || StringUtils.isBlank(map.get("status").toString())){
				message.successStatus().putMsg("info", "status cannot be null");
				return message;
			}
			List<Map<String, Object>> resultMap = iShipmentService.selectShipmentByOrder(map);
			if (null != resultMap){
				message.successStatus().putMsg("info","SUCCESS").setData(resultMap);
				return message;
			}
			message.errorStatus().putMsg("info","SUCCESS").setData(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error Mssage : " + e.getMessage() );
			message.errorStatus().putMsg("error Message", e.getMessage());
		}
		
		return message;
	}
	
	
	
	
}
