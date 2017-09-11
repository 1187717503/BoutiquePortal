package com.intramirror.product.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intramirror.product.api.model.SkuStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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

	public int cancelSkuStore (Long shopProductSkuId) {
		return skuStoreMapper.cancelSkuStore(shopProductSkuId);
	}
	/**
	 * 根据skuId修改确认库存
	 * @param shopProductSkuId
	 * @return
	 */
	public void updateConfirmStoreByShopProductSkuId (Long shopProductSkuId) throws Exception {

		List<SkuStore> skuStoreList = skuStoreMapper.selectSkuStoreByShopProductSkuId(shopProductSkuId);
		SkuStore skuStore = null;
		if (null != skuStoreList && skuStoreList.size() > 0) {
			skuStore = skuStoreList.get(0);
		}

		try {
			if (null != skuStore) {
				Long skuStoreId = skuStore.getSkuStoreId();
				skuStoreMapper.confirmSkuStore(skuStoreId);
				if (skuStore.getStore() < 0) {
					skuStoreMapper.confirmSkuStoreByNegativeStore(skuStoreId);
				}
			}
		} catch (DataIntegrityViolationException e) {
			logger.error("店铺商品SKU "+shopProductSkuId+" 扣减库存异常,reserved,confirmed不可为负数!");
			throw e;
		}
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
