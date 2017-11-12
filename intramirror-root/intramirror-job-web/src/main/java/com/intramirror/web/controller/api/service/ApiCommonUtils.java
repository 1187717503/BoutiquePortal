package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.parameter.StatusType;

/**
 * Created by dingyifan on 2017/11/11.
 */
public class ApiCommonUtils {
    private static final Logger logger = Logger.getLogger(ApiCommonUtils.class);

    public static boolean isSuccessMap(Map<String,Object> resultMap){
        if(resultMap.get("status").toString().equals(StatusType.SUCCESS+"")) {
            return true;
        }
        return false;
    }

    public static boolean isErrorMap(Map<String,Object> resultMap){
        if(resultMap.get("status").toString().equals(StatusType.FAILURE+"")) {
            return true;
        }
        return false;
    }

    public static int ifUpdatePrice(BigDecimal oldPrice,BigDecimal newPrice){
        int rs = newPrice.compareTo(oldPrice);
        if(rs == 0) {
            return 0;
        }

        BigDecimal rangePrice = oldPrice.multiply(new BigDecimal(0.2));

        BigDecimal minPrice = oldPrice.subtract(rangePrice);
        BigDecimal maxPrice = oldPrice.add(rangePrice);

        int dy = newPrice.compareTo(minPrice);
        int xy = newPrice.compareTo(maxPrice);

        if(dy == 1 && xy == -1) {
            return 1;
        }
        return 2;
    }

    /** 返回错误消息 */
    public static Map<String,Object> errorMap(ApiErrorTypeEnum.errorType errorType,String key,String value){
        logger.info("ApiCommonUtils,errorMap,inputParams,errorType:"+errorType.getCode()+",key:"+key+",value:"+value);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("status", StatusType.FAILURE);
        resultMap.put("info",errorType.getDesc());
        resultMap.put("key",key);
        resultMap.put("value",value);
        resultMap.put("error_enum",errorType);
        logger.info("ApiCommonUtils,errorMap,outputParams,resultMap:" + JSONObject.toJSONString(resultMap));
        return resultMap;
    }

    public static Map<String,Object> successMap(){
        Map<String,Object> successMap = new HashMap<>();
        successMap.put("status",StatusType.SUCCESS);
        successMap.put("info","success");
        return successMap;
    }

    public static String escape(String str){
        if(StringUtils.isNotBlank(str)) {
            str = str.replaceAll("'","\\\\'");
        }
        return str;
    }
}
