package com.intramirror.product.api.service.brand;

import com.intramirror.product.api.model.Brand;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/19.
 */
public interface IBrandService {

    /**
     * @return 获取有效品牌数据
     */
    List<Map<String, Object>> queryActiveBrand() throws Exception;

    Brand getBrandById(Long brandId);
}
