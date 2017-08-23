/**
 * 
 */
package com.intramirror.order.core.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
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
		
		if (null != map.get("length")){
			container.setLength(Long.parseLong(map.get("length").toString()));
		}else{
			container.setLength(Long.parseLong("0"));
		}
		if (null != map.get("width")){
			container.setWidth(Long.parseLong(map.get("width").toString()));
		}else{
			container.setWidth(Long.parseLong("0"));
		}
		if (null != map.get("height")){
			container.setHeight(Long.parseLong(map.get("height").toString()));
		}else{
			container.setHeight(Long.parseLong("0"));
		}
		//新增默认箱子为open 
		container.setStatus(ContainerType.OPEN);
		Date currentDate = new Date();
		container.setCreatedAt(currentDate);
		container.setUpdatedAt(currentDate);
		if (null != map.get("barcode")){
			container.setBarcode(map.get("barcode").toString());
		}else{
			container.setBarcode("");
		}
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
		int status = Integer.parseInt(map.get("status").toString());
		map.put("container_id", map.get("containerId"));
		//查询当前对象信息
		Container container = containerMapper.selectContainerById(map);
		int result = 0;
		//修改状态
		if (null != container){
			logger.info(MessageFormat.format("当前container状态:{0},需要修改后的container状态:{1}",container.getStatus(),status));
			//状态一直不需要修改
			if(container.getStatus() == status){
	        	return result;
	        }
			//获取上一个状态
			int lastStatus= ContainerType.getLastStatus(status);
			//如果一直修改状态
			if (lastStatus == container.getStatus()){
				result = containerMapper.updateContainerBystatus(map);
			}
			//如果为编辑箱子，修改箱子状态
			if (status == 1){
				result = containerMapper.updateContainerBystatus(map);
			}
		}
		return result;
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
	public Integer getMaxBarcode() {
		return containerMapper.getMaxBarcode();
	}

	@Override
	public int updateContainerByCondition(Map<String, Object> map) {
	
		return containerMapper.updateContainerByCondition(map);
	}

	@Override
	public Container selectContainerById(Map<String, Object> map) {
		return containerMapper.selectContainerById(map);
	}

	@Override
	public List<Map<String, Object>> getContainerList(Map<String, Object> map) {
		return containerMapper.getContainerList(map);
	}

	@Override
	public List<Map<String, Object>> getShipmentList(Map<String, Object> map) {
		return containerMapper.getShipmentList(map);
	}

	@Override
	public Integer getContainerCount(Map<String, Object> map) {
		return containerMapper.getContainerCount(map);
	}

	@Override
	public int updateContainerShipment(Map<String, Object> map) {
		return containerMapper.updateContainerShipment(map);
	}


	

}
