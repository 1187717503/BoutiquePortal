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
				message.errorStatus().putMsg("info", "consigner_country_id cannot be null");
				return message;
			}
			if(map.get("consignee_country_id") == null || StringUtils.isBlank(map.get("consignee_country_id").toString())){
				message.errorStatus().putMsg("info", "consignee_country_id cannot be null");
				return message;
			}
			if(map.get("vendor_id") == null || StringUtils.isBlank(map.get("vendor_id").toString())){
				message.errorStatus().putMsg("info", "vendor_id cannot be null");
				return message;
			}
			if(map.get("shipToProvince") == null || StringUtils.isBlank(map.get("shipToProvince").toString())){
				message.errorStatus().putMsg("info", "shipToProvince cannot be null");
				return message;
			}
			if(map.get("shipToCity") == null || StringUtils.isBlank(map.get("shipToCity").toString())){
				message.errorStatus().putMsg("info", "shipToCity cannot be null");
				return message;
			}
			if(map.get("shipToDistrict") == null || StringUtils.isBlank(map.get("shipToDistrict").toString())){
				message.errorStatus().putMsg("info", "shipToDistrict cannot be null");
				return message;
			}
			if(map.get("shipToAddr") == null || StringUtils.isBlank(map.get("shipToAddr").toString())){
				message.errorStatus().putMsg("info", "shipToAddr cannot be null");
				return message;
			}
			if(map.get("shipToGeography") == null || StringUtils.isBlank(map.get("shipToGeography").toString())){
				message.errorStatus().putMsg("info", "shipToGeography cannot be null");
				return message;
			}
			if(map.get("consignee") == null || StringUtils.isBlank(map.get("consignee").toString())){
				message.errorStatus().putMsg("info", "consignee cannot be null");
				return message;
			}
			if(map.get("shipToCountry") == null || StringUtils.isBlank(map.get("shipToCountry").toString())){
				message.errorStatus().putMsg("info", "shipToCountry cannot be null");
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
	
	
	/**
	 * 按国家查询当前status的shipment
	 * @param map
	 * @return
	 */
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
			//shipment状态参数校验
			if(map.get("status") == null || StringUtils.isBlank(map.get("status").toString())){
				message.successStatus().putMsg("info", "status cannot be null");
				return message;
			}
			if(map.get("shipToCountry") == null || StringUtils.isBlank(map.get("shipToCountry").toString())){
				message.successStatus().putMsg("info", "shipToCountry cannot be null");
				return message;
			}
			List<Map<String, Object>> resultMap = iShipmentService.getShipmentByStatus(map);
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
	
	/**
	 * 查询第一段shipment type
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/selectShipmentByOrder", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage selectShipmentByOrder(@RequestBody Map<String, Object> map){
		logger.info("selectShipmentByOrder param : " +new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0==map.size()){
				logger.info("selectShipmentByOrder cannot be null");
				message.errorStatus().putMsg("error Message", "parameter is null");
			 	return message;
			}
			//查询shipmentType参数校验
			if(map.get("consigner_country_id") == null || StringUtils.isBlank(map.get("consigner_country_id").toString())){
				message.successStatus().putMsg("info", "status cannot be null");
				return message;
			}
			if(map.get("consignee_country_id") == null || StringUtils.isBlank(map.get("consignee_country_id").toString())){
				message.successStatus().putMsg("info", "status cannot be null");
				return message;
			}
			if(map.get("vendor_id") == null || StringUtils.isBlank(map.get("vendor_id").toString())){
				message.successStatus().putMsg("info", "status cannot be null");
				return message;
			}
			Map<String, Object> resultMap = iShipmentService.selectShipmentByOrder(map);
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
