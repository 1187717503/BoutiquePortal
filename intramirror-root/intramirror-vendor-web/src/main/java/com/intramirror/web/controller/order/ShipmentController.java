/**
 *
 */
package com.intramirror.web.controller.order;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.model.SubShipment;
import com.intramirror.order.api.service.*;
import com.intramirror.order.api.util.HttpClientUtil;
import com.intramirror.order.api.vo.ShipmentSendMailVO;
import com.intramirror.order.api.vo.ShippedParam;
import com.intramirror.user.api.model.User;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.order.api.vo.ShipmentInputVO;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pk.shoplus.common.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

	@Autowired
	private IContainerService containerService;

	@Autowired
	private ILogisticsProductService logisticsProductService;

	@Autowired
	private ISubShipmentService subShipmentService;

	@Autowired
	private ILogisticProductShipmentService logisticProductShipmentService;

	@Autowired
	private KafkaUtilService kafkaUtilService;

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
			if (null == orderResult || 0==orderResult.size()){
				message.errorStatus().putMsg("info", "order result null");
				return message;
			}
			orderResult.put("shipmentId", map.get("shipmentId").toString());
			//新的入参
			Shipment result = iShipmentService.saveShipmentByOrderId(orderResult);
			if (result!=null){
				message.successStatus().putMsg("info","SUCCESS").setData(result.getShipmentId());
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
				logger.info("shipmentId cannot be null");
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


	@RequestMapping(value="/updateShipmentById", method=RequestMethod.POST)
	@ResponseBody
	@Transactional
	public ResultMessage updateShipmentById(@RequestBody Map<String, Object> map,HttpServletRequest httpRequest){
		logger.info("getShipmentTypeById parameter : "+new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0 == map.size()){
				logger.info("parameter cannot be null");
				message.errorStatus().putMsg("Info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("shipmentId") || StringUtils.isBlank(map.get("shipmentId").toString())){
				logger.info("shipmentId cannot be null");
				message.errorStatus().putMsg("Info", "shipmentId cannot be null");
				return message;
			}
			if (null == map.get("status") || StringUtils.isBlank(map.get("status").toString())){
				logger.info("status cannot be null");
				message.errorStatus().putMsg("Info", "status cannot be null");
				return message;
			}
			int result = iShipmentService.updateShipmentStatus(map);
			if (0==result){
				message.errorStatus().putMsg("Info", "update fail").setData(0);
				return message;
			}
			updateContainerStatus(map);

			message.successStatus().putMsg("Info", "SUCCESS").setData(1);

			//如果shipment操作reopen,需要删除awb
			if (1 == Long.parseLong(map.get("status").toString())){
				Shipment shipment = iShipmentService.selectShipmentById(map);
				SubShipment dhlShipment = null;
				if (shipment!=null){
					map.put("sequence", 1);
					dhlShipment = subShipmentService.getDHLShipment(map);
				}
				if (dhlShipment!=null){
					if (StringUtil.isNotEmpty(dhlShipment.getAwbNum())){
						User user = this.getUser(httpRequest);
						if (user == null) {
							message.setMsg("Please log in again");
							return message;
						}
						Map<String,Object> params = new HashMap<>();
						params.put("awbNo",dhlShipment.getAwbNum());
						params.put("requestorName",user.getUsername());
						params.put("reason","001");
						String s;
						try{
							s = HttpClientUtil.httpPost(JsonTransformUtil.toJson(params), HttpClientUtil.deleteAWBUrl);
						}catch (Exception e){
							logger.error("request fail,params={},url={}",JsonTransformUtil.toJson(params),HttpClientUtil.deleteAWBUrl);
							message.setMsg("DHL service invocation failed");
							return message;
						}
						if (StringUtil.isEmpty(s)){
							logger.error("deleteAWB fail");
							message.errorStatus().putMsg("Info", "deleteAWB fail");
							return message;
						}
						params.put("shipmentId",map.get("shipmentId"));
						params.put("awbNo","");
						subShipmentService.updateSubShipment(params);

						//删除物流表中awb单号
						iShipmentService.deleteMilestone(dhlShipment.getAwbNum());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error Message : " + e.getMessage());
			message.errorStatus().putMsg("error Message", e.getMessage());
		}
		return message;
	}

	private void updateContainerStatus(Map<String, Object> map) {
		//状态修改成功修改container状态
		logger.info("update container status");
		List<Map<String, Object>> container = containerService.getShipmentList(map);
		for (Map<String, Object> map2 : container) {
            Map<String, Object> uMap = new HashMap<>();
            uMap.put("status", map.get("status").toString());
            uMap.put("containerId", map2.get("container_id").toString());
            logger.info("update container param" + new Gson().toJson(uMap));
            containerService.updateContainerBystatus(uMap);
            uMap = new HashMap<>();
            uMap.put("container_id", map2.get("container_id").toString());
            List<LogisticsProduct> list  = logisticsProductService.selectByCondition(uMap);
            logger.info("update LogisticsProduct param" + new Gson().toJson(uMap));
            if (3 == Long.parseLong(map.get("status").toString())){
                for (LogisticsProduct logisticsProduct : list) {
                    logger.info("update LogisticsProduct param Logistics_product_id" + logisticsProduct.getLogistics_product_id());
                    logisticsProductService.updateOrderLogisticsStatusById(logisticsProduct.getLogistics_product_id(),
                            Integer.parseInt(map.get("status").toString()));
                }
            }
        }
	}

	/**
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/newShipment", method=RequestMethod.POST)
	@ResponseBody
	@Transactional
	public ResultMessage newShipment(@RequestBody Map<String, Object> map){
		logger.info("getShipmentTypeById parameter : "+new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		try {
			if (null == map || 0 == map.size()){
				logger.info("parameter cannot be null");
				message.errorStatus().putMsg("Info", "parameter cannot be null");
				return message;
			}
			if (null == map.get("shipmentId") || StringUtils.isBlank(map.get("shipmentId").toString())){
				logger.info("shipmentId cannot be null");
				message.errorStatus().putMsg("Info", "shipmentId cannot be null");
				return message;
			}
			if (null == map.get("container_id") || StringUtils.isBlank(map.get("container_id").toString())){
				logger.info("containerId cannot be null");
				message.errorStatus().putMsg("Info", "status cannot be null");
				return message;
			}
			List<LogisticsProduct> list  = logisticsProductService.selectByCondition(map);
			Map<String, Object> paramMap = new HashMap<>();
			if (null == list || list.isEmpty()){
				logger.info("LogisticsProductlist cannot be null");
				message.errorStatus().putMsg("Info", "LogisticsProductlist result cannot be null");
				return message;
			}else{
				paramMap.put("orderNumber", list.get(0).getOrder_line_num());
				paramMap.put("shipmentId", "");
				Map<String, Object> orderResult = orderService.getShipmentDetails(paramMap);
				orderResult.put("shipmentId",map.get("shipmentId"));
				Long shipmentId = iShipmentService.newShipment(orderResult);
				if (shipmentId != null){
					Map<String, Object> containerMap = new HashMap<>();
					//获取当前container关联的subShipment
					containerMap.put("containerId", Long.parseLong(map.get("container_id").toString()));
					List<SubShipment> subList = subShipmentService.getSubShipmentIdByContainerId(containerMap);
					if (null != subList && 0 < subList.size()){
						for (SubShipment subShipment : subList) {
							containerMap = new HashMap<>();
							containerMap.put("consignee", subShipment.getConsignee());
							containerMap.put("segmentSequence", subShipment.getSegmentSequence());
							containerMap.put("shippingSegmentId", subShipment.getShippingSegmentId());
							containerMap.put("shipToAddr", subShipment.getShipToAddr());
							containerMap.put("shipToAddr2", subShipment.getShipToAddr2());
							containerMap.put("shipToAddr3", subShipment.getShipToAddr3());
							containerMap.put("shipToEamilAddr", subShipment.getShipToEamilAddr());
							containerMap.put("shipToCity", subShipment.getShipToCity());
							containerMap.put("shipToCountry", subShipment.getShipToCountry());
							containerMap.put("shipToCountryCode",subShipment.getShipToCountryCode());
							containerMap.put("shipToDistrict", subShipment.getShipToDistrict());
							containerMap.put("shipToProvince", subShipment.getShipToProvince());
							containerMap.put("postalCode", subShipment.getPostalCode());
							containerMap.put("contact",subShipment.getContact());
							containerMap.put("shipmentId", shipmentId);
							containerMap.put("status", ContainerType.RECEIVED);
							Date currentDate = new Date();
							containerMap.put("updatedAt", currentDate);
							containerMap.put("createdAt", currentDate);
							containerMap.put("newShipment", 1);
							subShipmentService.insertSubshipment(containerMap);
						}
					}

					//修改container关联
					containerMap = new HashMap<>();
					containerMap.put("shipment_id", shipmentId);
					containerMap.put("container_id", Long.parseLong(map.get("container_id").toString()));
					containerService.updateContainerShipment(containerMap);

					containerMap = new HashMap<>();
					//获取修改之后container关联的subShipment
					containerMap.put("containerId", Long.parseLong(map.get("container_id").toString()));
					List<SubShipment> newsubList = subShipmentService.getSubShipmentIdByContainerId(containerMap);
					if (null != subList && 0 < subList.size()){
						//修改logistic_product_shipment关联
						int index = 0;
						for (SubShipment subShipment : newsubList) {
							subShipment.getSubShipmentId();
							containerMap = new HashMap<>();
							containerMap.put("subShipmentId", subShipment.getSubShipmentId());
							containerMap.put("subShipmentId1", subList.get(index)==null?0:subList.get(index).getSubShipmentId());
							index++;
							logisticProductShipmentService.updateBysubShipmentId(containerMap);
						}
					}
					message.successStatus().putMsg("Info", "SUCCESS").setData(1);
					return message;
				}
				message.successStatus().putMsg("Info", "SUCCESS").setData(-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error Message : " + e.getMessage());
			message.errorStatus().putMsg("error Message", e.getMessage());
		}
		return message;
	}

	@RequestMapping(value="/shipped", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage shipped(@RequestBody ShippedParam param) {
		logger.info("shipped parameter : " + new Gson().toJson(param));
		ResultMessage message = ResultMessage.getInstance();
		message.errorStatus();
		List<String> awbNos = param.getAwbNos();
		if (null == awbNos || 0 == awbNos.size()){
			logger.info("awbNos cannot be null");
			message.errorStatus().putMsg("Info", "awbNos cannot be null");
			return message;
		}
		List<Shipment> shipmentList = null;
		//防止并发调用自动ship，导致邮件重复发送
		synchronized (this){
			shipmentList = iShipmentService.getShipmentList(awbNos);
			if (shipmentList!=null&&shipmentList.size()>0){
				for (Shipment shipment:shipmentList){
					if (3!=shipment.getStatus()){
						shippedOrder(message, shipment);
					}
				}
			}
		}
		if (shipmentList==null||shipmentList.size()<awbNos.size()){
			logger.warn("awb Number list:{}包含不存在的shipment",new Gson().toJson(awbNos));
		}
		return message;
	}

	@RequestMapping(value="/shipToComo", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage shipToComo(@RequestParam String orderLineNum) {
		logger.info("shipped orderLineNum : {}", orderLineNum);
		ResultMessage message = ResultMessage.getInstance();
		message.errorStatus();

		synchronized (this) {
			Map<String, Object> shipmentMap = iShipmentService.getShipmentByOrderLineNum(orderLineNum);
			if (shipmentMap != null) {
				Shipment shipment = (Shipment) shipmentMap.get("shipment");
				if (3 != shipment.getStatus()) {
					shippedOrder(message, shipment);
					List<String> nums = (List<String>) shipmentMap.get("orderLineNum");
					message.setData(nums);
				}
			}
		}
		return message;
	}

	@RequestMapping(value="/saveAwb", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage saveAwb(@RequestBody ShipmentInputVO inputVO) {
		logger.info("保存线下生成AWB，请求参数：{}", inputVO);
		ResultMessage message = ResultMessage.getInstance();
		message.errorStatus();
		if (StringUtil.isEmpty(inputVO.getAwbNo())){
			throw new RuntimeException("The awb number cannot be empty!");
		}
		if (inputVO.getShipmentId() == null){
			throw new RuntimeException("The shipmentId cannot be empty!");
		}
		try {
			iShipmentService.saveAwb(inputVO);
			message.successStatus();
		}catch (Exception e){
			logger.error(e.getMessage());
		}
		return message;
	}

	@Transactional
	public void shippedOrder(ResultMessage message, Shipment shipment) {
		logger.info("shipmentNo:{}，自动ship",shipment.getShipmentNo());
		List<String> list = new ArrayList<>();
		iShipmentService.shipmentToShip(shipment);
		//修改carton状态
		Map<String,Object> map = new HashMap<>();
		map.put("shipmentId",shipment.getShipmentId());
		map.put("status",3);
		updateContainerStatus(map);
		//shipped操作发送消息用来生成资金报表
		List<LogisticsProduct> logisticsProducts = iShipmentService.getLogisticsProductByShipment(shipment.getShipmentId());
		if (logisticsProducts!=null && logisticsProducts.size()>0){
            for (LogisticsProduct logisticsProduct:logisticsProducts){
                kafkaUtilService.saveOrderFinance(logisticsProduct);
                list.add(logisticsProduct.getOrder_line_num());
            }
        }

		//调用微店接口ship
		iShipmentService.styleroomShip(list);

		// 起线程发邮件
		ShipmentSendMailVO vo = new ShipmentSendMailVO();
		vo.setShipmentNo(shipment.getShipmentNo());
		vo.setShipmentId(shipment.getShipmentId());
		if (shipment.getToType() == 2) {
            vo.setDestination("Transit Warehouse");
        } else if("China excl. Taiwan".equals(shipment.getShipToGeography())) {
			vo.setDestination("China");
		}else {
			vo.setDestination(shipment.getShipToGeography());
		}
		iShipmentService.sendMailForShipped(vo);
		message.successStatus();
	}

	/**
	 * 香港和大陆买手店获取物流类型
	 * @param shipmentId
	 * @return
	 */
	@GetMapping("getLogisticsType")
	@ResponseBody
	public ResultMessage getCartoonType(@RequestParam Long shipmentId){
		ResultMessage resultMessage = new ResultMessage();
		try{
			int type = iShipmentService.getCartoonType(shipmentId);
			resultMessage.successStatus().setData(type);
		}catch(Exception e){
			resultMessage.errorStatus().addMsg(e.getMessage());
			logger.error("香港和大陆买手店获取物流类型，异常："+e.getMessage(),e);
		}
		return resultMessage;
	}

	/**
	 * 香港和大陆买手店发货
	 * @param shipmentId
	 * @param shippmentCode
	 * @param logisticsType
	 * @return
	 */
	@GetMapping("ship4hkAndMainLandVendor")
	@ResponseBody
	public ResultMessage ship4hkAndMainLandVendor(@RequestParam(value = "shipmentId",required = true)Long shipmentId,
										@RequestParam(value = "shippmentCode",required = true)String shippmentCode,
										@RequestParam(value = "logisticsType",required = true)Integer logisticsType){
		ResultMessage resultMessage = new ResultMessage();
		try{
			iShipmentService.ship4hkAndMainLandVendor(shipmentId,shippmentCode,logisticsType);
			resultMessage.successStatus();
		}catch(Exception e){
			resultMessage.errorStatus().addMsg(e.getMessage());
			logger.error("香港和大陆买手店发货，异常："+e.getMessage(),e);
		}
		return resultMessage;
	}


}
