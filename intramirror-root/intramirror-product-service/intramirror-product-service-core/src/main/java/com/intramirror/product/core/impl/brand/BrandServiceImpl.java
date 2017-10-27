package com.intramirror.product.core.impl.brand;

import com.intramirror.common.parameter.EnabledType;
import com.intramirror.product.api.model.Brand;
import com.intramirror.product.api.service.brand.IBrandService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.BrandMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/19.
 */
@Service(value = "productBrandServiceImpl")
public class BrandServiceImpl extends BaseDao implements IBrandService {

    private static Logger logger = LoggerFactory.getLogger(BrandServiceImpl.class);

    private BrandMapper brandMapper;

    @Override
    public void init() {
        brandMapper = this.getSqlSession().getMapper(BrandMapper.class);
    }

    @Override
    public List<Map<String, Object>> queryActiveBrand() {
        Brand brand = new Brand();
        brand.setEnabled(EnabledType.USED);
        List<Map<String, Object>> brands = brandMapper.selBrandByConditions(brand);
        Collections.sort(brands, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Map<String, Object> map1 = (Map<String, Object>) o1;
                Map<String, Object> map2 = (Map<String, Object>) o2;
                String name1 = map1.get("english_name").toString().substring(0, 1);
                String name2 = map2.get("english_name").toString().substring(0, 1);
                return name1.compareTo(name2);
            }
        });
        return brands;
    }

    @Override
    public List<Map<String, Object>>  listActiveBrand(){
       return brandMapper.listActiveBrand();
    }

    @Override
    public Brand getBrandById(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

}
