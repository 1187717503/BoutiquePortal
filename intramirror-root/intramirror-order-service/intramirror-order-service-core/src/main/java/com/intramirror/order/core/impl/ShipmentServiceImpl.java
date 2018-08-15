/**
 *
 */
package com.intramirror.order.core.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.intramirror.common.core.mapper.SubShipmentMapper;
import com.intramirror.common.help.StringUtils;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.model.SubShipment;
import com.intramirror.order.api.service.*;
import com.intramirror.order.api.util.HttpClientUtil;
import com.intramirror.order.api.vo.*;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticProductShipmentMapper;
import com.intramirror.order.core.mapper.LogisticsProductMapper;
import com.intramirror.order.core.mapper.ShipmentMapper;
import com.intramirror.order.core.utils.MailSendManageService;
import com.intramirror.order.core.utils.ShipMailSendThread;
import com.intramirror.utils.transform.JsonTransformUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	private LogisticProductShipmentMapper logisticProductShipmentMapper;

	@Autowired
	private ISubShipmentService subShipmentService;

	@Autowired
	private MailSendManageService mailSendManageService;

	@Autowired
	private KafkaUtilService kafkaUtilService;

	@Autowired
	private IOrderService orderService;

	@Autowired
	private ILogisticsProductService iLogisticsProductService;

	@Override
	public void init() {
		shipmentMapper = this.getSqlSession().getMapper(ShipmentMapper.class);
		subShipmentMapper = this.getSqlSession().getMapper(SubShipmentMapper.class);
		logisticProductShipmentMapper = this.getSqlSession().getMapper(LogisticProductShipmentMapper.class);
        logisticsProductMapper = this.getSqlSession().getMapper(LogisticsProductMapper.class);
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
			synchronized (shipment){
				//获取当前时间
				Date currentDate = new Date();
				//保存对象信息
				shipment.setShipToGeography(map.get("pack_english_name")==null?" ":map.get("pack_english_name").toString());
				Long vendorId = Long.parseLong(map.get("vendor_id").toString());
				Object shipFromLocation_id = map.get("ship_from_location_id");
				Integer locationId = Integer.parseInt(shipFromLocation_id!=null?shipFromLocation_id.toString():"0");
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
				shipment.setShipFromLocationId(locationId);
				shipment.setCreatedAt(currentDate);
				shipment.setUpdatedAt(currentDate);
				shipment.setShipmentCategory(shipmentCategory);
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
					List<Map<String, Object>> listMap = shipmentMapper.getShippmentByType(typeMap);
					logger.info("result shipmentType:" + new Gson().toJson(listMap));
					shipmentId = shipmentMapper.getShipmentId(shipment);
					Long consigner_country_id =  Long.parseLong(map.get("consigner_country_id").toString());
					Long consignee_country_id =  Long.parseLong(map.get("consignee_country_id").toString());
					saveSubShipmentByTms(map,consigner_country_id,consignee_country_id,vendorId,shipmentId,Long.parseLong(
							map.get("logistics_product_id")==null?"0":map.get("logistics_product_id").toString()));
					/*saveSubShipment(listMap, map,shipmentId,Long.parseLong(
							map.get("logistics_product_id")==null?"0":map.get("logistics_product_id").toString()));*/
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
				List<Map<String, Object>> listMap = shipmentMapper.getShippmentByType(typeMap);
				logger.info("result shipmentType:" + new Gson().toJson(listMap));
//				saveSubShipment(listMap, map,shipmentId,Long.parseLong(
//						map.get("logistics_product_id")==null?"0":map.get("logistics_product_id").toString()));
				Long consigner_country_id =  Long.parseLong(map.get("consigner_country_id").toString());
				Long consignee_country_id =  Long.parseLong(map.get("consignee_country_id").toString());
				saveSubShipmentByTms(map,consigner_country_id,consignee_country_id,Long.parseLong(map.get("vendor_id").toString()),shipmentId,Long.parseLong(
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

	public void saveSubShipmentByTms(Map<String, Object> map,Long consigner_country_id,Long consignee_country_id, Long vendorId, Long shipmentId, Long logisticProductId){
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
				for(ProviderVo providerVo:routeVos.get(0).getProviderVoList()){
					if(StringUtils.isNotBlank(providerVo.getAddress())){
                        SubShipment subShipment = new SubShipment();
                        subShipment.setConsignee(map.get("transfer_consignee") == null ? "" : map.get("transfer_consignee").toString());
                        subShipment.setPersonName(map.get("person_name") == null ? "" : map.get("person_name").toString());
                        subShipment.setSegmentSequence(providerVo.getSequence().longValue());
                        subShipment.setShippingSegmentId(providerVo.getShippingSegmentId());
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
                        subShipment.setContact(providerVo.getContactName());
                        subShipment.setPiva(providerVo.getTransferPiva());
                        subShipment.setPostalCode(providerVo.getPostalCode());
                        subShipmentMapper.insertSubshipmentVO(subShipment);
                    }else {
                        Map<String, Object> bean = saveBean(map, currentDate, shipmentId, providerVo.getSequence().longValue());
                        bean.put("shippingSegmentId",providerVo.getShippingSegmentId());
                        subShipmentMapper.insertSubshipment(bean);
                    }
				}
			}
		}
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
	}

	public Map<String, Object> saveBean(Map<String, Object> map, Date currentDate, Long shipmentId,Long segmentSequence){
		Map<String, Object> beanMap = new HashMap<String, Object>();
		String countryCode = map.get("countryCode") == null ? " " : map.get("countryCode").toString();
		//发往中国大陆，及港澳地区的不用校验地址信息
		boolean flag = true;
		if ("CN".equalsIgnoreCase(countryCode)||"HK".equalsIgnoreCase(countryCode)||"MO".equalsIgnoreCase(countryCode)){
			flag = false;
		}
		beanMap.put("consignee", map.get("consignee")==null?" ":map.get("consignee").toString());
		beanMap.put("personName", map.get("consignee")==null?" ":map.get("consignee").toString());
		beanMap.put("segmentSequence", segmentSequence);
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
			int lastStatus = ContainerType.getLastStatus(status);
			if (status == 3){
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
			}
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
	public void styleroomShip(List<String> list) {
	    if (list==null||list.size()==0){
	        return;
        }
		//判断是否是微店订单，即channel_id=6
		List<String> orderLineNums = orderService.getStyleroomOrder(list);
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
        ShipMailSendThread thread = new ShipMailSendThread(shipment, mailSendManageService);
        thread.run();
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
	public void shipmentToShip(Long shipmentId) {
		checkAWB(shipmentId);

		Map<String,Object> map = new HashMap<>();
		map.put("shipmentId",shipmentId);
		map.put("status",3);
		shipmentMapper.updateShipmentStatus(map);
	}

	@Override
	public List<LogisticsProduct> getLogisticsProductByShipment(Long shipmentId) {
		return logisticsProductMapper.getLogisticsProductByShipment(shipmentId);
	}
}
