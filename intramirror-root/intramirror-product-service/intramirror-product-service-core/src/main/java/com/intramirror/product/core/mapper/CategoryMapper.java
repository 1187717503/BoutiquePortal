package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.model.CategoryWithBLOBs;

import java.util.List;
import java.util.Map;

public interface CategoryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table category
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long categoryId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table category
     * @mbggenerated
     */
    int insert(CategoryWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table category
     * @mbggenerated
     */
    int insertSelective(CategoryWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table category
     * @mbggenerated
     */
    CategoryWithBLOBs selectByPrimaryKey(Long categoryId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table category
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(CategoryWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table category
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(CategoryWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table category
     * @mbggenerated
     */
    int updateByPrimaryKey(Category record);

    List<Map<String, Object>> selCategoryByConditions(Category param);

    /**
     * 根据条件获取相关信息
     * @param boutique_first_category,boutique_second_category,boutique_third_category
     * @return
     */
    List<Map<String, Object>> getMappingCategoryInfoByCondition(Map<String, Object> param);

    /**
     * 根据条件查询商品信息
     * @param map
     * @return
     */
    List<Map<String, Object>> getCategoryInfoByCondition(Map<String, Object> map);

    List<Category> listAllCategoryByConditions(Category param);

    /**
     * 根据条件查询商品信息
     * @param map
     * @return
     */
    List<Map<String, Object>> getCategoryByCondition(Map<String, Object> map);

    List<Category> listSubCategoryByCategoryId(Long categoryId);

}