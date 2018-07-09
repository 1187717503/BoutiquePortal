package com.intramirror.product.core.impl;

import com.intramirror.product.api.service.IProductExceptionService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductExceptionMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductExceptionServiceImpl extends BaseDao implements IProductExceptionService {

    private static Logger logger = LoggerFactory.getLogger(ProductExceptionServiceImpl.class);

    private ProductExceptionMapper productExceptionMapper;

    public void init() {
        productExceptionMapper = this.getSqlSession().getMapper(ProductExceptionMapper.class);
    }

    @Override
    public int saveProductException(Map<String, Object> map) throws Exception {
        return productExceptionMapper.saveProductException(map);
    }

    @Override
    public int updateProductException(Map<String, Object> map) throws Exception {
        return productExceptionMapper.updateProductException(map);
    }

    @Override
    public List<Map<String, Object>> selectByProductAndSkuId(Map<String, Object> map) {
        return productExceptionMapper.selectByProductAndSkuId(map);
    }
}
