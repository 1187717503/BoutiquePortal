/**
 * 
 */
package com.intramirror.order.core.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.SubShipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.intramirror.common.core.mapper.SubShipmentMapper;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticProductShipmentMapper;
import com.intramirror.order.core.mapper.ShipmentMapper;

/**
 * 订单装箱service
 * @author yuan
 *
 */
@Service
public class ShipmentServiceImpl extends BaseDao implements IShipmentService{

	private static Logger logger = LoggerFactory.getLogger(ShipmentServiceImpl.class);
	
	private ShipmentMapper shipmentMapper;
	
	private SubShipmentMapper subShipmentMapper;
	
	private LogisticProductShipmentMapper logisticProductShipmentMapper;
	
	@Override
	public void init() {
		shipmentMapper = this.getSqlSession().getMapper(ShipmentMapper.class);
		subShipmentMapper = this.getSqlSession().getMapper(SubShipmentMapper.class);
		logisticProductShipmentMapper = this.getSqlSession().getMapper(LogisticProductShipmentMapper.class);
	}
	
	/**
	 * Confirmed的Order生成Shipment 新的Shipment默认有一个carton
	 */
	@Override
	public Shipment saveShipmentByOrderId(Map<String, Object> map) {
		logger.info("shimentSaveService parameter " + new Gson().toJson(map));
		//如果shipment为null创建新的shipment 如果有直接拿shipment生成SUBshipment
		Long shipmentId = Long.parseLong(map.get("shipmentId")==null?"0":map.get("shipmentId").toString());
		//默认为2发往用户
		Integer shipmentCategory = Integer.parseInt(map.get("shipment_category")==null?"2":map.get("shipment_category").toString());
		if( 1 == shipmentCategory){
			map.put("pack_english_name","Transit Warehouse");
		}
		int result = 0;
		Shipment shipment = new Shipment();
		if (shipmentId == 0){
			//获取当前时间
			Date currentDate = new Date();
			//保存对象信息
			shipment.setShipToGeography(map.get("pack_english_name")==null?" ":map.get("pack_english_name").toString());
			Long vendorId = Long.parseLong(map.get("vendor_id").toString());
			Integer stockLocationId = Integer.parseInt(map.get("stock_location_id").toString());
			String top = shipmentMapper.getVendorCodeById(vendorId);
			Map<String, Object> noMap = new HashMap<>();
			noMap.put("topName", top+"SP");
			Integer maxNo = shipmentMapper.getMaxShipmentNo(noMap);
			if (null == maxNo)
				maxNo = 1000001;
			else 
				maxNo ++;
			//生成shipmentNo
			shipment.setShipmentNo(top+"SP"+maxNo);
			shipment.setVendorId(vendorId);
			shipment.setStatus(ContainerType.OPEN);
			shipment.setStockLocationId(stockLocationId);
			shipment.setCreatedAt(currentDate);
			shipment.setUpdatedAt(currentDate);
			shipment.setShipmentCategory(shipmentCategory);
			logger.info("parameter :" + new Gson().toJson(shipment));
			result = shipmentMapper.saveShipmentByOrderId(shipment);
			logger.info("insert shipment result : " +result);
			if (result == 1){
				//根据段生成subshipment
				Map<String, Object> typeMap = addCountryNum(map, shipmentCategory);
				typeMap.put("vendor_id",vendorId);
				logger.info("getShipmentId :" + new Gson().toJson(typeMap));
				List<Map<String, Object>> listMap = shipmentMapper.getShippmentByType(typeMap);
				logger.info("result shipmentType:" + new Gson().toJson(listMap));
				shipmentId = shipmentMapper.getShipmentId(shipment);
				saveSubShipment(listMap, map,shipmentId,Long.parseLong(
						map.get("logistics_product_id")==null?"0":map.get("logistics_product_id").toString()));
				shipment.setShipmentId(shipmentId);
				return shipment;
			}
		}else{
			shipment = shipmentMapper.selectShipmentById(map);
			shipmentId = shipment.getShipmentId();
			if (null != shipment){
				Map<String, Object> typeMap = addCountryNum(map, shipment.getShipmentCategory());
				typeMap.put("vendor_id",shipment.getVendorId());
				logger.info("getShipmentId :" + new Gson().toJson(typeMap));
				List<Map<String, Object>> listMap = shipmentMapper.getShippmentByType(typeMap);
				logger.info("result shipmentType:" + new Gson().toJson(listMap));
				saveSubShipment(listMap, map,shipmentId,Long.parseLong(
						map.get("logistics_product_id")==null?"0":map.get("logistics_product_id").toString()));
				return shipment;
			}
		}
		return null;
	}

	private Map<String, Object> addCountryNum(Map<String, Object> map, Integer shipmentCategory) {
		Map<String, Object> typeMap = new HashMap<>();
		if (shipmentCategory!=null&&1 == shipmentCategory){
            //如果是发往质检仓
            typeMap.put("consigner_country_id",19);
            typeMap.put("consignee_country_id",52);
        }else {
            if (map.get("consigner_country_id") != null)
                typeMap.put("consigner_country_id", Long.parseLong(map.get("consigner_country_id").toString()));
            if (map.get("consignee_country_id") != null)
                typeMap.put("consignee_country_id", Long.parseLong(map.get("consignee_country_id").toString()));
        }
		return typeMap;
	}

	/**
	 * 根据订单号查询shipment基本信息
	 * @param map
	 * @return map
	 */
	@Override
	public Map<String, Object> selectShipmentByOrder(Map<String, Object> map) {
		return shipmentMapper.selectShipmentByOrder(map);
	}
	/**
	 * 根据订单号查询shipmentt TYPE
	 * @param map
	 * @return map
	 */
	@Override
	public List<Map<String, Object>> getShippmentByType(Map<String, Object> map) {
		return shipmentMapper.getShippmentByType(map);
	}

	public void saveSubShipment(List<Map<String, Object>> map, Map<String, Object> lastMap, Long shipmentId, Long logisticProductId){
		//打印入参段
		logger.info("pararmeter " + new Gson().toJson(map));
		Map<String, Object> getShipment = new HashMap<>();
		getShipment.put("shipmentId",shipmentId);
		Shipment shipment = shipmentMapper.selectShipmentById(getShipment);
		String shipToGeography = shipment.getShipToGeography();
		Date currentDate = new Date();
		//根据不同段生成不同sub-shipment
		if (null != map && 0 < map.size()){
			//每条路线生成不同sub-shipment
            if (map.size() == 1){
                lastMap.put("shippingSegmentId", Long.parseLong(map.get(0).get("shipping_segment_id").toString()));
//				if (1==Long.parseLong(shipmentType)){
				Long segmentSequence = Long.parseLong(map.get(0).get("segment_sequence").toString());
				Long subShipmentId = null;
				Long oldSubShipmentId = null;
				if ("Transit Warehouse".equals(shipToGeography)){
					oldSubShipmentId = subShipmentMapper.getSubshipment(saveBeanByMap(map.get(0), currentDate, shipmentId , segmentSequence));
					if(oldSubShipmentId==null){
						SubShipment subShipment = saveBeanVO(map.get(0), currentDate, shipmentId, segmentSequence);
						subShipmentMapper.insertSubshipmentVO(subShipment);
						subShipmentId = subShipment.getSubShipmentId();
					}
				}else {
					oldSubShipmentId = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
					if(oldSubShipmentId==null){
						subShipmentMapper.insertSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
						subShipmentId = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
					}
				}
				if(oldSubShipmentId==null){
					Map<String, Object> lpsMap = new HashMap<>();
					lpsMap.put("logisticProductId", logisticProductId);
					lpsMap.put("subShipmentId", subShipmentId);
					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
//				}else {
//					Long segmentSequence = Long.parseLong(map.get(0).get("segment_sequence").toString());
//					//插入物流订单关联查询是否已有记录
//					Long subShipmentId1 = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
//					if (null == subShipmentId1){
//						subShipmentMapper.insertSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
//						Map<String, Object> lpsMap = new HashMap<>();
//						Long subShipmentId = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
//						lpsMap.put("logisticProductId", logisticProductId);
//						lpsMap.put("subShipmentId", subShipmentId);
//						logisticProductShipmentMapper.insertlpShipment(lpsMap);
//					}
//				}
				
			}
			if (map.size() == 2){
                lastMap.put("shippingSegmentId", Long.parseLong(map.get(1).get("shipping_segment_id").toString()));
				Map<String, Object> thisMap = map.get(0);
				Long segmentSequence = Long.parseLong(map.get(0).get("segment_sequence").toString());
				//如果该段没有数据生成
				Long subShipmentId1 = subShipmentMapper.getSubshipment(saveBeanByMap(thisMap, currentDate, shipmentId , segmentSequence));
				if (null == subShipmentId1){
					//subShipmentMapper.insertSubshipment(saveBeanByMap(thisMap, currentDate, shipmentId, segmentSequence));
					subShipmentMapper.insertSubshipmentVO(saveBeanVO(thisMap, currentDate, shipmentId, segmentSequence));
					//Map<String, Object> lpsMap = new HashMap<>();
					//subShipmentMapper.getSubshipment(saveBeanByMap(thisMap, currentDate, shipmentId, segmentSequence));
//					lpsMap.put("logisticProductId", logisticProductId);
//					lpsMap.put("subShipmentId", subShipmentId);
//					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
				Long segmentSequence1 = Long.parseLong(map.get(1).get("segment_sequence").toString());
				//生成最后段
//				Long subShipmentId2 = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence1));
//				if (null == subShipmentId2){
				subShipmentMapper.insertSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence1));
				Map<String, Object> lpsMap = new HashMap<>();
				Long subShipmentId = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence1));
				lpsMap.put("logisticProductId", logisticProductId);
				lpsMap.put("subShipmentId", subShipmentId);
				logisticProductShipmentMapper.insertlpShipment(lpsMap);
//				}
			}
			if (map.size() == 3){
                lastMap.put("shippingSegmentId", Long.parseLong(map.get(2).get("shipping_segment_id").toString()));
				Map<String, Object> oneMap = map.get(0);
				Map<String, Object> twoMap = map.get(1);
				Long segmentSequence1 = Long.parseLong(map.get(0).get("segment_sequence").toString());
				Long segmentSequence2 = Long.parseLong(map.get(1).get("segment_sequence").toString());
				Long segmentSequence3 = Long.parseLong(map.get(2).get("segment_sequence").toString());
				Long subShipmentId1 = subShipmentMapper.getSubshipment(saveBeanByMap(oneMap, currentDate, shipmentId, segmentSequence1));
				if (null == subShipmentId1){
					//subShipmentMapper.insertSubshipment(saveBeanByMap(oneMap, currentDate, shipmentId, segmentSequence1));
					subShipmentMapper.insertSubshipmentVO(saveBeanVO(oneMap, currentDate, shipmentId, segmentSequence1));
					//Map<String, Object> lpsMap = new HashMap<>();
					//subShipmentMapper.getSubshipment(saveBeanByMap(oneMap, currentDate, shipmentId, segmentSequence1));
//					lpsMap.put("logisticProductId", logisticProductId);
//					lpsMap.put("subShipmentId", subShipmentId);
//					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
				Long subShipmentId2 = subShipmentMapper.getSubshipment(saveBeanByMap(twoMap, currentDate, shipmentId, segmentSequence2));
				if (null == subShipmentId2){
					subShipmentMapper.insertSubshipmentVO(saveBeanVO(twoMap, currentDate, shipmentId, segmentSequence2));
					//Map<String, Object> lpsMap = new HashMap<>();
					//subShipmentMapper.getSubshipment(saveBeanByMap(twoMap, currentDate, shipmentId, segmentSequence2));
//					lpsMap.put("logisticProductId", logisticProductId);
//					lpsMap.put("subShipmentId", subShipmentId);
//					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
//				Long subShipmentId3 = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence3));
//				if (null == subShipmentId3){
				subShipmentMapper.insertSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence3));
				Map<String, Object> lpsMap = new HashMap<>();
				Long subShipmentId = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence3));
				lpsMap.put("logisticProductId", logisticProductId);
				lpsMap.put("subShipmentId", subShipmentId);
				logisticProductShipmentMapper.insertlpShipment(lpsMap);
//				}
				
			}
		}
	}
	
	public Map<String, Object> saveBean(Map<String, Object> map, Date currentDate, Long shipmentId,Long segmentSequence){
		Map<String, Object> beanMap = new HashMap<String, Object>();
		beanMap.put("consignee", map.get("consignee")==null?" ":map.get("consignee").toString());
		beanMap.put("segmentSequence", segmentSequence);
		beanMap.put("shippingSegmentId", Long.parseLong(map.get("shippingSegmentId")==null?"0":map.get("shippingSegmentId").toString()));
		beanMap.put("shipToAddr", map.get("shipToAddr")==null?" ":map.get("shipToAddr").toString());
		beanMap.put("shipToCity", map.get("shipToCity")==null?" ":map.get("shipToCity").toString());
		beanMap.put("shipToCountry", map.get("shipToCountry")==null?" ":map.get("shipToCountry").toString());
		beanMap.put("shipToDistrict", map.get("shipToDistrict")==null?" ":map.get("shipToDistrict").toString());
		beanMap.put("shipToProvince", map.get("shipToProvince")==null?" ":map.get("shipToProvince").toString());
		beanMap.put("shipmentId", shipmentId);
		beanMap.put("status", ContainerType.RECEIVED);
		beanMap.put("updatedAt", currentDate);
		beanMap.put("createdAt", currentDate);
		beanMap.put("countryCode",map.get("countryCode")==null?" ":map.get("countryCode").toString());
		return beanMap;
	}

	private SubShipment saveBeanVO(Map<String, Object> map, Date currentDate, Long shipmentId,Long segmentSequence){
		SubShipment subShipment = new SubShipment();
		subShipment.setConsignee(map.get("transfer_consignee")==null?"":map.get("transfer_consignee").toString());
		subShipment.setShippingSegmentId(Long.parseLong(map.get("shipping_segment_id")==null?"0":map.get("shipping_segment_id").toString()));
		subShipment.setSegmentSequence(segmentSequence);
		subShipment.setShipToAddr(map.get("transfer_addr")==null?"":map.get("transfer_addr").toString());
		subShipment.setShipToDistrict(map.get("addr_district")==null?"":map.get("addr_district").toString());
		subShipment.setShipToCity(map.get("addr_city")==null?"":map.get("addr_city").toString());
		subShipment.setShipToProvince(map.get("addr_province")==null?"":map.get("addr_province").toString());
		subShipment.setShipToCountry(map.get("addr_country")==null?"":map.get("addr_country").toString());
		subShipment.setUpdatedAt(currentDate);
		subShipment.setShipmentId(shipmentId);
		subShipment.setCreatedAt(currentDate);
		subShipment.setStatus(ContainerType.RECEIVED);
		subShipment.setShipToCountryCode(map.get("country_code")==null?"":map.get("country_code").toString());
		subShipment.setContact(map.get("transfer_contact")==null?"":map.get("transfer_contact").toString());
		subShipment.setPiva(map.get("transfer_piva")==null?"":map.get("transfer_piva").toString());
		return subShipment;
	}
	
	public Map<String, Object> saveBeanByMap(Map<String, Object> map, Date currentDate, Long shipmentId, Long segmentSequence){
		Map<String, Object> beanMap = new HashMap<String, Object>();
		beanMap.put("consignee", map.get("transfer_consignee")==null?" ":map.get("transfer_consignee").toString());
		beanMap.put("segmentSequence",segmentSequence);
		beanMap.put("shippingSegmentId", Long.parseLong(map.get("shipping_segment_id")==null?"0":map.get("shipping_segment_id").toString()));
		beanMap.put("shipToAddr", map.get("transfer_addr")==null?" ":map.get("transfer_addr").toString());
		beanMap.put("shipToDistrict", map.get("addr_district")==null?" ":map.get("addr_district").toString());
		beanMap.put("shipToCity", map.get("addr_city")==null?" ":map.get("addr_city").toString());
		beanMap.put("shipToProvince", map.get("addr_province")==null?" ":map.get("addr_province").toString());
		beanMap.put("shipToCountry", map.get("addr_country")==null?" ":map.get("addr_country").toString());
		beanMap.put("shipmentId", shipmentId);
		beanMap.put("status", ContainerType.RECEIVED);
		beanMap.put("updatedAt", currentDate);
		beanMap.put("createdAt", currentDate);
		return beanMap;
	}

	/**
	 * 根据shipment状态大区查询shipment
	 * @param map
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getShipmentByStatus(Map<String, Object> map) {
		return shipmentMapper.getShipmentByStatus(map);
	}

	/**
	 * 根据shipmentID查询shipmentType
	 * @param map
	 * @return
	 */
	public Map<String, Object> getShipmentTypeById(Map<String, Object> map) {
		return shipmentMapper.getShipmentTypeById(map);
	}
	
	/**
	 * 根据vendorId大区查询shipment open的列表 如果可以返回第一段
	 * @return
	 */
	public List<Map<String, Object>> getShipmentsByVendor(Map<String, Object> map) {
		return shipmentMapper.getShipmentsByVendor(map);
	}
	
	/**
	 * 根据vendorId查询vendorCode
	 * @param vendorId
	 * @return
	 */
	public String getVendorCodeById(Long vendorId) {
		return shipmentMapper.getVendorCodeById(vendorId);
	}
	
	/**
	 * 根据条件查询shipment
	 * @param shipment
	 * @return
	 */
	public Long getShipmentId(Shipment shipment) {
		return shipmentMapper.getShipmentId(shipment);
	}

	/**
	 * 获取最大shipmentNo
	 */
	public Integer getMaxShipmentNo(Map<String, Object> map) {
		return shipmentMapper.getMaxShipmentNo(map);
	}

	/**
	 * 根据shipmentId修改状态
	 */
	@Override
	public int updateShipmentStatus(Map<String, Object> map) {
		int status = Integer.parseInt(map.get("status").toString());
		//查询当前对象信息
		Shipment shipment = shipmentMapper.selectShipmentById(map);
		int result = 0;
		//修改状态
		if (null != shipment){
			logger.info(MessageFormat.format("当前shipment状态:{0},需要修改后的shipment状态:{1}",shipment.getStatus(),status));
			//状态一直不需要修改
			if(shipment.getStatus() == status){
	        	return result;
	        }
			//获取上一个状态
			int lastStatus= ContainerType.getLastStatus(status);
			//如果一直修改状态
			if (lastStatus == shipment.getStatus()){
				result = shipmentMapper.updateShipmentStatus(map);
			}
			//如果为编辑箱子，修改箱子状态
			if (status == 1){
				result = shipmentMapper.updateShipmentStatus(map);
			}
		}
		return result;
	}

	@Override
	public Shipment selectShipmentById(Map<String, Object> map) {
		return shipmentMapper.selectShipmentById(map);
	}

	@Override
	public List<Shipment> selectContainerId(Map<String, Object> map) {
		return shipmentMapper.selectContainerId(map);
	}

	@Override
	public int deleteShipmentById(Map<String, Object> map) {
		return shipmentMapper.deleteShipmentById(map);
	}

	@Override
	public Map<String, Object> getShipmentInfoById(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return shipmentMapper.getShipmentInfoById(map);
	}

	public Long newShipment(Map<String, Object> map){
		Shipment shipment = new Shipment();
		//获取当前时间
		Date currentDate = new Date();
		//保存对象信息
		/*String shipToGeography = map.get("shipToGeography") == null ? " " : map.get("shipToGeography").toString();
		if("China Mainland".equals(shipToGeography)||
				"HongKong".equals(shipToGeography)){
			shipToGeography = "China excl. Taiwan";
		}*/
		String shipToGeography = "";
		Shipment oldShipment = shipmentMapper.selectShipmentById(map);
		if (oldShipment != null){
			shipToGeography = oldShipment.getShipToGeography();
		}
		shipment.setShipToGeography(shipToGeography);
		Long vendorId = Long.parseLong(map.get("vendor_id").toString());
		String top = shipmentMapper.getVendorCodeById(vendorId);
		Map<String, Object> noMap = new HashMap<>();
		noMap.put("topName", top+"SP");
		Integer maxNo = shipmentMapper.getMaxShipmentNo(noMap);
		if (null == maxNo)
			maxNo = 1000001;
		else 
			maxNo ++;
		//生成shipmentNo
		shipment.setShipmentNo(top+"SP"+maxNo);
		shipment.setVendorId(vendorId);
		shipment.setStatus(ContainerType.OPEN);
		shipment.setShipmentCategory(oldShipment.getShipmentCategory());
		shipment.setCreatedAt(currentDate);
		shipment.setUpdatedAt(currentDate);
		logger.info("parameter :" + new Gson().toJson(shipment));
		int result = shipmentMapper.saveShipmentByOrderId(shipment);
		logger.info("insert shipment result : " +result);
		Long shipmentId = shipmentMapper.getShipmentId(shipment);
		return shipmentId;
	}

	/**
	 * 根据shipmentId查询shipmentNo
	 */
	@Override
	public String getShipmentNoById(Map<String, Object> map) {
		return shipmentMapper.getShipmentNoById(map);
	}

	@Override
	public List<Shipment> getShipmentList(Map<String, Object> map) {
		return shipmentMapper.getShipmentList(map);
	}
}
