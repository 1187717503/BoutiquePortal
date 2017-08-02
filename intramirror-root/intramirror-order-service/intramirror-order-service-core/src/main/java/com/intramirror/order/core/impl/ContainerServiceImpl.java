/**
 * 
 */
package com.intramirror.order.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intramirror.order.api.model.Container;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.ContainerMapper;

/**
 * @author 袁孟亮
 *
 */
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
	public int saveContainerByShipment(Container container) {
		return containerMapper.saveContainerByShipment(container);
	}

	/**
	 * 查询箱子
	 * @param barcode
	 * @return Container
	 */
	public Container getContainerByBarcode(String barcode) {
		return containerMapper.getContainerByBarcode(barcode);
	}

	/**
	 * 修改箱子
	 * @param barcode
	 * @return Container
	 */
	public int updateContainerByBarcode(Container container) {
		return containerMapper.updateContainerByBarcode(container);
	}


	

}
