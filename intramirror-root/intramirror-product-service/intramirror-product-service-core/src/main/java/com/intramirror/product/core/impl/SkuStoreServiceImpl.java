package com.intramirror.product.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SkuStoreMapper;


@Service
public class SkuStoreServiceImpl extends BaseDao implements ISkuStoreService{
	
    private static Logger logger = LoggerFactory.getLogger(SkuStoreServiceImpl.class);

    private SkuStoreMapper skuStoreMapper;

    public void init() {
    	skuStoreMapper = this.getSqlSession().getMapper(SkuStoreMapper.class);
    }

    
	/**
	 * 根据SkuId修改库存相关信息
	 * @param statusType-订单的状态     skuid 
	 * @return
	 */
	public int updateBySkuId(int statusType,long skuid) {
    	Map<String,Object> param = new HashMap<String, Object>();
    	param.put("statusType", statusType);
    	param.put("skuId", skuid);
		return skuStoreMapper.updateBySkuId(param);
	}


	/**
	 * 根据shopProductSkuId 查询SKUid
	 * @param shopProductSkuId
	 * @return
	 */
	public Long selectSkuIdByShopProductSkuId(Long shopProductSkuId) {
		return skuStoreMapper.selectSkuIdByShopProductSkuId(shopProductSkuId);
	}
}
