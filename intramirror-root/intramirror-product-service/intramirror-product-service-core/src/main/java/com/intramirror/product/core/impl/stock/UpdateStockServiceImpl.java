package com.intramirror.product.core.impl.stock;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductMapper;
import com.intramirror.product.core.mapper.SkuStoreMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dingyifan on 2017/8/2.
 */
@Service(value = "updateStockService")
public class UpdateStockServiceImpl extends BaseDao implements IUpdateStockService{

    private static final Logger logger = Logger.getLogger(UpdateStockServiceImpl.class);

    private ProductMapper productMapper;

    private SkuStoreMapper skuStoreMapper;

    @Override
    public ResultMessage zeroClearing(Long vendorId) {
        try {
            logger.info("UpdateStockServiceImplZeroClearing,inputParams,vendorId:"+vendorId);

            if(vendorId == null) {
                return ResultMessage.getInstance().errorStatus().addMsg("vendor_id is null.");
            }

            Map<String,Object> updateStockMap = new HashMap<>();
            updateStockMap.put("vendor_id",vendorId);
            updateStockMap.put("date", DateUtils.getStrDate(new Date())+" 00:00:00");

            logger.info("UpdateStockServiceImplZeroClearing,start,outputParams,vendorId:"+vendorId+",updateStockMap:"+ JSONObject.toJSONString(updateStockMap));
            skuStoreMapper.zeroClearing(updateStockMap);
            logger.info("UpdateStockServiceImplZeroClearing,end,outputParams,vendorId:"+vendorId+",updateStockMap:"+ JSONObject.toJSONString(updateStockMap));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("UpdateStockServiceImplZeroClearing,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return null;
    }

    @Override
    public void init() {
        productMapper = this.getSqlSession().getMapper(ProductMapper.class);
        skuStoreMapper = this.getSqlSession().getMapper(SkuStoreMapper.class);
    }
}
