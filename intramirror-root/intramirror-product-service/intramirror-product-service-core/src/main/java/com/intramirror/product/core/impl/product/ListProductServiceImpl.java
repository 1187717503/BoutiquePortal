package com.intramirror.product.core.impl.product;

import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.product.api.service.product.IListProductService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created on 2017/10/20.
 * @author YouFeng.Zhu
 */
@Service("listProductService")
public class ListProductServiceImpl extends BaseDao implements IListProductService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ListProductServiceImpl.class);

    ProductMapper productMapper;

    @Override
    public void init() {
        productMapper = this.getSqlSession().getMapper(ProductMapper.class);
    }

    @Override
    public List<Map<String, Object>> listProductService(SearchCondition searchCondition) {
        return productMapper.listProductDetailInfo(searchCondition);
    }

}
