package com.intramirror.web.controller.api.filippo;

import com.alibaba.fastjson15.JSONObject;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/18.
 */
@Controller
@RequestMapping("filippo_stock")
public class FilippoUpdateByStockController  implements InitializingBean {

    // logger
    private static final Logger logger = Logger.getLogger(FilippoUpdateByStockController.class);

    // init params
    private Map<String,Object> paramsMap;

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    // 原始文件名称前缀
    private static final String origin = "origin";

    // 新文件名称前缀
    private static final String revised = "revised";

    // 文件名称后缀
    private static final String suffix = ".txt";

    @ResponseBody
    @RequestMapping("/syn_product")
    public Map<String,Object> execute(@Param(value = "name")String name){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        logger.info("FilippoUpdateByStockControllerExecute,inputParams,name:"+name);

        // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status", StatusType.FAILURE).putData("info","name is null").getMap();
        }

        // get param
        Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
        String vendor_id = param.get("vendor_id").toString();
        String url = param.get("url").toString();
        String filippo_compare_path = param.get("filippo_compare_path").toString();
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

        // file path
        String originPath = filippo_compare_path+"/"+origin+suffix;
        String revisedPath = filippo_compare_path+"/"+revised+suffix;

        try {
            String responceData = getPostRequestUtil.requestMethod(GetPostRequestService.HTTP_GET,url,null);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("FilippoUpdateByStockControllerExecute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("FilippoUpdateByStockControllerExecute,outputParams,mapUtils:"+ JSONObject.toJSONString(mapUtils));
        return mapUtils.getMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // filippo
        Map<String,Object> filippo_increment_updateproduct = new HashMap<>();
        filippo_increment_updateproduct.put("url","http://stat.filippomarchesani.net:2060/gw/collect.php/?p=1n3r4&o=intra&q=getqty");
        filippo_increment_updateproduct.put("vendor_id","17");
        filippo_increment_updateproduct.put("filippo_compare_path","/mnt/compare/filippo");
        filippo_increment_updateproduct.put("fileUtils",new ApiDataFileUtils("filippo","filippo增量更新库存"));

        // put initData
        paramsMap = new HashMap<>();
        paramsMap.put("filippo_increment_updateproduct",filippo_increment_updateproduct);
    }
}
