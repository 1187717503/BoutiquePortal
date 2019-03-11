/**
 *
 */
package com.intramirror.order.core.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.intramirror.common.core.mapper.SubShipmentMapper;
import com.intramirror.common.core.mapper.ThirdWarehouseMapper;
import com.intramirror.common.core.mapper.TransitWarehouseMapper;
import com.intramirror.common.help.StringUtils;
import com.intramirror.main.api.service.TaxService;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.common.LogisticsType;
import com.intramirror.order.api.model.*;
import com.intramirror.order.api.service.*;
import com.intramirror.order.api.util.HttpClientUtil;
import com.intramirror.order.api.util.RedisService;
import com.intramirror.order.api.vo.*;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.*;
import com.intramirror.order.core.utils.MailSendManageService;
import com.intramirror.order.core.utils.ShipMailSendThread;
import com.intramirror.utils.transform.JsonTransformUtil;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

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
	private LogisticsProductMapper logisticsProductMapper;
	private TransitWarehouseMapper transitWarehouseMapper;
	private ThirdWarehouseMapper thirdWarehouseMapper;
	private CczhangOrderEmailMapper cczhangOrderEmailMapper;
	private LogisticsMilestoneMapper logisticsMilestoneMapper;
	private ContainerMapper containerMapper;
	private VendorShipmentMapper vendorShipmentMapper;
	private ShippingProviderMapper shippingProviderMapper;

	@Autowired
	private ISubShipmentService subShipmentService;
	@Autowired
	private MailSendManageService mailSendManageService;
	@Autowired
	private KafkaUtilService kafkaUtilService;
	@Autowired
	private IOrderService orderService;
	@Autowired
	private TaxService taxService;
    @Autowired
	private RedisService redisService;


	@Override
	public void init() {
		shipmentMapper = this.getSqlSession().getMapper(ShipmentMapper.class);
		subShipmentMapper = this.getSqlSession().getMapper(SubShipmentMapper.class);
        logisticsProductMapper = this.getSqlSession().getMapper(LogisticsProductMapper.class);
        transitWarehouseMapper = this.getSqlSession().getMapper(TransitWarehouseMapper.class);
		thirdWarehouseMapper = this.getSqlSession().getMapper(ThirdWarehouseMapper.class);
		cczhangOrderEmailMapper = this.getSqlSession().getMapper(CczhangOrderEmailMapper.class);
		logisticsMilestoneMapper = this.getSqlSession().getMapper(LogisticsMilestoneMapper.class);
		containerMapper = this.getSqlSession().getMapper(ContainerMapper.class);
		vendorShipmentMapper = this.getSqlSession().getMapper(VendorShipmentMapper.class);
		shippingProviderMapper = this.getSqlSession().getMapper(ShippingProviderMapper.class);
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
		String shipToGeography = map.get("pack_english_name") == null ? " " : map.get("pack_english_name").toString();
		Shipment shipment = new Shipment();
		if (shipmentId == 0){
			synchronized (this){
				//获取当前时间
				Date currentDate = new Date();
				//保存对象信息
				shipment.setShipToGeography(shipToGeography);
				Long vendorId = Long.parseLong(map.get("vendor_id").toString());
				Object shipFromLocation_id = map.get("ship_from_location_id");
				Integer locationId = Integer.parseInt(shipFromLocation_id!=null?shipFromLocation_id.toString():"0");
				Integer shippingMethod = map.get("shipping_method") != null ? Integer.valueOf(map.get("shipping_method").toString()) : null;
				String top = shipmentMapper.getVendorCodeById(vendorId);
				Map<String, Object> noMap = new HashMap<>();
				noMap.put("topName", top+"SP");
				String key = "shipment:barcode:" + top;
				Integer maxNo;
				Object o = redisService.get(key);
				if (o != null && StringUtils.isNotBlank(o.toString())){
					maxNo = Integer.valueOf(o.toString()) + 1;
				}else {
					maxNo = shipmentMapper.getMaxShipmentNo(noMap);
					if (null == maxNo)
						maxNo = 1000001;
					else
						maxNo++;
				}
				redisService.set(key,maxNo);
				//生成shipmentNo
				shipment.setShipmentNo(top+"SP"+maxNo);
				shipment.setVendorId(vendorId);
				shipment.setStatus(ContainerType.OPEN);
				shipment.setShipFromLocationId(locationId);
				shipment.setCreatedAt(currentDate);
				shipment.setUpdatedAt(currentDate);
				shipment.setShipmentCategory(shipmentCategory);
				shipment.setShippingMethod(shippingMethod);
				//wms需要的信息
				shipment.setFromType(1); //买手店发货
				shipment.setFromRefId(vendorId);
				if( 1 == shipmentCategory) {
					shipment.setToType(2);  //发给质检仓
					shipment.setToRefId(1L);  //质检仓id
				}else {
					//发给用户的默认为0
					shipment.setToType(0);
					shipment.setToRefId(0L);
				}
				shipment.setGeographyId(Long.parseLong(map.get("geography_id").toString()));
				logger.info("parameter :" + new Gson().toJson(shipment));
				result = shipmentMapper.saveShipmentByOrderId(shipment);
				logger.info("insert shipment result : " +result);
				if (result == 1){
					//根据段生成subshipment
					Map<String, Object> typeMap = addCountryNum(map, shipmentCategory);
					typeMap.put("vendor_id",vendorId);
					logger.info("getShipmentId :" + new Gson().toJson(typeMap));
					shipmentId = shipmentMapper.getShipmentId(shipment);

					Long vaddressCountryId = vendorShipmentMapper.getVendorAddressCountryIdByShipmentId(shipmentId);
					if(vaddressCountryId != null && (vaddressCountryId == 2 || vaddressCountryId == 3)) { // 中国香港和中国大陆的店
						saveCoeSubShipment(shipmentId);
					}else {
						if ("China excl. Taiwan".equalsIgnoreCase(shipToGeography)){
							//发往中国大陆的通过桂邦中转
							saveSubShipment(map,shipmentId,new Date());
						}else {
							Long consigner_country_id =  Long.parseLong(map.get("consigner_country_id").toString());
							Long consignee_country_id = null;
							if (shipmentCategory == 1){
								//发往质检仓的，收件id为52
								consignee_country_id = 52L;
							}else {
								consignee_country_id =  Long.parseLong(map.get("consignee_country_id").toString());
							}
							saveSubShipmentByTms(map,consigner_country_id,consignee_country_id,vendorId,shipmentId);
						}
					}

					shipment.setShipmentId(shipmentId);
					return shipment;
				}
			}
		}else{
			shipment = shipmentMapper.selectShipmentById(map);
			shipmentId = shipment.getShipmentId();
			if (null != shipment){
				Map<String, Object> typeMap = addCountryNum(map, shipment.getShipmentCategory());
				typeMap.put("vendor_id",shipment.getVendorId());
				logger.info("getShipmentId :" + new Gson().toJson(typeMap));
				Long vaddressCountryId = vendorShipmentMapper.getVendorAddressCountryIdByShipmentId(shipmentId);
				if(vaddressCountryId != null && (vaddressCountryId == 2 || vaddressCountryId == 3)) { // 中国香港和中国大陆的店
					saveCoeSubShipment(shipmentId);
				}else{
					//				saveSubShipment(listMap, map,shipmentId,Long.parseLong(
//						map.get("logistics_product_id")==null?"0":map.get("logistics_product_id").toString()));
					//Long consigner_country_id =  Long.parseLong(map.get("consigner_country_id").toString());
					//Long consignee_country_id =  Long.parseLong(map.get("consignee_country_id").toString());
				if ("China excl. Taiwan".equalsIgnoreCase(shipToGeography)){
					//发往中国大陆的通过桂邦中转
					saveSubShipment(map,shipmentId,new Date());
				}else {
					Long consigner_country_id = Long.parseLong(map.get("consigner_country_id").toString());
					Long consignee_country_id = null;
					if (shipmentCategory == 1) {
						//发往质检仓的，收件id为52
						consignee_country_id = 52L;
					} else {
						consignee_country_id = Long.parseLong(map.get("consignee_country_id").toString());
					}
					saveSubShipmentByTms(map, consigner_country_id, consignee_country_id, Long.parseLong(map.get("vendor_id").toString()), shipmentId);
					}
				}
				return shipment;
			}
		}
		return null;
	}

	private void saveCoeSubShipment(Long shipmentId) {
		// 获取coe地址 香港和中国买手店特殊处理 不调用tms
		ShippingProvider shippingProvider = shippingProviderMapper.selectByPrimaryKey(1L);
		SubShipment record = new SubShipment();
		record.setShipmentId(shipmentId);
		record.setConsignee(shippingProvider.getTransferConsignee());
		record.setContact(shippingProvider.getTransferContact());
		record.setCreatedAt(new Date());
		record.setStatus(1); //open
		record.setPersonName(shippingProvider.getPersonName());
		record.setPostalCode(shippingProvider.getZipCode());
		record.setSegmentSequence(1L);  // 第一段
		record.setShipToAddr(shippingProvider.getTransferAddr());
		record.setShipToAddr2(shippingProvider.getTransferAddr2());
		record.setShipToAddr3(shippingProvider.getTransferAddr3());
		record.setShipToEamilAddr(shippingProvider.getEmailAddress());
		record.setShipToCity(shippingProvider.getAddrCity());
		record.setShipToCountry(shippingProvider.getAddrCountry());
		record.setShipToCountryCode(shippingProvider.getCountryCode());
		record.setUpdatedAt(new Date());
		subShipmentMapper.insertSubshipmentVO(record);
	}

	private void saveSubShipment(Map<String, Object> map, Long shipmentId, Date currentDate) {
		//添加第一段
		//获取桂邦地址
		TransitWarehouse transitWarehouse = transitWarehouseMapper.selectByPrimaryKey(5L);
		if (transitWarehouse == null){
			throw new RuntimeException("TransitWarehouse is null");
		}
		SubShipment subShipment = new SubShipment();
		subShipment.setConsignee(transitWarehouse.getContactCompanyname());
		subShipment.setPersonName(transitWarehouse.getContactPersonname());
		subShipment.setSegmentSequence(1L);
		subShipment.setShippingSegmentId(4L);
		//subShipment.setShippingProviderId(null);
		subShipment.setShipToAddr(transitWarehouse.getAddressStreetlines());
		subShipment.setShipToAddr2(transitWarehouse.getAddressStreetlines2());
		subShipment.setShipToAddr3(transitWarehouse.getAddressStreetlines3());
		subShipment.setShipToEamilAddr(transitWarehouse.getContactEmailaddress());
		subShipment.setShipToCity(transitWarehouse.getAddressCity());
		subShipment.setShipToCountry(transitWarehouse.getCountry());
		subShipment.setUpdatedAt(currentDate);
		subShipment.setShipmentId(shipmentId);
		subShipment.setCreatedAt(currentDate);
		subShipment.setStatus(ContainerType.RECEIVED);
		subShipment.setShipToCountryCode(transitWarehouse.getAddressCountrycode());
		subShipment.setContact(transitWarehouse.getContactPhonenumber());
		subShipment.setPostalCode(transitWarehouse.getAddressPostalcode());
		Map beanMap = JsonTransformUtil.readValue(JsonTransformUtil.toJson(subShipment),Map.class);
		Long subShipmentId = subShipmentMapper.getSubshipment(beanMap);
		if(subShipmentId==null){
			subShipmentMapper.insertSubshipmentVO(subShipment);
		}

		//添加第二段
		ProviderVo providerVo = new ProviderVo();
		providerVo.setSequence(2);
		Map<String, Object> bean = saveBean(map, new Date(), shipmentId, providerVo);
		bean.put("shippingSegmentId",null);
		Long subShipmentId1 = subShipmentMapper.getSubshipment(bean);
		if(subShipmentId1==null){
			subShipmentMapper.insertSubshipment(bean);
		}
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

	public void saveSubShipmentByTms(Map<String, Object> map,Long consigner_country_id,Long consignee_country_id, Long vendorId, Long shipmentId){
		StringBuffer sb = new StringBuffer(HttpClientUtil.tmsProviderRouteUrl);
		sb.append("?").append("senderCountryId=").append(consigner_country_id).append("&recipientCountryId=")
				.append(consignee_country_id).append("&invokerId=").append(vendorId).append("&invokerType=1");
		String json = HttpClientUtil.httpGet(sb.toString());
		saveSubShipmentReturnId(json,shipmentId,map);
	}

	private void saveSubShipmentReturnId(String json,Long shipmentId,Map<String, Object> map){
		if(StringUtils.isNotBlank(json)){
			List<TransportationRouteVo> routeVos = JSONArray.parseArray(json, TransportationRouteVo.class);
			if(routeVos!=null&&routeVos.size()>0){
				Date currentDate = new Date();
				List<ProviderVo> providerVoList = routeVos.get(0).getProviderVoList();
				if (CollectionUtils.isEmpty(providerVoList)){
					throw new RuntimeException("Logistics routing is not configured.");
				}
				for(ProviderVo providerVo: providerVoList){
					if(StringUtils.isNotBlank(providerVo.getAddress())){
                        SubShipment subShipment = new SubShipment();
                        subShipment.setConsignee(providerVo.getCompanyName());
                        subShipment.setPersonName(providerVo.getContactName());
                        subShipment.setSegmentSequence(providerVo.getSequence().longValue());
                        subShipment.setShippingSegmentId(providerVo.getShippingSegmentId());
                        subShipment.setShippingProviderId(providerVo.getProviderId());
                        subShipment.setShipToAddr(providerVo.getAddress());
                        subShipment.setShipToAddr2(providerVo.getAddress2());
                        subShipment.setShipToAddr3(providerVo.getAddress3());
                        subShipment.setShipToEamilAddr(providerVo.getEmail());
                        subShipment.setShipToDistrict(providerVo.getDistrict());
                        subShipment.setShipToCity(providerVo.getCity());
                        subShipment.setShipToProvince(providerVo.getProvince());
                        subShipment.setShipToCountry(providerVo.getCountry());
                        subShipment.setUpdatedAt(currentDate);
                        subShipment.setShipmentId(shipmentId);
                        subShipment.setCreatedAt(currentDate);
                        subShipment.setStatus(ContainerType.RECEIVED);
                        subShipment.setShipToCountryCode(providerVo.getCountryCode());
                        subShipment.setContact(providerVo.getPhoneNumber());
                        subShipment.setPiva(providerVo.getTransferPiva());
                        subShipment.setPostalCode(providerVo.getPostalCode());
                        Map beanMap = JsonTransformUtil.readValue(JsonTransformUtil.toJson(subShipment),Map.class);
                        Long subShipmentId = subShipmentMapper.getSubshipment(beanMap);
                        if(subShipmentId==null){
                            subShipmentMapper.insertSubshipmentVO(subShipment);
                        }
                    }else {
                        Map<String, Object> bean = saveBean(map, currentDate, shipmentId, providerVo);
                        bean.put("shippingSegmentId",providerVo.getShippingSegmentId());
                        Long subShipmentId = subShipmentMapper.getSubshipment(bean);
                        if(subShipmentId==null){
                            subShipmentMapper.insertSubshipment(bean);
                        }
                    }
				}
			}
		}
	}

	/*public void saveSubShipment(List<Map<String, Object>> map, Map<String, Object> lastMap, Long shipmentId, Long logisticProductId){
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
				Map<String, Object> lpsMap = new HashMap<>();
				lpsMap.put("logisticProductId", logisticProductId);
				if(oldSubShipmentId==null){
					lpsMap.put("subShipmentId", subShipmentId);
				}else {
					lpsMap.put("subShipmentId", oldSubShipmentId);
				}
				logisticProductShipmentMapper.insertlpShipment(lpsMap);

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
	}*/

	public Map<String, Object> saveBean(Map<String, Object> map, Date currentDate, Long shipmentId,ProviderVo providerVo){
		Map<String, Object> beanMap = new HashMap<>();
		String countryCode = map.get("countryCode") == null ? " " : map.get("countryCode").toString();
		//发往中国大陆，及港澳地区的不用校验地址信息
		boolean flag = true;
		if ("CN".equalsIgnoreCase(countryCode)||"HK".equalsIgnoreCase(countryCode)||"MO".equalsIgnoreCase(countryCode)){
			flag = false;
		}
		beanMap.put("consignee", map.get("consignee")==null?" ":map.get("consignee").toString());
		beanMap.put("personName", map.get("consignee")==null?" ":map.get("consignee").toString());
		beanMap.put("segmentSequence", providerVo.getSequence());
		beanMap.put("shippingProviderId", providerVo.getProviderId());
		beanMap.put("shippingSegmentId", Long.parseLong(map.get("shippingSegmentId")==null?"0":map.get("shippingSegmentId").toString()));
		String addr = map.get("shipToAddr") == null ? "" : map.get("shipToAddr").toString();
		if (flag && StringUtils.isBlank(addr)){
			throw new RuntimeException("The Receiving address cannot be empty. Please contact customer to adjust!");
		}
		/*if (flag && addr.length() > 105){
			throw new RuntimeException("Receiving address is longer than maximum length (35 characters). Please contact customer to adjust!");
		}*/
		if (flag){
			//按空格把过长的地址拆分三段（每段长度不超过35个字符）
			List<String> address = splitAddress(addr);
			beanMap.put("shipToAddr", address.get(0).trim());
			if (StringUtils.isNotBlank(address.get(1)))
			beanMap.put("shipToAddr2", address.get(1).trim());
			if (StringUtils.isNotBlank(address.get(2)))
			beanMap.put("shipToAddr3", address.get(2).trim());
		}else {
			if (addr.length() > 200){
				throw new RuntimeException("Receiving address is longer than maximum length (200 characters). Please contact customer to adjust!");
			}
			beanMap.put("shipToAddr", addr);
		}
		//beanMap.put("shipToAddr2", map.get("shipToAddr2")==null?" ":map.get("shipToAddr2").toString());
		//beanMap.put("shipToAddr3", map.get("shipToAddr3")==null?" ":map.get("shipToAddr3").toString());
		beanMap.put("shipToEamilAddr", "shipment@intramirror.com");
        String city = map.get("shipToCity") == null ? "" : map.get("shipToCity").toString();
        if (flag && (StringUtils.isBlank(city)||"0".equals(city))){
            throw new RuntimeException("The city cannot be empty. Please contact customer to adjust!");
        }
        beanMap.put("shipToCity", map.get("shipToCity")==null?" ":map.get("shipToCity").toString());
		beanMap.put("shipToCountry", map.get("shipToCountry")==null?" ":map.get("shipToCountry").toString());
		beanMap.put("shipToDistrict", map.get("shipToDistrict")==null?" ":map.get("shipToDistrict").toString());
		beanMap.put("shipToProvince", map.get("shipToProvince")==null?" ":map.get("shipToProvince").toString());
		beanMap.put("shipmentId", shipmentId);
		beanMap.put("status", ContainerType.RECEIVED);
		beanMap.put("updatedAt", currentDate);
		beanMap.put("createdAt", currentDate);
		beanMap.put("shipToCountryCode",countryCode);
        String postalCode = map.get("zip_code") == null ? "" : map.get("zip_code").toString();
        if (flag && StringUtils.isBlank(postalCode)){
            throw new RuntimeException("The postcode cannot be empty. Please contact customer to adjust!");
        }
        beanMap.put("postalCode",postalCode);
		beanMap.put("contact",map.get("contact")==null?"":map.get("contact").toString());
		return beanMap;
	}

	private List<String> splitAddress(String addr) {
		List<String> address = new ArrayList<>();
		if (StringUtils.isBlank(addr)){
			return address;
		}else {
			String[] ss = addr.split(" ");
			for (int i=0;i<ss.length;i++){
				if (ss[i].length()>35){
					ss[i] = ss[i].substring(0,35);
				}
			}
			int index = 0;
			String a = "";
			while (address.size()<3){
				for(int i=index; i<ss.length;i++){
					if (a.trim().length()<=35){
						a += ss[i] + " ";
						index = i+1;
						if ((i<ss.length-1)&&(a+ss[i+1]).length()>35){
							break;
						}
					}
				}
				address.add(a);
				a = "";
			}
		}
		return address;
	}

	private SubShipment saveBeanVO(Map<String, Object> map, Date currentDate, Long shipmentId,Long segmentSequence){
		SubShipment subShipment = new SubShipment();
		subShipment.setConsignee(map.get("transfer_consignee")==null?"":map.get("transfer_consignee").toString());
		subShipment.setPersonName(map.get("person_name")==null?"":map.get("person_name").toString());
		subShipment.setShippingSegmentId(Long.parseLong(map.get("shipping_segment_id")==null?"0":map.get("shipping_segment_id").toString()));
		subShipment.setSegmentSequence(segmentSequence);
		subShipment.setShipToAddr(map.get("transfer_addr")==null?"":map.get("transfer_addr").toString());
		subShipment.setShipToAddr2(map.get("transfer_addr2")==null?"":map.get("transfer_addr2").toString());
		subShipment.setShipToAddr3(map.get("transfer_addr3")==null?"":map.get("transfer_addr3").toString());
		subShipment.setShipToEamilAddr(map.get("emailAddress")==null?"":map.get("emailAddress").toString());
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
		subShipment.setPostalCode(map.get("zip_code")==null?"":map.get("zip_code").toString());
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
		beanMap.put("shipToCountryCode", map.get("country_code")==null?" ":map.get("country_code").toString());
		beanMap.put("contact", map.get("transfer_contact")==null?" ":map.get("transfer_contact").toString());
		beanMap.put("postalCode", map.get("zip_code")==null?" ":map.get("zip_code").toString());
		beanMap.put("shipToEamilAddr", map.get("emailAddress")==null?" ":map.get("emailAddress").toString());
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
		Map<String, Object> shipmentMap = shipmentMapper.getShipmentTypeById(map);
		if (shipmentMap != null){
			if ("China excl. Taiwan".equalsIgnoreCase(shipmentMap.get("ship_to_geography").toString())){
				shipmentMap.put("shipment_type_id",3);
			}
		}
		return shipmentMap;
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
			int lastStatus = ContainerType.getLastStatus(status);
			/*if (status == 3){
				//记录发货时间
				map.put("ship_at",new Date());
				List<String> list = new ArrayList<>();

				//校验是否生成AWB
				String awb = checkAWB(shipment.getShipmentId());
				//shipped操作发送消息用来生成资金报表
                List<LogisticsProduct> logisticsProducts = logisticsProductMapper.getLogisticsProductByShipment(shipment.getShipmentId());
                if (logisticsProducts!=null && logisticsProducts.size()>0){
                    for (LogisticsProduct logisticsProduct:logisticsProducts){
                        kafkaUtilService.saveOrderFinance(logisticsProduct);
						list.add(logisticsProduct.getOrder_line_num());
                    }
                }

				//调用微店接口ship
				styleroomShip(list);

				// 起线程发邮件
				ShipmentSendMailVO vo = new ShipmentSendMailVO();
				vo.setShipmentNo(shipment.getShipmentNo());
				vo.setShipmentId(shipment.getShipmentId());
				if (shipment.getToType() == 2) {
					vo.setDestination("Transit Warehouse");
				} else if("China Mainland".equals(shipment.getShipToGeography())
						||"HongKong".equals(shipment.getShipToGeography())
						||"China excl. Taiwan".equals(shipment.getShipToGeography())) {
					vo.setDestination("China");
				}
                sendMail(vo);
				logger.info("shipmentNo:{},手动ship",shipment.getShipmentNo());
			}*/
			//如果一直修改状态
			if (lastStatus == shipment.getStatus()){
				if (status == 3){
                    logger.info("shipmentNo:{},手动ship",shipment.getShipmentNo());
					//记录发货时间
					map.put("ship_at",new Date());
					List<String> list = new ArrayList<>();

					//校验是否生成AWB
					if (shipment.getShippingMethod() != null&&
							(2 == shipment.getShippingMethod()||"COMO".equals(shipment.getShipToGeography()))){
						//如果此shipment是发往COMO的就不需要校验AWB是否生成
					}else {
						checkAWB(shipment.getShipmentId());
					}
					//临时修改，暂时不同步
					syncOrderToWarehouse(shipment);

					//shipped操作发送消息用来生成资金报表
					List<LogisticsProduct> logisticsProducts = logisticsProductMapper.getLogisticsProductByShipment(shipment.getShipmentId());
					if (logisticsProducts!=null && logisticsProducts.size()>0){
						for (LogisticsProduct logisticsProduct:logisticsProducts){
							kafkaUtilService.saveOrderFinance(logisticsProduct);
							list.add(logisticsProduct.getOrder_line_num());
						}
					}

					//调用微店接口ship
					styleroomShip(list);

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
					sendMail(vo);
				}
				result = shipmentMapper.updateShipmentStatus(map);
			}
			//如果为编辑箱子，修改箱子状态
			if (status == 1){
				map.put("resetStep",0);
				result = shipmentMapper.updateShipmentStatus(map);
			}
		}
		return result;
	}

	private void syncOrderToWarehouse(Shipment shipment) {
		//如果是发往中国大陆的shipment，需要同步一份数据到third_warehouse
		if ("China excl. Taiwan".equalsIgnoreCase(shipment.getShipToGeography())){
            //获取这批货的订单详情数据
            List<ThirdWarehouseVO> orderDetailList = thirdWarehouseMapper.getOrderDetailList(shipment.getShipmentNo());
            if (CollectionUtils.isEmpty(orderDetailList)){
                throw new RuntimeException("The order list is empty.shipmentNo:"+shipment.getShipmentNo());
            }
            List<String> orderLineNumList = new ArrayList<>();
            for (ThirdWarehouseVO thirdWarehouse:orderDetailList){
                Date currentDate = new Date();
                thirdWarehouse.setOverseaShipmentShipTime(currentDate);
                thirdWarehouse.setCreateTime(currentDate);
                thirdWarehouse.setUpdateTime(currentDate);
                //库存状态  1. 待收货  2. 已收货 3.已发货  4.异常收货
                thirdWarehouse.setStockStatus((byte)1);
				int expressType = thirdWarehouse.getExpressType() == null ? 0 : thirdWarehouse.getExpressType();
				if (thirdWarehouse.getAddressCountryId() != null && expressType == 0){
                	if (3 == thirdWarehouse.getAddressCountryId() || 4 == thirdWarehouse.getAddressCountryId()){
                		//港澳本地单
                		thirdWarehouse.setSortingType((byte)0);
					}
				}
				// bc订单
				if(expressType == 1){
					thirdWarehouse.setSortingType((byte)3);
				}
				//获取图片
				String spuImage = thirdWarehouse.getSpuImage();
				if (StringUtils.isNotBlank(spuImage) && !"[]".equals(spuImage)){
					//商品图片取spu图片
					thirdWarehouse.setOrderImage(spuImage);
				}
                thirdWarehouseMapper.insertSelective(thirdWarehouse);

                orderLineNumList.add(thirdWarehouse.getOrderLineNum());
            }
            logger.info("shipmentNo:{},发货到中国大陆同步订单到第三方仓库的单号：{}",shipment.getShipmentNo(),orderLineNumList);
        }
	}

	@Override
	public void styleroomShip(List<String> list) {
	    if (list==null||list.size()==0){
	        return;
        }
		//判断是否是微店订单，即channel_id=6
		List<String> orderLineNums = orderService.getStyleroomOrder(list);
		try {
			if (orderLineNums!=null&&orderLineNums.size()>0){
				logger.info("调用微店ship接口,orderLineNums:{},url:{}",
						JsonTransformUtil.toJson(orderLineNums), HttpClientUtil.shippedOrder);
				Map<String,Object> params = new HashMap<>();
				params.put("order_line_nums",orderLineNums);
				String re = HttpClientUtil.doPost(HttpClientUtil.shippedOrder,JsonTransformUtil.toJson(params),"utf-8");
				if (StringUtils.isNotBlank(re)){
					JSONObject object = JSONObject.fromObject(re);
					String success = object.optString("success");
					if (StringUtils.isNotBlank(success)){
						logger.info("调用微店ship接口成功");
					}else {
						logger.error("调用微店ship接口失败,msg:{}",object.optString("error"));
					}
				}else {
					logger.error("调用微店ship接口失败");
				}
			}
		}catch (Exception e){
			logger.error("调用微店ship接口失败,msg:{}",e.getMessage());
		}
	}

	@Override
	public String queryAwbUrlByAwbNum(String awbNum) {
		String docUrl = shipmentMapper.queryShipmentAwbUrlByAwbNum(awbNum);
		return docUrl;
	}

	@Override
	public int updatePrintStep(Map<String, Object> updateStepMap) {
		int printStep = Integer.valueOf(updateStepMap.get("print_step").toString());
		Long shipmentId = Long.valueOf(updateStepMap.get("shipment_id").toString());
		Map<String,Object> getShipmentMap = new HashMap<>();
		getShipmentMap.put("shipmentId",shipmentId);
		Shipment shipment = shipmentMapper.selectShipmentById(getShipmentMap);
		logger.info("print step is {},update print step {}",shipment.getPrintStep(),printStep);
		if(shipment.getPrintStep() == 4 && printStep == 3){ // undo 这种情况要清楚awb 删除awb清除milestone物流信息
			List<SubShipment> subShipments = subShipmentMapper.getSubShipmentByShipmentId(shipmentId);
			SubShipment subShipment = null;
			if(CollectionUtils.isNotEmpty(subShipments)){
				for(SubShipment sub : subShipments){
					if(sub.getSegmentSequence().equals(1L)){
						subShipment = sub;
						break;
					}
				}
			}
			if(subShipment != null && StringUtils.isNotBlank(subShipment.getAwbNum())){
				logger.info("删除milestone信息，awbNum:{}",subShipment.getAwbNum());
				shipmentMapper.deleteMilestone(subShipment.getAwbNum()); // 删除milestone
				subShipment.setAwbNum(null);
				logger.info("更新subShipment，subShipmentId:{}",subShipment.getSubShipmentId());
				subShipmentMapper.updateByPrimaryKey(subShipment);
				updateStepMap.put("awb_advance",0);  // 这种情况也需要重新生成awb
			}
		}
		if(printStep == 2 && shipment.getPrintStep() == 3){  // undo 这种情况需要重新设置awb_advance
			updateStepMap.put("awb_advance",0);
		}
		return shipmentMapper.updatePrintStep(updateStepMap);
	}

	@Override
	public void updateShipmentAwbAdvance(Map<String, Object> awbAdvanceMap) {
		shipmentMapper.updateShipmentAwbAdvance(awbAdvanceMap);
	}

	@Override
	public Map<String,Object> getShipmentByOrderLineNum(String orderLineNum) {
		Map<String,Object> map = new HashMap<>();
		Shipment shipment = shipmentMapper.getShipmentByOrderLineNum(orderLineNum);
		if (shipment != null){
			map.put("shipment",shipment);
			//查询此shipment下所有订单
			List<String> nums = shipmentMapper.getOrderLineNumsByShipment(shipment.getShipmentId());
			map.put("orderLineNum",nums);
			return map;
		}
		return null;
	}

	@Override
	@Transactional
	public void saveAwb(ShipmentInputVO inputVO) {
		Map<String,Object> params = new HashMap<>();
		Long shipmentId = inputVO.getShipmentId();
		String awbNo = inputVO.getAwbNo();
		params.put("shipmentId", shipmentId);
		params.put("sequence",1);
		SubShipment dhlShipment = subShipmentService.getDHLShipment(params);
		String oldAwb = null;
		if (dhlShipment != null && dhlShipment.getAwbNum() != null){
			oldAwb = dhlShipment.getAwbNum();
		}
		params.put("awbNo",awbNo);
		subShipmentService.updateSubShipment(params);

		//保存awb单号
		List<LogisticsProductVO> productVOList = getlogisticsMilestone(shipmentId);
		if (productVOList != null && productVOList.size() > 0 ){
			for (LogisticsProductVO vo:productVOList){
				vo.setAwb(awbNo);
				vo.setIsComplete(0);
				vo.setShipmentType(3);
				vo.setType(4);
				saveMilestone(vo);
			}
		}
		logger.info("shipmentId:{},新生成awb为{},并同步到物流数据中",shipmentId,awbNo);

		if (oldAwb != null){
			//删除物流表中旧的awb单号
			deleteMilestone(oldAwb);
			logger.info("awb:{}已从物流数据中删除",oldAwb);
		}
	}

	@Override
	public int getCartoonType(Long shipmentId) {
		List<Map<String,Object>> list = shipmentMapper.getCartoonType(shipmentId);
		Map<String, Object> map = list.get(0);
		if(!"3".equals(map.get("address_country_id_vendor").toString()) && !"2".equals(map.get("address_country_id_vendor").toString())){
			throw new RuntimeException("error shipmentId");
		}
		if(map == null){
			throw new RuntimeException("this carton have no order");
		}
		//香港特殊订单
		if("1".equals(map.get("express_type").toString())){
			return LogisticsType.EMS;
		}
		//大陆店
		if("2".equals(map.get("address_country_id_vendor").toString())){
			if ("ZNW".equals(map.get("vendor_code").toString())){
				//Z&W店用中通发货
				return LogisticsType.ZHONG_TONG;
			}
			return LogisticsType.SHUN_FENG;
		}
		//香港店
		if("3".equals(map.get("address_country_id_vendor").toString())){
			String addressCountryId = map.get("address_country_id").toString();
			//发往大陆使用ems其他使用顺丰
			if("2".equals(addressCountryId)){
				return LogisticsType.EMS;
			}

			if ("MUS".equals(map.get("vendor_code").toString())){
				if ("3".equals(addressCountryId) || "4".equals(addressCountryId)){
					//Muse店用本地件宅急便发货
					return LogisticsType.ZHAI_JI_BIAN;
				}

			}
			return LogisticsType.SHUN_FENG;
		}

		throw new RuntimeException("error vendor");
	}

	@Override
	@Transactional
	public void ship4hkAndMainLandVendor(Long shipmentId, String shipmentCode, Integer logisticsType)throws Exception {
		List<Map<String,Object>> list = shipmentMapper.getCartoonType(shipmentId);
		Map<String,Object> map = list.get(0);
		if(!"3".equals(map.get("address_country_id_vendor").toString()) && !"2".equals(map.get("address_country_id_vendor").toString())){
			throw new RuntimeException("error shipmentId");
		}
		if(map == null){
			throw new RuntimeException("this carton have no order");
		}
		//验证shipmentCode是否重复
		if(logisticsType.intValue() == 1){
			shipmentCode = "SF"+shipmentCode;
		}
		int count = shipmentMapper.doRepeatShipmentCode(shipmentCode);
		if(count>0){
			throw new RuntimeException("The logistic No. has been used");
		}
		int ccCount = cczhangOrderEmailMapper.doRepeatShipmentCode(shipmentCode);
		if(ccCount>0){
			throw new RuntimeException("The logistic No. has been used");
		}
		Date now = new Date();
		if("1".equals(map.get("express_type").toString())){
			CczhangOrderEmail cczhangOrderEmail = new CczhangOrderEmail();
			cczhangOrderEmail.setActionType(2);
			cczhangOrderEmail.setCreateTime(now);
			cczhangOrderEmail.setIsDeteled(0);
			cczhangOrderEmail.setOrderLineNum(map.get("order_line_num").toString());
			cczhangOrderEmail.setSendEmail(0);
			cczhangOrderEmail.setShipmentCode(shipmentCode);
			cczhangOrderEmail.setUpdateTime(now);
			cczhangOrderEmailMapper.insertSelective(cczhangOrderEmail);
		}else{
			logisticsMilestoneMapper.setDeleteByOrderAndType(map.get("order_line_num").toString(),4);
			String finalShipmentCode = shipmentCode;
			list.forEach(e ->{
				long logisticsProductId = Long.parseLong(e.get("logistics_product_id").toString());
				LogisticsMilestone logisticsMilestone = new LogisticsMilestone();
				logisticsMilestone.setCreateTime(now);
				logisticsMilestone.setDhlType(1);
				logisticsMilestone.setIsComplete(0);
				logisticsMilestone.setIsDeleted(0);
				logisticsMilestone.setIsReturn(0);
				logisticsMilestone.setIsShip(0);
				logisticsMilestone.setIsSubscription(0);
				logisticsMilestone.setLogisticsProductId(logisticsProductId);
				logisticsMilestone.setOrderNum(e.get("order_line_num").toString());
				logisticsMilestone.setRefOrderId(Long.parseLong(e.get("order_id").toString()));
				logisticsMilestone.setRefOrderNum(e.get("order_num").toString());
				logisticsMilestone.setShipmentCode(finalShipmentCode);
				logisticsMilestone.setTime(now);
				logisticsMilestone.setType(4);
				logisticsMilestone.setUpdateTime(now);
				logisticsMilestone.setUserId(Long.parseLong(e.get("user_id").toString()));
				if(logisticsType.intValue() == LogisticsType.SHUN_FENG){
					logisticsMilestone.setShipmentType(4);
				}else if (logisticsType.intValue() == LogisticsType.ZHONG_TONG) {
                    logisticsMilestone.setShipmentType(7);
                }else if (logisticsType.intValue() == LogisticsType.ZHAI_JI_BIAN) {
				    logisticsMilestone.setShipmentType(8);
                }else {
					logisticsMilestone.setShipmentType(Integer.parseInt(map.get("sorting_type").toString()));
				}
				logisticsMilestoneMapper.insertSelective(logisticsMilestone);

			});
		}

		list.forEach(e->{
			LogisticsProduct record = new LogisticsProduct();
			record.setStatus(3);
			record.setLogistics_product_id(Long.parseLong(e.get("logistics_product_id").toString()));
			record.setShipped_at(now);
			logisticsProductMapper.updateByLogisticsProduct(record);
		});

		Map<String,Object> shipmentStatusMap = new HashMap<>();
		shipmentStatusMap.put("status",3);
		shipmentStatusMap.put("ship_at",now);
		shipmentStatusMap.put("shipmentId",shipmentId);
		shipmentMapper.updateShipmentStatus(shipmentStatusMap);

		Map<String,Object> containerStatusMap = new HashMap<>();
		containerStatusMap.put("status",3);
		containerStatusMap.put("containerId",Long.parseLong(map.get("container_id").toString()));
		containerMapper.updateContainerBystatus(containerStatusMap);
	}



	private String checkAWB(Long shipmentId) {
		Map<String,Object> params = new HashMap<>();
		params.put("shipmentId",shipmentId);
		//查询第一段
		params.put("sequence",1);
		SubShipment dhlShipment = subShipmentService.getDHLShipment(params);
		if(dhlShipment!=null) {
            if (dhlShipment.getAwbNum() != null && StringUtils.isNotBlank(dhlShipment.getAwbNum())) {
				return dhlShipment.getAwbNum();
            }
        }
        throw new  RuntimeException("No AWB is not allowed to be shipped");
	}

    private void sendMail(ShipmentSendMailVO shipment){
        ShipMailSendThread thread = new ShipMailSendThread(shipment, mailSendManageService,taxService);
        new Thread(thread).start();
    }

	@Override
	public void sendMailForShipped(ShipmentSendMailVO shipment) {
		sendMail(shipment);
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
		shipment.setShipFromLocationId(oldShipment.getShipFromLocationId());
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

	@Override
	public BigDecimal getCustomValue(Map<String, Object> map) {
		return shipmentMapper.getCustomValue(map);
	}

    @Override
    public SubShipment getSubShipmentByShipmentId(Long shipmentId) {
        List<SubShipment> subShipments = subShipmentService.getSubShipmentByShipmentId(shipmentId);
        if (subShipments!=null&&subShipments.size()>0){
            return subShipments.get(0);
        }
        return null;
    }

	@Override
	public List<LogisticsProductVO> getlogisticsMilestone(Long shipmentId) {
		return shipmentMapper.getlogisticsMilestone(shipmentId);
	}

	@Override
	public void saveMilestone(LogisticsProductVO vo) {
		shipmentMapper.saveMilestone(vo);
	}

	@Override
	public void deleteMilestone(String awbNo) {
		shipmentMapper.deleteMilestone(awbNo);
	}

	@Override
	public List<Shipment> getShipmentList(List awbNos) {
		return shipmentMapper.getShipmentListByAwbNo(awbNos);
	}

	@Override
	public void shipmentToShip(Shipment shipment) {
		Long shipmentId = shipment.getShipmentId();
		if (!"COMO".equals(shipment.getShipToGeography())){
			checkAWB(shipmentId);
		}

		Map<String,Object> map = new HashMap<>();
		map.put("shipmentId",shipmentId);
		map.put("status",3);
		shipmentMapper.updateShipmentStatus(map);

		//如果是发往中国大陆的shipment，需要同步一份数据到third_warehouse
		//临时修改，暂时不同步
		syncOrderToWarehouse(shipment);
	}

	@Override
	public List<LogisticsProduct> getLogisticsProductByShipment(Long shipmentId) {
		return logisticsProductMapper.getLogisticsProductByShipment(shipmentId);
	}

	@Override
	public void deleteSubShipment(Long containerId) {
		shipmentMapper.deleteSubShipment(containerId);
	}
}
