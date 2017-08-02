/**
 * 
 */
package com.intramirror.web.controller.order;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.model.Container;
import com.intramirror.order.api.service.IContainerService;


/**
 * @author 袁孟亮
 *
 */
@Controller
@RequestMapping("/container")
public class ContainerController {

	private static Logger logger = LoggerFactory.getLogger(ContainerController.class);
	
	@Autowired
	private IContainerService containerService;
	
	/**
	 * 新增箱子
	 * @param map
	 * @return result
	 */
	@RequestMapping("/saveContainer")
	@ResponseBody
	public ResultMessage saveContainer(@RequestBody Map<String, Object> map){
		logger.info("saveContainer param " + new Gson().toJson(map));
		//创建返回对象
		ResultMessage message = ResultMessage.getInstance();
		try {
			//入参校验
			if (null == map || map.size() == 0){
				message.successStatus().putMsg("info", "Parameter cannot be null");
				return message;
			}
			if (null == map.get("length") || StringUtils.isBlank(map.get("length").toString())){
				message.successStatus().putMsg("info", "length cannot be null");
				return message;
			}
			if (null == map.get("width") || StringUtils.isBlank(map.get("width").toString())){
				message.successStatus().putMsg("info", "width cannot be null");
				return message;
			}
			if (null == map.get("height") || StringUtils.isBlank(map.get("height").toString())){
				message.successStatus().putMsg("info", "height cannot be null");
				return message;
			}
			map.put("barcode", "1123333321312321");
			int result = containerService.saveContainerByShipment(map);
			message.successStatus().putMsg("info","SUCCESS").setData(result);
		} catch (Exception e) {
			 e.printStackTrace();
            logger.error(" error message : {}", e.getMessage());
            message.errorStatus().putMsg("info","error message : " + e.getMessage());
		}
			return message;
	}
	
	/**
	 * 根据barcode修改箱子信息
	 * @param map
	 * @return resultMessage
	 */
	@RequestMapping("/updateContainerBybarcode")
	@ResponseBody
	public ResultMessage updateContainerBybarcode(@RequestBody Map<String, Object> map){
		ResultMessage message = ResultMessage.getInstance();
		logger.info("updateContainerByBarcode param" +new Gson().toJson(map));
		try {
			if (null == map || map.size() == 0){
				message.successStatus().putMsg("info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("length") || StringUtils.isBlank(map.get("length").toString())){
				message.successStatus().putMsg("info", "length cannot be null");
				return message;
			}
			if (null == map.get("width") || StringUtils.isBlank(map.get("width").toString())){
				message.successStatus().putMsg("info", "width cannot be null");
				return message;
			}
			if (null == map.get("height") || StringUtils.isBlank(map.get("height").toString())){
				message.successStatus().putMsg("info", "height cannot be null");
				return message;
			}
			int result = containerService.updateContainerBybarcode(map);
			message.successStatus().putMsg("info","SUCCESS").setData(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" error Message : {}", e.getMessage());
			message.errorStatus().putMsg("info", "error Message : "+ e.getMessage());
		}
		return message;
	}
	
	/**
	 * 根据状态条形码查询箱子
	 * @param map
	 * @return resultMessage
	 */
	@RequestMapping("/getContainerBybarcode")
	@ResponseBody
	public ResultMessage getContainerBybarcode(@RequestBody Map<String, Object> map){
		ResultMessage message = ResultMessage.getInstance();
		logger.info("getContainerBybarcode param " + new Gson().toJson(map));
		try{
			if (null == map || map.size() == 0){
				message.successStatus().putMsg("info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("barcode") || StringUtils.isBlank(map.get("barcode").toString())){
				message.successStatus().putMsg("info", "barcode cannot be null");
				return message;
			}
			if (null == map.get("status") || StringUtils.isBlank(map.get("status").toString())){
				message.successStatus().putMsg("info", "status cannot be null");
				return message;
			}
			Container container = containerService.getContainerBybarcode(map);
			if (null == container){
				message.successStatus().putMsg("info", "SUCCESS").setData(null);
			}
			message.successStatus().putMsg("info", "SUCCESS").setData(container);
		} catch (Exception e){
			e.printStackTrace();
			logger.error(" error Message : {}", e.getMessage());
			message.errorStatus().putMsg("info", "error Message : "+e.getMessage());
		}
		return message;
	}
	
}
