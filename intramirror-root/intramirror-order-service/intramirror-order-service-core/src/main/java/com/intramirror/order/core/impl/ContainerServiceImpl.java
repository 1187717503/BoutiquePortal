/**
 * 
 */
package com.intramirror.order.core.impl;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
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
		container.setBarcode(map.get("barcode").toString());
		//新增默认箱子为open 
		container.setStatus(ContainerType.OPEN);
		Date currentDate = new Date();
		container.setCreatedAt(currentDate);
		container.setUpdatedAt(currentDate);
		if (null != map.get("containerType")){
			container.setContainerType(map.get("containerType").toString());
		}else{
			container.setContainerType("");
		}
		if (null != map.get("shipmentId")){
			container.setShipmentId(Long.parseLong(map.get("shipmentId").toString()));
		}else{
			//默认设置为0
			container.setShipmentId(Long.parseLong("0"));
		}
		if (null != map.get("shipToGeography")){
			container.setShipToGeography(map.get("shipToGeography").toString());
		}else{
			container.setShipToGeography("");
		}
		if (null != map.get("weight")){
			container.setWeight(Long.parseLong(map.get("weight").toString()));
		}else{
			container.setWeight(Long.parseLong("0"));
		}
		logger.info("perameter "+new Gson().toJson(container));
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

	/**
	 * 修改箱子状态
	 * @param map
	 * @return int
	 */
	public int updateContainerBystatus(Map<String, Object> map) {
		return containerMapper.updateContainerBystatus(map);
	}

	/**
	 * 删除箱子
	 * @param map
	 * @return int
	 */
	public int deleteContainerById(Map<String, Object> map) {
		return containerMapper.deleteContainerById(map);
	}

	/**
	 * 生成条形码
	 * @return int
	 */
	@Override
	public int getMaxBarcode() {
		return containerMapper.getMaxBarcode();
	}


	

}
