/**
 * 
 */
package com.intramirror.order.core.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.model.Container;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.ContainerMapper;

/**
 * @author 袁孟亮
 *
 */
@Service
public class ContainerServiceImpl extends BaseDao implements IContainerService{

	private static Logger logger = LoggerFactory.getLogger(ContainerServiceImpl.class);
	
	private ContainerMapper containerMapper;
	
	@Override
	public void init() {
		containerMapper = this.getSqlSession().getMapper(ContainerMapper.class);
	}
	
	/**
	 * 新增箱子
	 * @param container
	 * @return int
	 */
	public int saveContainerByShipment(Map<String, Object> map) {
		Container container = new Container();
		container.setLength(Long.parseLong(map.get("length").toString()));
		container.setWidth(Long.parseLong(map.get("width").toString()));
		container.setHeight(Long.parseLong(map.get("height").toString()));
		//新增默认箱子为open 
		container.setStatus(ContainerType.OPEN);
		Date currentDate = new Date();
		container.setCreatedAt(currentDate);
		container.setUpdatedAt(currentDate);
		if (null != map.get("containerType") || StringUtils.isNotBlank(map.get("containerType").toString())){
			container.setContainerType(map.get("containerType").toString());
		}
		if (null != map.get("shipmentId") || StringUtils.isNotBlank(map.get("shipmentId").toString())){
			container.setShipmentId(Long.parseLong(map.get("shipmentId").toString()));
		}
		if (null != map.get("shipToGeography") || StringUtils.isNotBlank(map.get("shipToGeography").toString())){
			container.setShipToGeography(map.get("shipToGeography").toString());
		}
		if (null != map.get("weight") || StringUtils.isNotBlank(map.get("weight").toString())){
			container.setWeight(Long.parseLong(map.get("weight").toString()));
		}
		if (null != map.get("barcode") || StringUtils.isNotBlank(map.get("barcode").toString())){
			container.setBarcode(map.get("barcode").toString());
		}
		return containerMapper.saveContainerByShipment(container);
	}

	/**
	 * 查询箱子
	 * @param barcode
	 * @return Container
	 */
	public Container getContainerBybarcode(Map<String, Object> map) {
		return containerMapper.selectContainerByBarcode(map);
	}

	/**
	 * 修改箱子
	 * @param barcode
	 * @return Container
	 */
	public int updateContainerBybarcode(Map<String, Object> map) {
		return containerMapper.updateContainerByBarcode(map);
	}


	

}
