package com.intramirror.product.core.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.intramirror.common.Helper;
import com.intramirror.product.api.model.SkuStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SkuStoreMapper;

@Service
public class SkuStoreServiceImpl extends BaseDao implements ISkuStoreService {

    private static Logger logger = LoggerFactory.getLogger(SkuStoreServiceImpl.class);

    private SkuStoreMapper skuStoreMapper;

    public void init() {
        skuStoreMapper = this.getSqlSession().getMapper(SkuStoreMapper.class);
    }

    /**
     * 根据SkuId修改库存相关信息
     *
     * @param statusType-订单的状态
     *         skuid
     * @return
     */
    public int updateBySkuId(int statusType, long skuid) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("statusType", statusType);
        param.put("skuId", skuid);
        return skuStoreMapper.updateBySkuId(param);
    }

    public int cancelSkuStore(Long shopProductSkuId) {
        return skuStoreMapper.cancelSkuStore(shopProductSkuId);
    }

    /**
     * 根据skuId修改确认库存
     *
     * @param shopProductSkuId
     * @return
     */
    public void updateConfirmStoreByShopProductSkuId(Long shopProductSkuId) throws Exception {

        List<SkuStore> skuStoreList = skuStoreMapper.selectSkuStoreByShopProductSkuId(shopProductSkuId);
        SkuStore skuStore = null;
        if (null != skuStoreList && skuStoreList.size() > 0) {
            skuStore = skuStoreList.get(0);
            logger.info("订单商品 " + shopProductSkuId + " 确认库存前库存情况 :" + JSON.toJSONString(skuStore));
        }

        try {
            if (null != skuStore) {
                Long skuStoreId = skuStore.getSkuStoreId();
                skuStoreMapper.confirmSkuStore(skuStoreId);
                logger.info("订单商品 " + shopProductSkuId + " 确认库存执行 : s.store = s.store + 1,s.confirmed = s.confirmed - 1");
                if (skuStore.getStore() < 0) {
                    skuStoreMapper.confirmSkuStoreByNegativeStore(skuStoreId);
                    logger.info("订单商品 " + shopProductSkuId + " 确认库存,展示库存小于零时执行 : s.store = s.store + 1,s.confirmed = s.confirmed - 1");
                }
            }
        } catch (DataIntegrityViolationException e) {
            logger.error("店铺商品SKU " + shopProductSkuId + " 扣减库存异常,reserved,confirmed不可为负数!");
            throw e;
        }
    }

    /**
     * 根据shopProductSkuId 查询SKUid
     *
     * @param shopProductSkuId
     * @return
     */
    public Long selectSkuIdByShopProductSkuId(Long shopProductSkuId) {
        return skuStoreMapper.selectSkuIdByShopProductSkuId(shopProductSkuId);
    }

    @Override
    public void updateSkuStoreReserved(List<String> reservedList, String[] skuIdString, List<Long> skuIds) throws Exception {
        List<Map<String, Object>> skuIdList = skuStoreMapper.getSkuStoreBySkuId(skuIdString);
        if (skuIdList.size() > 0 && Helper.checkNotNull(skuIdList.get(0))) {
            for (int i = 0; i < skuIds.size(); i++) {
                BigDecimal store = new BigDecimal(skuIdList.get(i).get("store").toString());
                BigDecimal reservedTwo = new BigDecimal(reservedList.get(i));
                if (store.compareTo(reservedTwo) == -1) {
                    throw new RuntimeException("Lack of stock.商品库存不足！");
                }
                Long skuId = skuIds.get(i);
                skuStoreMapper.batchExecuteBySql(reservedTwo, skuId);
            }
        }
    }

    @Override
    public List<Map<String, Object>> listSkuStoreByProductId(Long productId) {
        return skuStoreMapper.listSkuStoreByProductId(productId);
    }

    @Override
    public List<Map<String, Object>> listSkuStoreByProductList(List<Map<String, Object>> products) {
        return skuStoreMapper.listSkuStoreByProductList(products);
    }

    @Override
    public List<Map<String, Object>> listStockByProductList(List<Map<String, Object>> products) {
        return skuStoreMapper.listStockByProductList(products);
    }

}
