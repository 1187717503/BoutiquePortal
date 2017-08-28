package com.intramirror.product.api.service.category;

import com.intramirror.product.api.model.Category;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/19.
 */
public interface ICategoryService {

    public List<Category> queryActiveCategorys() throws Exception;
    
    public List<Map<String,Object>> queryCategoryListByConditions(Category category) throws Exception;
    
	/**
	 * 根据条件获取相关信息
	 * @param boutique_first_category,boutique_second_category,boutique_third_category
	 * @return
	 */
	List<Map<String,Object>> getMappingCategoryInfoByCondition(Map<String,Object> param);
	
	/**
	 * 根据条件查询商品信息
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getCategoryInfoByCondition(Map<String, Object> map);
	
    
}
