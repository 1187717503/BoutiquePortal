/**
 * 
 */
package com.intramirror.web.controller.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sql2o.Connection;

import com.google.gson.Gson;

import pk.shoplus.DBConnector;
import pk.shoplus.model.ApiConfiguration;
import pk.shoplus.model.Category;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.ApiConfigurationService;
import pk.shoplus.service.CategoryService;

/**
 * @author yuan
 *
 */
@CrossOrigin
@Controller
@RequestMapping("/category")
public class CategoryController {
	
	private final Logger logger = Logger.getLogger(CategoryController.class);
	
	
	/**
	 * 查询商品信息
	 * @param map
	 * @return ResultMessage
	 */
	@RequestMapping(value="getCategory", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCategory(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> result = new HashMap<>();
		logger.info("getcategory param : "+ new Gson().toJson(request.getParameterMap()));
		//参数校验
		if (null == request.getParameter("Version") || StringUtils.isBlank(request.getParameter("Version").toString())){
			logger.info("Version cannot be null");
			result.put("E001", "Version cannot be null");
			return result;
		}
		if (null == request.getParameter("StoreID") || StringUtils.isBlank(request.getParameter("StoreID").toString())){
			logger.info("StoreID cannot be null");
			result.put("E001", "StoreID cannot be null");
			return result;
		}
		String version = request.getParameter("Version").toString();
		//查询配置
		String storeID = request.getParameter("StoreID").toString();
		int status = StatusType.FAILURE;
        try (Connection conn = DBConnector.sql2o.open()) {
        	ApiConfigurationService apiConfigurationService = new ApiConfigurationService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("store_code", storeID);
            condition.put("system", "intramirror");
            logger.info("param getMappingByCondition " +new Gson().toJson(condition));
            ApiConfiguration apiConfiguration = apiConfigurationService.getMappingByCondition(condition, null);
            logger.info("getMappingByCondition result " +new Gson().toJson(apiConfiguration));
            if (null != apiConfiguration && "1.0".equals(version)) {
                   Map<String, Object> map = populateResult1_0(response, status, conn);
                   return map;
            }
            //如果查询失败，返回状态-1
            result.put("status", StatusType.FAILURE);
        }catch (Exception e) {
			e.printStackTrace();
			logger.error("Error Message : " + e.getMessage());
			result.put("ERROR", e.getMessage());
			result.put("status", StatusType.DATABASE_ERROR);
		}
		
		return result;
	}
	
	/**
	 * 查询商品树
	 * @param res
	 * @param status
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> populateResult1_0(HttpServletResponse res , Integer status, Connection conn) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map <String, Object>> categoryList = null;
        // 根据传入的email 查询是否存在此email
        CategoryService categoryService = new CategoryService(conn);
        Map<String, Object> condition = new HashMap<>();
        condition.put("enabled", true);
        condition.put("level", 1);
        List<Category> firstCategoryList = categoryService.getCategoryListByCondition(condition);

        Map<String, Object> condition2 = new HashMap<>();
        condition2.put("enabled", true);
        condition2.put("level", 2);
        List<Category> secondCategoryList = categoryService.getCategoryListByCondition(condition2);

        Map<String, Object> condition3 = new HashMap<>();
        condition3.put("enabled", true);
        condition3.put("level", 3);
        List<Category> threeCategoryList = categoryService.getCategoryListByCondition(condition3);

        categoryList = this.convertListToTreeMap(firstCategoryList, secondCategoryList, threeCategoryList);
        status = StatusType.SUCCESS;

        result.put("status", status);
        result.put("data", categoryList);

        return result;
    }

	/**
	 * 封装数据返回树形数据
	 * @param first
	 * @param second
	 * @param three
	 * @return List<Map <String, Object>>
	 */
    private List<Map <String, Object>> convertListToTreeMap (List<Category> first, List<Category> second, List<Category> three) {
        List<Map <String, Object>> list = null;
        if (null != first && first.size() > 0) {
            list = new ArrayList<>(first.size());
            Map<String, Object> firstMap = null;
            Map<String, Object> secondMap = null;
            Map<String, Object> threeMap = null;

            for (Category firstCategory : first) {
                firstMap = new HashMap<>();
                Long categoryId = firstCategory.getCategory_id();
                firstMap.put("name", firstCategory.getName());
                firstMap.put("category_id", categoryId);
                List childrenList = new ArrayList<>();
                for (Category secondCategory : second) {
                    secondMap = new HashMap<>();
                    Long secondCategoryId = secondCategory.getCategory_id();
                    secondMap.put("name", secondCategory.getName());
                    secondMap.put("category_id", secondCategoryId);
                    Long firstCategoryId = secondCategory.getParent_id();
                    List secondChildrenList = new ArrayList<>();
                    for (Category threeCategory : three) {
                        threeMap = new HashMap<>();
                        threeMap.put("name", threeCategory.getName());
                        threeMap.put("category_id", threeCategory.getCategory_id());
                        Long secondCategoryId2 = threeCategory.getParent_id();
                        if (secondCategoryId.longValue() == secondCategoryId2.longValue()) {
                            secondChildrenList.add(threeMap);
                        }
                    }
                    secondMap.put("children", secondChildrenList);

                    if (firstCategoryId.longValue() == categoryId.longValue()) {
                        childrenList.add(secondMap);
                    }
                }

                firstMap.put("children", childrenList);
                list.add(firstMap);
            }
        }

        return list;
    }

}
