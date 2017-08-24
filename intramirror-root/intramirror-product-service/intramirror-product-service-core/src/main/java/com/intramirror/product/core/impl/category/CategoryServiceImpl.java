package com.intramirror.product.core.impl.category;

import com.intramirror.common.parameter.EnabledType;
import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.CategoryMapper;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/19.
 */
@Service(value = "productCategoryServiceImpl")
public class CategoryServiceImpl extends BaseDao implements ICategoryService {

    private CategoryMapper categoryMapper;

    @Override
    public void init() {
        categoryMapper = this.getSqlSession().getMapper(CategoryMapper.class);
    }

    @Override
    public List<Category> queryActiveCategorys() {
        Category category = new Category();
        category.setEnabled(EnabledType.USED);
        List<Map<String,Object>> categoryMaps = categoryMapper.selCategoryByConditions(category);
        List<Category> categories = this.convertMapToCategory(categoryMaps,new ArrayList<Category>(),-1L);
        return categories;
    }

    // 封装Category
    private List<Category> convertMapToCategory(List<Map<String,Object>> mapList,List<Category> categorys,Long bl_parentId){
        for(int i = 0;i < mapList.size();i++) {
            Map<String,Object> map = mapList.get(i);
            Long parentId = Long.parseLong(map.get("parent_id").toString());
            String name = map.get("name").toString();
            Long categoryId = Long.parseLong(map.get("category_id").toString());
            int level = Integer.parseInt(map.get("level").toString());

            if(bl_parentId.equals(parentId)) {
                Category category = new Category();
                category.setName(name);
                category.setCategoryId(categoryId);

//				mapList.remove(i);
                category.setChildren(this.convertMapToCategory(mapList, new ArrayList<Category>(), categoryId));
                categorys.add(category);
            }
        }
        return categorys;
    }

	@Override
	public List<Map<String,Object>> queryCategoryListByConditions(Category category)
			throws Exception {
		return categoryMapper.selCategoryByConditions(category);
	}

	@Override
	public List<Map<String, Object>> getMappingCategoryInfoByCondition(
			Map<String, Object> param) {
		
		return categoryMapper.getMappingCategoryInfoByCondition(param);
	}
}
