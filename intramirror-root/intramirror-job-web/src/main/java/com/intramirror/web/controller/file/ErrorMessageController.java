package com.intramirror.web.controller.file;

import com.alibaba.fastjson15.JSONObject;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.util.ApiDataFileUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.utils.FileUtils;
import pk.shoplus.service.CategoryService;
import pk.shoplus.util.MapUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/20.
 */
@Controller
@RequestMapping("/errormessage")
public class ErrorMessageController {

    // logger
    private static final Logger logger = Logger.getLogger(ErrorMessageController.class);

    @ResponseBody
    @RequestMapping("/select")
    public JSONObject getMessage(@Param("api_error_processing_id")String api_error_processing_id){
        Connection conn = null;
        try{
            // select path info
            conn = DBConnector.sql2o.beginTransaction();
            CategoryService categoryService = new CategoryService(conn);
            String selectPathSQL = "select aep.error_id,va.file_location from api_error_processing aep \n" +
                    "inner join vendor_api va on(aep.vendor_api_id = va.vendor_api_id)\n" +
                    "where aep.enabled = 1 and va.enabled = 1 and aep.api_error_processing_id = '"+api_error_processing_id+"'";
            logger.info("ErrorMessageControllerGetMessage,executeSQL,selectPathSQL:"+selectPathSQL);
            List<Map<String, Object>> pathMap =  categoryService.executeSQL(selectPathSQL);
            logger.info("ErrorMessageControllerGetMessage,executeSQL,pathMap:"+ JSONObject.toJSONString(pathMap)+",selectPathSQL:"+selectPathSQL);
            if(conn!=null){conn.close();}

            String error_id = pathMap.get(0).get("error_id").toString();
            String file_location = pathMap.get(0).get("file_location").toString();
            String datePath = error_id.substring(5,13);

            String path = file_location+"/"+datePath+"/"+error_id+".txt";

            String body = FileUtils.file2String(new File(path),"UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(body);
            logger.info("ErrorMessageControllerGetMessage,outputParams,api_error_processing_id:"+api_error_processing_id+",body:"+body);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            if(conn!=null) {conn.close();}
        }
        return null;
    }

    public static void main(String[] args) {
        String error = "error201709091234";

        System.out.println();
    }
}
