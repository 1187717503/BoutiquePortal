package com.intramirror.product.api.service.category;

import com.intramirror.product.api.model.Category;

import java.util.List;

/**
 * Created by dingyifan on 2017/7/19.
 */
public interface ICategoryService {

    public List<Category> queryActiveCategorys() throws Exception;
}
