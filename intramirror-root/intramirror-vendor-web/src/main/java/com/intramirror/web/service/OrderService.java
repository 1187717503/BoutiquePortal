package com.intramirror.web.service;

import java.text.MessageFormat;
import java.util.*;

import com.intramirror.order.api.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.api.service.ILogisticProductShipmentService;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.IShipmentService;

@Service
public class OrderService {
	
	private static Logger logger = Logger.getLogger(OrderService.class);
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private ILogisticsProductService iLogisticsProductService;
	
	@Autowired
	private IShipmentService iShipmentService;
	
	@Autowired
	private IContainerService containerService;
	
	@Autowired
	private ILogisticProductShipmentService logisticProductShipmentService;
	
	
	
	/**
	 * 装箱
	 * @param map :orderLineNum,status,containerId,shipment_id(可为空),vendorId
	 * @return
	 */
	@Transactional  
	public ResultMessage packingOrder(Map<String,Object> map) throws Exception{
    	logger.info(MessageFormat.format("order packingOrder 入参:{0}", new Gson().toJson(map)));
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		Map<String,Object> infoMap = new HashMap<String, Object>();
		//0 校验   2返回shipment列表  3箱子为空时  4不为空时  
		infoMap.put("statusType", StatusType.ORDER_CHECK_ORDER);
		
        String orderLineNum = map.get("orderLineNum").toString();
        Map<String,Object> currentOrder = null;
        
		Long vendorId = null;
		//根据订单状态查询订单
		/*logger.info(MessageFormat.format("order packingOrder 调用接口getOrderListByStatus 查询订单信息 入参  status:{0},vendorId:{1}",map.get("status").toString(),vendorId));
		List<Map<String,Object>> orderList = orderService.getOrderListByStatus(Integer.parseInt(map.get("status").toString()),vendorId,null);
		if(orderList ==null || orderList.size() == 0){
			result.setMsg("The current order list is empty");
			infoMap.put("code", StatusType.ORDER_ERROR_CODE);
			result.setInfoMap(infoMap);
			return result;
		}
		
		logger.info("order packingOrder 校验订单是否存在   orderLineNum:"+orderLineNum);
		for(Map<String,Object> info :orderList){
			if(orderLineNum.equalsIgnoreCase(info.get("order_line_num").toString())){
				currentOrder = info;
				result.successStatus();
				break;
			}
		}
		if(currentOrder == null){
			result.setMsg("Order does not exist");
			infoMap.put("code", StatusType.ORDER_ERROR_CODE);
			result.setInfoMap(infoMap);
			return result;
		}*/
        //校验order_line_num  是否在CONFIRMED中存在
        Map<String, Object> order = orderService.getOrderByOrderNumber(orderLineNum);
        if (order==null|| !"2".equals(String.valueOf(order.get("status")))){
            result.setMsg("Order does not exist");
            infoMap.put("code", StatusType.ORDER_ERROR_CODE);
            result.setInfoMap(infoMap);
            return result;
        }else {
            currentOrder = order;
            vendorId = Long.parseLong(order.get("vendor_id").toString());
        }
        //封装参数
		Map<String, Object> conditionMap = new HashMap<>();
		long containerId = Long.parseLong(map.get("containerId").toString());
		conditionMap.put("container_id", containerId);
		conditionMap.put("status", OrderStatusType.READYTOSHIP);
		currentOrder.put("containerId", containerId);
		currentOrder.put("vendorId",vendorId);
		
		//检查判断该箱子是否存在订单
		logger.info(MessageFormat.format("order packingOrder 获取箱子内订单列表 判断是否为空箱  iLogisticsProductService.selectByCondition 入参:{0}",new Gson().toJson(conditionMap)));
		List<LogisticsProduct> list = iLogisticsProductService.selectByCondition(conditionMap);
		String shipment_id = map.get("shipment_id").toString();
		
		//获取箱子信息
		Map<String, Object> selectContainer = new HashMap<>();
		selectContainer.put("container_id", containerId);
		Container container =  containerService.selectContainerById(selectContainer);
		
		//如果为空箱子，并且已经选择过shipMent 则直接关联，并加入箱子
		if(StringUtils.isNoneBlank(shipment_id) && (list == null || list.size() == 0)){
			logger.info("order packingOrder 已经选择过shipMent 直接关联，并加入箱子 ");
			infoMap.put("statusType", StatusType.ORDER_CONTAINER_EMPTY);
			
			//判断箱子的geography 跟订单的大区是否一致
			if(checkShipToGeography(currentOrder, container)){
				result.setMsg("This Order's ship-to geography is different than carton's");
				infoMap.put("code", StatusType.ORDER_ERROR_CODE);
				result.setInfoMap(infoMap);
				return result;
			}
			//判断箱子的stockLocation和订单是否一致
			if(checkStockLocation(currentOrder, container)){
				result.setMsg("This Order's stock_location is different than carton's");
				infoMap.put("code", StatusType.ORDER_ERROR_CODE);
				result.setInfoMap(infoMap);
				return result;
			}
			//箱子关联Shipment
			Map<String, Object> updateContainer = new HashMap<String, Object>(); 
			updateContainer.put("shipment_id",shipment_id);
			updateContainer.put("container_id", Long.parseLong(map.get("containerId").toString()));
			logger.info("order packingOrder 箱子关联Shipment 调用接口 containerService.updateContainerByCondition 入参"+new Gson().toJson(updateContainer));
			int row = containerService.updateContainerByCondition(updateContainer);
			
			//关联成功，则往箱子里存入订单
			if(row > 0 ){
				Map<String, Object> shipMentMap = new HashMap<>();
				//根据订单大区选择的Shipment   所以只需要用订单的大区即可(只有箱子为空时)
				//shipMentMap.put("ship_to_geography", currentOrder.get("pack_english_name").toString());
				shipMentMap.put("ship_to_geography", container.getShipToGeography());
				shipMentMap.put("shipment_id", Long.parseLong(shipment_id));
				shipMentMap.put("ship_from_location_id", container.getShipFromLocationId());
				//获取当前ShipMent 第一段的物流类型(不需要  空箱子不比较shipmentType 直接放入)
				try {
					//订单加入箱子
					logger.info("order packingOrder updateLogisticsProduct");
					result = updateLogisticsProduct(currentOrder, shipMentMap, false, true);
				}catch(RuntimeException re){
					logger.error(re.getMessage());
					throw new RuntimeException(re.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new RuntimeException("The order failed to be added to the carton.");
				}
			}else{
				result.setMsg("The modification failed. Check that the parameters are correct ");
				infoMap.put("code", StatusType.ORDER_ERROR_CODE);
				result.setInfoMap(infoMap);
			}
			logger.info("已经选择过shipMent 直接关联，并加入箱子 end");
			return result;
		}
		//如果是新箱子，则需要关联Shipment,如果存在符合条件的Shipment有多个则返回列表供选择,如果只有一个则默认存入，没有则需要新建Shipment
		if(list == null || list.size() == 0){
			infoMap.put("statusType", StatusType.ORDER_CONTAINER_EMPTY);
			//if (checkPack(result, infoMap, currentOrder, container)) return result;
			//创建新箱子不用校验stockLocation
			//判断箱子的geography 跟订单的大区是否一致
			if(checkShipToGeography(currentOrder, container)){
				result.setMsg("This Order's ship-to geography is different than carton's");
				infoMap.put("code", StatusType.ORDER_ERROR_CODE);
				result.setInfoMap(infoMap);
				return result;
			}

			Map<String, Object> selectShipmentParam = new HashMap<>();
			//selectShipmentParam.put("shipToGeography", currentOrder.get("pack_english_name").toString());
			selectShipmentParam.put("shipToGeography", container.getShipToGeography());

			//shipment 状态
			selectShipmentParam.put("status", ContainerType.OPEN);
			selectShipmentParam.put("vendorId", vendorId);
			selectShipmentParam.put("shipFromLocationId",currentOrder.get("ship_from_location_id"));
			
			//查询shipment
			logger.info("order packingOrder 查询shipment 列表  iShipmentService.getShipmentsByVendor 入参:"+new Gson().toJson(selectShipmentParam));
			List<Map<String, Object>> shipmentMapList = iShipmentService.getShipmentsByVendor(selectShipmentParam);
			
			//如果为空  新建Shipment
			if(shipmentMapList == null || shipmentMapList.size() == 0  ){
				logger.info("order packingOrder shipmentMapList is null ");
				Map<String, Object> saveShipmentParam = new HashMap<>();
				saveShipmentParam.put("orderNumber", orderLineNum);
				saveShipmentParam.put("shipment_id", 0);

				//获取需要的参数
				Map<String, Object> orderResult = orderService.getShipmentDetails(saveShipmentParam);
				//默认为0
				orderResult.put("shipmentId", 0);
				//如果是发往质检仓
				if("Transit Warehouse".equals(container.getShipToGeography())){
					orderResult.put("shipment_category",1);
				}else {
					orderResult.put("shipment_category",2);
				}
				//接口返回shipmentId
				logger.info("order packingOrder 添加sub_shipment物流信息   调用接口   iShipmentService.saveShipmentByOrderId 入参:"+new Gson().toJson(orderResult));
				orderResult.put("pack_english_name",container.getShipToGeography());
				Shipment shipment = iShipmentService.saveShipmentByOrderId(orderResult);
				if (shipment != null && shipment.getShipmentId() != null){
					Long shipmentId = shipment.getShipmentId();
					//箱子关联Shipment
					Map<String, Object> updateContainer = new HashMap<>();
					updateContainer.put("shipment_id", shipmentId);
					updateContainer.put("container_id", Long.parseLong(map.get("containerId").toString()));
					updateContainer.put("ship_from_location_id",shipment.getShipFromLocationId());
					logger.info("order packingOrder 箱子关联Shipment 调用接口 containerService.updateContainerByCondition 入参"+new Gson().toJson(updateContainer));
					int updateContainerRow = containerService.updateContainerByCondition(updateContainer);
					
					//关联成功，则往箱子里存入订单
					if(updateContainerRow > 0 ){
						Map<String, Object> shipMentMap = new HashMap<>();
						//根据订单大区创建的Shipment   所以只需要用订单的大区即可(只有箱子为空时)
						//shipMentMap.put("ship_to_geography", currentOrder.get("pack_english_name").toString());
						shipMentMap.put("ship_to_geography", container.getShipToGeography());
						shipMentMap.put("shipment_id", shipmentId);
						shipMentMap.put("ship_from_location_id", shipment.getShipFromLocationId());
						try {
							//订单加入箱子(已经调用过saveShipmentByOrderId 方法  不需要再次创建)
							result =  updateLogisticsProduct(currentOrder,shipMentMap,false,false);
						}catch(RuntimeException re){
							logger.error(re.getMessage());
							throw new RuntimeException(re.getMessage());
						} catch (Exception e) {
							logger.error(e.getMessage());
							throw new RuntimeException("The order failed to be added to the carton.");
						}
					}else{
						logger.info("order packingOrder 调用containerService.updateContainerByCondition 箱子关联Shipment失败   入参:"+new Gson().toJson(updateContainer));
						throw new RuntimeException("error updateContainer fail");
					}
				}else{
					//生成shipment 失败
					result.setInfoMap(infoMap);
					logger.error("order packingOrder 调用iShipmentService.saveShipmentByOrderId 返回shipmentId为空  入参:"+ new Gson().toJson(orderResult));
					throw new RuntimeException("error create shipment fail");
				}
				//如果匹配的Shipment 只存在一个,就直接关联箱子   并把订单存入箱子
				}else if(shipmentMapList.size() == 1){
					logger.info("shipmentMapList size 1  start  ");
					
					//箱子关联Shipment
					Map<String, Object> updateContainer = new HashMap<>();
					updateContainer.put("shipment_id", Long.parseLong(shipmentMapList.get(0).get("shipment_id").toString()));
					updateContainer.put("container_id", Long.parseLong(map.get("containerId").toString()));
					updateContainer.put("ship_from_location_id", shipmentMapList.get(0).get("ship_from_location_id"));
					logger.info("order packingOrder 箱子关联Shipment 调用接口 containerService.updateContainerByCondition 入参"+new Gson().toJson(updateContainer));
					int row = containerService.updateContainerByCondition(updateContainer);
					
					//关联成功，则往箱子里存入订单
					if(row > 0 ){
						Map<String, Object> shipMentMap = shipmentMapList.get(0);
						//获取当前ShipMent 第一段的物流类型(不需要  空箱子不比较shipmentType 直接放入)
						try {
							//订单加入箱子
							result =  updateLogisticsProduct(currentOrder,shipMentMap,false,true);
						}catch(RuntimeException re){
							logger.error(re.getMessage());
							throw new RuntimeException(re.getMessage());
						} catch (Exception e) {
							logger.error(e.getMessage());
							throw new RuntimeException("The order failed to be added to the carton.");
						}
					}else{
						logger.info("order packingOrder 箱子关联Shipment 失败 ");
						result.setInfoMap(infoMap);
						throw new RuntimeException("error updateContainer fail");
					}
				//如果匹配的Shipment 存在多个，则返回列表供选择
				}else if(shipmentMapList.size() > 1){
					logger.info("order packingOrder shipmentMapList 存在多条,返回列表供选择");
					result.setData(shipmentMapList);
					infoMap.put("statusType", StatusType.ORDER_QUERY_LIST);
					result.setInfoMap(infoMap);
				}
		//如果箱子中存在订单，则直接加入箱子
		}else{
			logger.info("order packingCheckOrder 箱子不为空    ");
			infoMap.put("statusType", StatusType.ORDER_CONTAINER_NOT_EMPTY);
			
			Map<String, Object> getShipment = new HashMap<>();
			getShipment.put("shipmentId", Long.parseLong(shipment_id));
			
			//根据shipmentId 获取shipment 相关信息及物流第一段类型
			logger.info("order packingOrder 获取shipment 及第一段物流信息   调用接口 iShipmentService.getShipmentTypeById 入参:"+new Gson().toJson(getShipment));
			Map<String, Object> shipmentMap = iShipmentService.getShipmentTypeById(getShipment);
			
			if(shipmentMap != null ){
				try {
					//订单加入箱子
					result =  updateLogisticsProduct(currentOrder,shipmentMap,true,true);
				}catch(RuntimeException re){
					logger.error(re.getMessage());
					throw new RuntimeException(re.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new RuntimeException("The order failed to be added to the carton.");
				}
			}else{
				result.setMsg("shipment Query is empty ");
				infoMap.put("code", StatusType.ORDER_ERROR_CODE);
				result.setInfoMap(infoMap);
			}
		}
		result.successStatus();
		return result;
	}

	private boolean checkStockLocation(Map<String, Object> currentOrder, Object container) {
		boolean flag = true;
		if (container != null) {
			String stock_location_id = currentOrder.get("ship_from_location_id")!=null?currentOrder.get("ship_from_location_id").toString():"0";
			Integer stockLocationId =null;
			if (container instanceof Container){
				stockLocationId = ((Container)container).getShipFromLocationId();
			}else if (container instanceof Map){
				String id =((Map<String,Object>)container).get("ship_from_location_id")!=null?((Map<String,Object>)container).get("ship_from_location_id").toString():"0";
				stockLocationId = Integer.parseInt(id);
			}
			if ( stockLocationId!= null&&stockLocationId!=0) {
				if(stock_location_id.equals(stockLocationId.toString())){
					flag = false;
				}
			}else {
				//初次添加carton不用判断
				flag = false;
			}
		}
		return flag;
	}

	//检验发货大区与订单是否一致
	private boolean checkShipToGeography(Map<String, Object> currentOrder, Object container) {
		boolean flag = true;
		if (container != null){
			String packEnglishName = currentOrder.get("pack_english_name").toString();
			String packGroup = currentOrder.get("pack_group").toString();
			String addressCountryId = currentOrder.get("country_id")!=null?
					currentOrder.get("country_id").toString():"";
            String shippingMethod = currentOrder.get("shipping_method") != null ? currentOrder.get("shipping_method").toString() : "0";
            String shipToGeography = "";
			if(container instanceof Container){
				shipToGeography = ((Container)container).getShipToGeography();
			}else if (container instanceof Map){
				shipToGeography = ((Map<String,Object>)container).get("ship_to_geography").toString();
			}
			if ("Transit Warehouse".equals(shipToGeography)){
				//如果是发往质检仓就不需要验证
				flag = false;
			}else if ("COMO".equals(shipToGeography)){
				//如果是走荷兰邮政，订单必须是到中国大陆的，而且是选定走COMO运输的
				if ("2".equals(addressCountryId)&&"2".equals(shippingMethod)){
					flag = false;
				}
			}else if("1".equals(packGroup)){
				//如果是去往China excl. Taiwan
				if("China excl. Taiwan".equals(shipToGeography)){
					flag = false;
				}
			}else {
				if (shipToGeography.equals(packEnglishName)){
					flag = false;
				}
			}

			//如果是走荷兰邮政的，不能直发其他大区
            if ("2".equals(shippingMethod)){
			    if (!"COMO".equals(shipToGeography)){
			        //如果不是发往COMO就报错
                    flag = true;
                }
            }
		}
		return flag;
	}


	/**
	 * 订单装箱
	 * @param orderMap
	 * @param shipMentMap
	 * @param ischeck
	 * @param isSaveSubShipment
	 * @return
	 * @throws Exception
	 */
	@Transactional  
	public ResultMessage updateLogisticsProduct(Map<String,Object> orderMap,Map<String,Object> shipMentMap,boolean ischeck,boolean isSaveSubShipment) throws Exception{
		logger.info(MessageFormat.format("order updateLogisticsProduct 订单装箱 入参信息   orderMap:{0},shipMentMap:{1},ischeck:{2},isSaveSubShipment:{3}",new Gson().toJson(orderMap),new Gson().toJson(shipMentMap),ischeck,isSaveSubShipment));
		ResultMessage result= new ResultMessage();
		Map<String, Object> info = new HashMap<>();
		if(ischeck){
			info.put("statusType", StatusType.ORDER_CONTAINER_NOT_EMPTY);
		}else{
			info.put("statusType", StatusType.ORDER_CONTAINER_EMPTY);
		}
		result.errorStatus();
		
		//校验该订单跟箱子所属的Shipment的目的地是否一致,一致则加入,是否分段运输，发货员自行判断
		logger.info("order updateLogisticsProduct 装箱校验  1.大区是否一致 2.是否为空箱子 3.shipment_type");
		//如果大区不一致，直接返回
		if(ischeck && checkShipToGeography(orderMap,shipMentMap)){
			//result.setMsg("This Order's consignee address is different than carton's. ");
			//info.put("code", StatusType.ORDER_ERROR_CODE);
			//result.setInfoMap(info);
			//return result;
			throw new RuntimeException("This Order's consignee address is different than carton's. ");
		}
		/*else{
			//shipment_type 用于判断该箱子是否能存放多个，状态为1 只能存放一个  所以不能在存入
			if(ischeck && shipMentMap.get("shipment_type_id").toString().equals("1")){
				throw new RuntimeException("Only one Order can be packed in this carton.");
			}
		}*/

		//校验该订单跟箱子所属的Shipment的发货地是否一致,一致则加入
		if(checkStockLocation(orderMap,shipMentMap)){
			//result.setMsg("This Order's stock location is different than carton's. ");
			//info.put("code", StatusType.ORDER_ERROR_CODE);
			//result.setInfoMap(info);
			//return result;
			throw new RuntimeException("This Order's stock location is different than carton's. ");
		}

		long shipmentId = Long.parseLong(shipMentMap.get("shipment_id").toString());
		if (shipMentMap.get("shipment_type_id")!=null){
			if (shipMentMap.get("shipment_type_id").toString().equals("2")){
				//按用户收货地址校验打包
				SubShipment subShipment = iShipmentService.getSubShipmentByShipmentId(shipmentId);
				Map<String, Object> saveShipmentParam = new HashMap<>();
				saveShipmentParam.put("orderNumber", orderMap.get("order_line_num"));
				Map<String, Object> map = orderService.getShipmentDetails(saveShipmentParam);

				if (subShipment != null){
					String consignee = map.get("consignee") == null ? " " : map.get("consignee").toString();
					if (!subShipment.getPersonName().equalsIgnoreCase(consignee)){
						throw new RuntimeException("The personName is not consistent with the receiver address information.");
					}
					String shipToAddr = map.get("shipToAddr") == null ? " " : map.get("shipToAddr").toString();
					if (!subShipment.getShipToAddr().equalsIgnoreCase(shipToAddr)){
						throw new RuntimeException("The shipToAddr is not consistent with the receiver address information.");
					}
					if (!subShipment.getShipToEamilAddr().equals("shipment@intramirror.com")){
						throw new RuntimeException("The shipToEamilAddr is not consistent with the receiver address information.");
					}
					String shipToCity = map.get("shipToCity") == null ? " " : map.get("shipToCity").toString();
					if (!subShipment.getShipToCity().equalsIgnoreCase(shipToCity)){
						throw new RuntimeException("The shipToCity is not consistent with the receiver address information.");
					}
					String countryCode = map.get("countryCode") == null ? " " : map.get("countryCode").toString();
					if (!subShipment.getShipToCountryCode().equalsIgnoreCase(countryCode)){
						throw new RuntimeException("The countryCode is not consistent with the receiver address information.");
					}
					String postalCode = map.get("zip_code") == null ? "" : map.get("zip_code").toString();
					if (!subShipment.getPostalCode().equalsIgnoreCase(postalCode)){
						throw new RuntimeException("The postalCode is not consistent with the receiver address information.");
					}
					String contact = map.get("contact") == null ? "" : map.get("contact").toString();
					contact = contact.replace(" ","");
					if (!subShipment.getContact().replace(" ","").equalsIgnoreCase(contact)){
						throw new RuntimeException("The contact is not consistent with the receiver address information.");
					}
				}
			}

			//shipment_type 用于判断该箱子是否能存放多个，状态为1 只能存放一个  所以不能在存入
			if ("1".equals(shipMentMap.get("shipment_type_id").toString())){
				throw new RuntimeException("Only one Order can be packed in this carton.");
			}

		}


		//添加订单跟箱子的关联,并修改状态为READYTOSHIP
		logger.info("order updateLogisticsProduct 添加订单与箱子的关联  ");
		long containerId = Long.parseLong(orderMap.get("containerId").toString());
		long logisticsProductId = Long.parseLong(orderMap.get("logistics_product_id").toString());
		LogisticsProduct logisticsProduct = new LogisticsProduct();
		logisticsProduct.setLogistics_product_id(logisticsProductId);
		logisticsProduct.setContainer_id(containerId);
		logisticsProduct.setStatus(OrderStatusType.READYTOSHIP);
		logisticsProduct.setPacked_at(Helper.getCurrentUTCTime());
		logger.info("order updateLogisticsProduct 添加订单与箱子的关联 并修改状态  调用接口iLogisticsProductService.updateByLogisticsProduct 订单修改前状态:"+orderMap.get("status").toString()+"  入参:"+new Gson().toJson(logisticsProduct));
		int row = iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);
		//添加订单跟箱子的关联,与wms系统关联
		long vendorId = Long.parseLong(orderMap.get("vendorId").toString());
		String orderLineNum = orderMap.get("order_line_num").toString();
		LogisticProductContainer productContainer = new LogisticProductContainer();
		productContainer.setContainerId(containerId);
		productContainer.setLogisticsProductId(logisticsProductId);
		productContainer.setShipmentId(shipmentId);
		productContainer.setRecieveStatus(1);
		productContainer.setOrderLineNum(orderLineNum);
		productContainer.setVendorId(vendorId);
		productContainer.setCreateTime(new Date());
		productContainer.setUpdateTime(new Date());
		LogisticProductContainer logisticProductContainer = iLogisticsProductService.getLogisticProductContainer(productContainer);
		if (logisticProductContainer==null){
			iLogisticsProductService.insertLogisticProductContainer(productContainer);
		}

		if(row > 0){
			result.successStatus();
			
			Map<String, Object> saveShipmentParam = new HashMap<>();
			saveShipmentParam.put("orderNumber", orderLineNum);
			saveShipmentParam.put("shipmentId", shipmentId);
			Map<String, Object> orderResult = orderService.getShipmentDetails(saveShipmentParam);
			orderResult.put("shipmentId", shipmentId);
			orderResult.put("shipment_category",shipMentMap.get("shipment_category"));
            orderResult.put("pack_english_name",shipMentMap.get("ship_to_geography"));
			if(isSaveSubShipment){
				//添加第三段物流
				logger.info("order updateLogisticsProduct 添加sub_shipment物流信息   调用接口   iShipmentService.saveShipmentByOrderId 入参:"+new Gson().toJson(orderResult));
				iShipmentService.saveShipmentByOrderId(orderResult);
			}

		}else{
			info.put("code", StatusType.ORDER_ERROR_CODE);
			result.setMsg("Package failure");
		}
		result.setInfoMap(info);
		return result;
	}
	
	
	
	/**
	 * 删除订单
	 * @param 
	 * @return
	 */
	@Transactional  
	public ResultMessage deleteOrder(Map<String,Object> map) throws Exception{
		logger.info(MessageFormat.format("order deleteOrder 入参:{0}", new Gson().toJson(map)));
		ResultMessage result= new ResultMessage();
		result.errorStatus();

		Long containerId = Long.parseLong(map.get("container_id").toString());
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("logistics_product_id", Long.parseLong(map.get("logistics_product_id").toString()));
		conditionMap.put("container_id", containerId);
		conditionMap.put("status", OrderStatusType.READYTOSHIP);

		//检查判断该箱子是否存在订单
		List<LogisticsProduct> list = iLogisticsProductService.selectByCondition(conditionMap);
		if(list == null || list.size() == 0){
			result.setMsg("Order does not exist ");
			return result;
		}

		//取消订单跟箱子的关联,并修改状态为CONFIRMED
		LogisticsProduct logisticsProduct = new LogisticsProduct();
		long logisticsProductId = Long.parseLong(map.get("logistics_product_id").toString());
		logisticsProduct.setLogistics_product_id(logisticsProductId);
		logisticsProduct.setContainer_id(0l);
		logisticsProduct.setStatus(OrderStatusType.COMFIRMED);
		logger.info("order deleteOrder 取消订单与箱子的关联 并修改状态  调用接口iLogisticsProductService.updateByLogisticsProduct 订单修改前状态:"+OrderStatusType.READYTOSHIP+"  入参:"+new Gson().toJson(logisticsProduct));
		int row = iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);
		//删除关联（wms系统）
		LogisticProductContainer logisticProductContainer = new LogisticProductContainer();
		logisticProductContainer.setLogisticsProductId(logisticsProductId);
		logisticProductContainer.setContainerId(containerId);
		iLogisticsProductService.updateLogisticProductContainer(logisticProductContainer);

		if(row > 0){
			//查询相关的logisProShipmentInfo 表获取sub_shipment_id
			Map<String,Object> logisProShipmentInfo = iLogisticsProductService.selectLogisProShipmentById(Long.parseLong(map.get("logistics_product_id").toString()));

			//根据sub_shipment_id 删除sub_shipment
			if(logisProShipmentInfo != null && logisProShipmentInfo.get("sub_shipment_id") != null ){
				//logger.info("order deleteOrder 删除订单相关联的 sub_shipment 调用接口  subShipmentService.deleteByPrimaryKey sub_shipment_id:"+logisProShipmentInfo.get("sub_shipment_id").toString());
				//subShipmentService.deleteByPrimaryKey(Long.parseLong(logisProShipmentInfo.get("sub_shipment_id").toString()));

				logger.info("order deleteOrder 删除订单相关联的 logisticProductShipment 调用接口  logisticProductShipmentService.deleteById sub_shipment_id:"+logisProShipmentInfo.get("sub_shipment_id").toString());
				logisticProductShipmentService.deleteById(Long.parseLong(logisProShipmentInfo.get("sub_shipment_id").toString()));
			}
			//如果当前数据为container里面的最后一个商品删除container与shipment关联
			Long vendor_id = list.get(0).getVendor_id();
			int status = OrderStatusType.READYTOSHIP;
			List<Map<String, Object>> orderMap = orderService.getOrderListByStatusAndContainerId(containerId, status, Arrays.asList(vendor_id));
			if (orderMap == null || orderMap.size() == 0){
				Map<String, Object> uMap = new HashMap<>();
				uMap.put("container_id", containerId);
				uMap.put("shipment_id", 0);
				int isfalse = containerService.updateContainerShipment(uMap);
				logger.info("update container shipment Relation:"+isfalse);
			}
			result.successStatus();
		}else{
			result.setMsg("Delete failed, please check whether the parameter is correct ");
		}

		
		return result;
	}

}
