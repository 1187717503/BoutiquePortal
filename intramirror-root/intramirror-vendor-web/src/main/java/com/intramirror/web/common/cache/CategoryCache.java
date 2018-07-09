package com.intramirror.web.common.cache;

import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.service.category.ICategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 2017/10/23.
 *
 * @author YouFeng.Zhu
 */

@Component
public class CategoryCache {

    private final static Logger LOGGER = LoggerFactory.getLogger(CategoryCache.class);

    private static HashMap<Long, Category> categoryHashMap = new HashMap<>();
    private static final String NO_SUCH_CATEGORY = "No such category";
    private static final int MAX_CATEGORY_LEVEL = 3;

    @Autowired
    private ICategoryService iCategoryService;

    private void init() {
        try {
            List<Category> categories = iCategoryService.listAllCategoryByConditions();
            mapCategory(categories, categoryHashMap);
        } catch (Exception e) {
            LOGGER.error("Unexcepted Exception: {}", e.getMessage(), e);
        }
    }

    private void mapCategory(List<Category> categories, HashMap<Long, Category> categoryMap) {
        for (Category category : categories) {
            categoryMap.put(category.getCategoryId(), category);
            //            if (categoryTree.containsKey(category.getParentId())) {
            //                categoryTree.get(category.getParentId()).add(category);
            //            } else {
            //                List<Category> categoryList = new LinkedList<>();
            //                categoryList.add(category);
            //                categoryTree.put(category.getParentId(), categoryList);
            //            }

        }
        for (Category category : categories) {
            Long parentId = category.getParentId();
            if (parentId < 0) {
                continue;
            }
            Category parent = categoryMap.get(parentId);
            if (parent.getChildren() == null) {
                List<Category> list = new ArrayList<>();
                list.add(category);
                parent.setChildren(list);
            } else {
                parent.getChildren().add(category);
            }

        }
    }

    public List<Long> getAllChildCategory(Long categoryId) {

        List<Long> categoryList = new ArrayList<>();
        Category category = categoryHashMap.get(categoryId);

        if (category == null) {
            categoryList.add(categoryId);
            return categoryList;
        }

        List<Category> parent = new ArrayList<>();
        parent.add(category);
        fetchChildCategory(parent, categoryList);
        LOGGER.info("{}", categoryList);
        return categoryList;
    }

    private void fetchChildCategory(List<Category> parentList, List<Long> categoryList) {

        List<Category> newCategoryList = new ArrayList<>();
        for (Category parent : parentList) {
            LOGGER.info("{}", parent.getChildren());
            if (parent.getChildren() == null) {
                newCategoryList.add(parent);
            } else {
                newCategoryList.addAll(parent.getChildren());
            }

        }
        if (newCategoryList.get(0).getChildren() == null) {
            for (Category category : newCategoryList) {
                categoryList.add(category.getCategoryId());
            }
            return;
        } else {
            fetchChildCategory(newCategoryList, categoryList);
        }
    }

    public String getAbsolutelyCategoryPath(Long categoryId) {

        StringBuilder sb = new StringBuilder();
        LinkedList<Category> pathCategory = new LinkedList<>();
        Long key = categoryId;
        do {
            Category category = categoryHashMap.get(key);
            if (category == null) {
                return NO_SUCH_CATEGORY;
            }
            pathCategory.addFirst(category);
            key = category.getParentId();
        } while (key != null && key > 0);

        for (Category category : pathCategory) {
            sb.append(category.getName());
            sb.append(" > ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public Category getRootCategory(Long categoryId) {
        Long key = categoryId;
        Category category;
        do {
            category = categoryHashMap.get(key);
            if (category == null) {
                return null;
            }
            key = category.getParentId();
        } while (key != null && key > 0);
        return category;
    }

}
