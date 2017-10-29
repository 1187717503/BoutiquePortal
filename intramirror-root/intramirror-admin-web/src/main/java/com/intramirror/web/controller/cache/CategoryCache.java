package com.intramirror.web.controller.cache;

import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.service.category.ICategoryService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2017/10/23.
 *
 * @author YouFeng.Zhu
 */
@Component
public class CategoryCache {

    private final static Logger LOGGER = LoggerFactory.getLogger(CategoryCache.class);

    private static CategoryCache instance = null;
    private static HashMap<Long, Category> categoryHashMap = new HashMap<>();
    private static final String NO_SUCH_CATEGORY = "No such category";
    private boolean isInit = false;

    @Autowired
    private ICategoryService iCategoryService;

    private void init() {
        try {
            List<Category> categories = iCategoryService.listAllCategoryByConditions();
            mapCategory(categories, categoryHashMap);
            isInit = true;
        } catch (Exception e) {
            LOGGER.error("Unexcepted Exception: {}", e.getMessage(), e);
        }
    }

    private void mapCategory(List<Category> categories, HashMap<Long, Category> categoryMap) {
        for (Category category : categories) {
            categoryMap.put(category.getCategoryId(), category);
        }
    }

    public String getAbsolutelyCategoryPath(Long categoryId) {
        if (!isInit) {
            init();
        }

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

}
