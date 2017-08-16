/**
 * 
 */
package com.intramirror.web.controller.order;

import java.util.HashMap;
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
import com.intramirror.order.api.model.Container;
import com.intramirror.order.api.model.LogisticProductShipment;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.api.service.ILogisticProductShipmentService;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.order.api.service.ISubShipmentService;
import com.intramirror.web.common.BarcodeUtil;


/**
 * @author 袁孟亮
 *
 */
@CrossOrigin
@Controller
@RequestMapping("/container")
public class ContainerController {

	private static Logger logger = LoggerFactory.getLogger(ContainerController.class);
	
	@Autowired
	private IContainerService containerService;
	
	@Autowired
	private IShipmentService shipmentService;
	
	@Autowired
	private ISubShipmentService subShipmentService;
	
	@Autowired
	private ILogisticsProductService logisticProductService;
	
	@Autowired
	private ILogisticProductShipmentService logisticProductShipmentService;
	/**
	 * 新增箱子
	 * @param map
	 * @return result
	 */
	@RequestMapping(value = "/saveContainer", method = RequestMethod.POST)
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
	@RequestMapping(value = "/updateContainerBybarcode", method = RequestMethod.POST)
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
	@RequestMapping(value = "/getContainerBybarcode", method = RequestMethod.POST)
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
				message.errorStatus().putMsg("info", "SUCCESS").setData(null);
			}
			message.successStatus().putMsg("info", "SUCCESS").setData(container);
		} catch (Exception e){
			e.printStackTrace();
			logger.error(" error Message : {}", e.getMessage());
			message.errorStatus().putMsg("info", "error Message : "+e.getMessage());
		}
		return message;
	}
	
	/**
	 * 修改箱子状态
	 * @return message
	 */
	@RequestMapping(value = "/updateContainerStatus", method = RequestMethod.POST)
	@ResponseBody
	public ResultMessage updateContainerStatus(@RequestBody Map<String, Object> map){
		logger.info("updateContainerStatus param" + new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0 == map.size()){
				message.successStatus().putMsg("info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("containerId") || StringUtils.isBlank(map.get("containerId").toString())){
				message.errorStatus().putMsg("info", "container_id cannot be null");
				return message;
			}
			if (null == map.get("status") || StringUtils.isBlank(map.get("status").toString())){
				message.errorStatus().putMsg("info", "status cannot be null");
				return message;
			}
			int result = containerService.updateContainerBystatus(map);
			if (result == 1){
				//状态修改成功修改shipment状态
				logger.info("update shipment status");
				Container container = containerService.selectContainerById(map);
				Map<String, Object> uMap = new HashMap<>();
				uMap.put("status", map.get("status").toString());
				uMap.put("shipmentId", container.getShipmentId());
				logger.info("update shipment param" + new Gson().toJson(uMap));
				shipmentService.updateShipmentStatus(uMap);
				message.successStatus().putMsg("info", "SUCCESS").setData(result);
			}else{
				message.errorStatus().putMsg("info", "SUCCESS").setData(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" error Message : {}", e.getMessage());
			message.errorStatus().putMsg("info", "error Message : " +e.getMessage());
		}
		return message;
	}
	
	/**
	 * 删除箱子
	 * @param map
	 * @return
	 */
	@RequestMapping(value="deleteContainerById", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage deleteContainerById(@RequestBody Map<String, Object> map){
		logger.info("deleteContainerById param" + new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0 == map.size()){
				message.successStatus().putMsg("info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("containerId") || StringUtils.isBlank(map.get("containerId").toString())){
				message.successStatus().putMsg("info", "containerId cannot be null");
				return message;
			}
			if (null == map.get("logistic_product_id") || StringUtils.isBlank(map.get("logistic_product_id").toString())){
				message.successStatus().putMsg("info", "containerId cannot be null");
				return message;
			}
			map.put("container_id", Long.parseLong(map.get("containerId").toString()));
			Container container = containerService.selectContainerById(map);
			int result = containerService.deleteContainerById(map);
			if (result == 1){
				Map<String, Object> setShipment = new HashMap<>();
				setShipment.put("shipmentId", container.getShipmentId());
				List<Map<String, Object>> list = containerService.getShipmentList(setShipment);
				//最后一个箱子的时候删除shipment
				if (list.size() == 0){
					shipmentService.deleteShipmentById(setShipment);
					subShipmentService.deleteSubShipmentByShipmentId(setShipment);
				}else{
					List<LogisticProductShipment> lpsList = logisticProductShipmentService.selectById(map);
					if (null != lpsList && 0 < lpsList.size()){
						for (LogisticProductShipment lps : lpsList) {
							subShipmentService.deleteByPrimaryKey(lps.getSubShipmentId());
							logisticProductShipmentService.deleteById(lps.getSubShipmentId());
						}
					}
				}
				logger.info("update logisticProduct ");
				logisticProductService.updateContainerById(Long.parseLong(map.get("logistic_product_id").toString()));
				message.successStatus().putMsg("info", "SUCCESS").setData(result);
				return message;
			}
			message.errorStatus().putMsg("info", "delete fail").setData(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" error Message : {}", e.getMessage());
			message.errorStatus().putMsg("info", "error Message :" + e.getMessage());
		}
		return message;
	}
	
	@RequestMapping(value="/getBarcode", method=RequestMethod.GET)
	@ResponseBody
	public ResultMessage getBarcode(){
		ResultMessage message = ResultMessage.getInstance();
		String barCode = "CTN";
		try {
			Integer maxCode = containerService.getMaxBarcode();
			if (null == maxCode)
				maxCode = 1000001;
			else 
				maxCode ++;
			barCode+=(maxCode+1);
			message.successStatus().addMsg("info SUCCESS").setData(barCode);
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" error Message : {}", e.getMessage());
			message.errorStatus().putMsg("info", "error Message :" + e.getMessage());
		}
		return message;
	}
	
	@RequestMapping(value="/printBarcode", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage printBarcode(@RequestBody Map<String, Object> map){
		logger.info("printBarcode param" + new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0 == map.size()){
				message.errorStatus().putMsg("info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("barCode") || StringUtils.isBlank(map.get("barCode").toString())){
				message.errorStatus().putMsg("info", "barCode cannot be null");
				return message;
			}
			String orderNumberUrl = "Barcode-"+map.get("barCode").toString()+".png";
			orderNumberUrl = BarcodeUtil.generateFile(map.get("barCode").toString(), orderNumberUrl,false);
			message.successStatus().putMsg("info", "success").setData(orderNumberUrl);
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" error Message : {}", e.getMessage());
			message.errorStatus().putMsg("info", "error Message :" + e.getMessage());
		}
		return message;
	}
	
	
}
