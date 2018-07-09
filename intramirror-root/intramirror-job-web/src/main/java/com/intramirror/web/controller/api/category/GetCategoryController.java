package com.intramirror.web.controller.api.category;

import com.alibaba.fastjson15.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Helper;
import pk.shoplus.model.Category;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.CategoryService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Controller
@RequestMapping("/")
public class GetCategoryController implements InitializingBean {

    // logger
    private static final Logger logger = Logger.getLogger(GetCategoryController.class);

    // init params
    private Map<String,Object> paramsMap;

    @RequestMapping("/getCategory")
    @ResponseBody
    public Map<String,Object> getCategory(HttpServletRequest request){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        Connection conn = null;
        try {
            InputStream is = request.getInputStream();
            String storeID = request.getParameter("StoreID");
            String version = request.getParameter("Version");
            logger.info("GetCategoryController,inputParams,storeID:"+storeID+",version:"+version);

            if (Helper.isNullOrEmpty(version) || Helper.isNullOrEmpty(storeID)) {
                mapUtils.putData("status", StatusType.PARAM_EMPTY_OR_NULL);
            } else {

                if(version.equals("1.0")) {
                    conn = DBConnector.sql2o.open();
                    Map<String,Object> result = this.populateResult1_0(conn);
                    if(conn!=null){conn.close();}
                    logger.info("GetCategoryController,outParams,storeID:"+storeID+",version:"+version+",result:"+ JSONObject.toJSONString(result));
                    return result;
                }
            }
        } catch (Exception e) {
            mapUtils.putData("status", StatusType.DATABASE_ERROR);
            e.printStackTrace();
            logger.info("GetCategoryController,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            if(conn!=null){conn.close();}
        } finally {
            if(conn!=null){conn.close();}
        }
        return mapUtils.getMap();
    }

    private Map<String, Object> populateResult1_0(Connection conn) throws Exception {
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

        result.put("status", StatusType.SUCCESS);
        result.put("data", categoryList);

        return result;
    }

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

    @Override
    public void afterPropertiesSet() throws Exception {
        paramsMap = new HashMap<>();
        ThreadPoolExecutor executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        paramsMap.put("executor",executor);

        Map<String,Object> X4ZMP = new HashMap<>();
        X4ZMP.put("store_code","X4ZMP");
        X4ZMP.put("vendor_id","8");
        X4ZMP.put("vendor_name","Luciana Bari");
        X4ZMP.put("eventName","luciana");
        paramsMap.put("X4ZMP",X4ZMP);

        Map<String,Object> XIW2E = new HashMap<>();
        XIW2E.put("store_code","XIW2E");
        XIW2E.put("vendor_id","10");
        XIW2E.put("vendor_name","Dante 5");
        XIW2E.put("eventName","dante");
        paramsMap.put("XIW2E",XIW2E);

        Map<String,Object> UIWK2 = new HashMap<>();
        UIWK2.put("store_code","UIWK2");
        UIWK2.put("vendor_id","11");
        UIWK2.put("vendor_name","I Cinque Fiori");
        UIWK2.put("eventName","iCinque");
        paramsMap.put("UIWK2",UIWK2);

        Map<String,Object> ERS4S = new HashMap<>();
        ERS4S.put("store_code","ERS4S");
        ERS4S.put("vendor_id","12");
        ERS4S.put("vendor_name","Mimma Ninni");
        ERS4S.put("eventName","mimma");
        paramsMap.put("ERS4S",ERS4S);

        Map<String,Object> UEYHD = new HashMap<>();
        UEYHD.put("store_code","UEYHD");
        UEYHD.put("vendor_id","13");
        UEYHD.put("vendor_name","Di Pierro");
        UEYHD.put("eventName","diPierro");
        paramsMap.put("UEYHD",UEYHD);

        Map<String,Object> IEK7W = new HashMap<>();
        IEK7W.put("store_code","IEK7W");
        IEK7W.put("vendor_id","14");
        IEK7W.put("vendor_name","Gisa Boutique");
        IEK7W.put("eventName","gisa");
        paramsMap.put("IEK7W",IEK7W);

        Map<String,Object> WISE = new HashMap<>();
        WISE.put("store_code","WISE");
        WISE.put("vendor_id","18");
        WISE.put("vendor_name","Wise Boutique");
        WISE.put("eventName","wise");
        paramsMap.put("WISE",WISE);
    }
}
