package com.intramirror.product.core.impl.category;

import com.intramirror.common.parameter.EnabledType;
import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.CategoryMapper;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
        List<Map<String, Object>> categoryMaps = categoryMapper.selCategoryByConditions(category);
        List<Category> categories = this.convertMapToCategoryWithPath(categoryMaps, new ArrayList<Category>(), -1L, null);
        return categories;
    }

    @Override
    public Map<Long,Category> queryAllCategoryName() {
        Category category = new Category();
        category.setEnabled(EnabledType.USED);
        List<Map<String, Object>> categoryMaps = categoryMapper.selCategoryByConditions(category);
        Map<Long,Category> categories = new HashMap<>();
        categories = this.getAllCategoryName(categoryMaps, categories, -1L, null);
        return categories;
    }

    @Override
    public List<Category> listAllCategoryByConditions() {
        Category category = new Category();
        category.setEnabled(EnabledType.USED);
        return categoryMapper.listAllCategoryByConditions(category);
    }

    // 封装Category
    private List<Category> convertMapToCategoryWithPath(List<Map<String, Object>> mapList, List<Category> categorys, Long bl_parentId, String parentPath) {
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            Long parentId = Long.parseLong(map.get("parent_id").toString());
            String name = map.get("name").toString();
            Long categoryId = Long.parseLong(map.get("category_id").toString());

            if (bl_parentId.equals(parentId)) {
                Category category = new Category();
                category.setName(name);
                category.setCategoryId(categoryId);
                category.setLevel(Byte.parseByte(map.get("level").toString()));
                category.setParentId(parentId);

                if (parentPath != null) {
                    category.setCategoryPath(parentPath + "=>" + name);
                } else {
                    category.setCategoryPath(name);
                }

                //				mapList.remove(i);
                category.setChildren(this.convertMapToCategoryWithPath(mapList, new ArrayList<Category>(), categoryId, category.getCategoryPath()));
                categorys.add(category);
            }
        }
        return categorys;
    }

    // 封装Category  获取每个分类的名称（包含父级）
    private Map<Long,Category> getAllCategoryName(List<Map<String, Object>> mapList,  Map<Long,Category> categoryMap, Long bl_parentId, String parentPath) {
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            Long parentId = Long.parseLong(map.get("parent_id").toString());
            String name = map.get("name").toString();
            Long categoryId = Long.parseLong(map.get("category_id").toString());

            if (bl_parentId.equals(parentId)) {
                Category category = new Category();
                category.setName(name);
                category.setCategoryId(categoryId);
                category.setLevel(Byte.parseByte(map.get("level").toString()));
                category.setParentId(parentId);

                if (parentPath != null) {
                    category.setCategoryPath(parentPath + "=>" + name);
                } else {
                    category.setCategoryPath(name);
                }
                categoryMap.put(category.getCategoryId(),category);
                this.getAllCategoryName(mapList, categoryMap, categoryId, category.getCategoryPath());
            }
        }
        return categoryMap;
    }

    // 封装Category
    private List<Category> convertMapToCategory(List<Map<String, Object>> mapList, List<Category> categorys, Long bl_parentId) {
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            Long parentId = Long.parseLong(map.get("parent_id").toString());
            String name = map.get("name").toString();
            Long categoryId = Long.parseLong(map.get("category_id").toString());

            if (bl_parentId.equals(parentId)) {
                Category category = new Category();
                category.setName(name);
                category.setCategoryId(categoryId);
                category.setLevel(Byte.parseByte(map.get("level").toString()));
                category.setParentId(parentId);

                //				mapList.remove(i);
                category.setChildren(this.convertMapToCategory(mapList, new ArrayList<Category>(), categoryId));
                categorys.add(category);
            }
        }
        return categorys;
    }

    @Override
    public List<Map<String, Object>> queryCategoryListByConditions(Category category) throws Exception {
        return categoryMapper.selCategoryByConditions(category);
    }

    @Override
    public List<Map<String, Object>> getMappingCategoryInfoByCondition(Map<String, Object> param) {

        return categoryMapper.getMappingCategoryInfoByCondition(param);
    }

    @Override
    public List<Map<String, Object>> getCategoryInfoByCondition(Map<String, Object> map) {
        return categoryMapper.getCategoryInfoByCondition(map);
    }

    @Override
    public List<Map<String, Object>> getCategoryByCondition(Map<String, Object> map) {
        return categoryMapper.getCategoryByCondition(map);
    }

    @Override
    public List<Map<String, Object>> getSubCidByPid(Long categoryId) {
        return categoryMapper.getSubCidByPid(categoryId);
    }
}
