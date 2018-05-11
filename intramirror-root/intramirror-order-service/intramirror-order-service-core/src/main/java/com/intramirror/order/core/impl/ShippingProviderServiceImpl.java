/**
 * 
 */
package com.intramirror.order.core.impl;

import com.intramirror.order.api.model.ShippingProvider;
import com.intramirror.order.api.service.IShippingProviderService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.ShippingProviderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单装箱service
 * @author yuan
 *
 */
@Service
public class ShippingProviderServiceImpl extends BaseDao implements IShippingProviderService{

	private static Logger logger = LoggerFactory.getLogger(ShippingProviderServiceImpl.class);
	

	private ShippingProviderMapper shippingProviderMapper;
	

	@Override
	public void init() {
		shippingProviderMapper = this.getSqlSession().getMapper(ShippingProviderMapper.class);
	}

	@Override
	public ShippingProvider getShippingProviderByShipmentId(Long shipmentId) {
		List<ShippingProvider> providers = shippingProviderMapper.getShippingProviderByShipmentId(shipmentId);
		if (providers!=null&&providers.size()>0){
			return providers.get(0);
		}
		return new ShippingProvider();
	}

	@Override
	public ShippingProvider getShippingProviderByName(String name) {
		List<ShippingProvider> providers = shippingProviderMapper.getShippingProviderByName(name);
		if (providers!=null&&providers.size()>0){
			return providers.get(0);
		}
		return new ShippingProvider();
	}
}
