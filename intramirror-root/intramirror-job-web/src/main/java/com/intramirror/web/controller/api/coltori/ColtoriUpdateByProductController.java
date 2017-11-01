package com.intramirror.web.controller.api.coltori;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.StringUtils;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dingyifan on 2017/11/1.
 */
@Controller
@RequestMapping("/coltori_product")
public class ColtoriUpdateByProductController implements InitializingBean {

    private static final Logger logger = Logger.getLogger(ColtoriUpdateByProductController.class);

    private Map<String,Object> paramsMap;

    @RequestMapping("/syn_product")
    @ResponseBody
    public Map<String, Object> execute(@Param(value = "name") String name) {
        try {
            if(StringUtils.isBlank(name) || paramsMap.get(name) == null) {
                return null;
            }

            Map<String,Object> paramMap = (Map<String, Object>) paramsMap.get(name);
            logger.info("ColtoriUpdateByProductController,Execute,inputParams,name:"+name+",paramsMap:"+ JSONObject.toJSONString(paramMap));
            String get_token_url = paramMap.get("get_token_url").toString();
            String get_product_url = paramMap.get("get_product_url").toString();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) paramMap.get("executor");
            ApiDataFileUtils apiDataFileUtils = (ApiDataFileUtils) paramMap.get("fileUtils");

            // get token



        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ColtoriUpdateByProductController,Execute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadPoolExecutor executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("get_token_url","https://api.orderlink.it/v2/user/token");
        paramMap.put("get_product_url","https://api.orderlink.it/v2/products");
        paramMap.put("fileUtils",new ApiDataFileUtils("coltori","product_all_update"));
        paramMap.put("executor",executor);

        paramsMap = new HashMap<>();
        paramsMap.put("coltori_product_all_update",paramMap);
    }
}
