package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.intramirror.web.contants.RedisKeyContants;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.service.RedisService;
import pk.shoplus.util.DateUtils;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/9/3.
 */
@Controller
@RequestMapping("/")
public class AtelierUpdateProductController {

    private static Logger logger = Logger.getLogger(AtelierUpdateProductController.class);

    private static RedisService redisService = RedisService.getInstance();

    @Resource(name = "atelierUpdateByProductService")
    private AtelierUpdateByProductService atelierUpdateByProductService;

    @RequestMapping(value = "/fullUpdateProduct",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> fullUpdateProduct(HttpServletRequest request){
        return this.execute(request,atelierUpdateByProductService.all_update_product);
    }

    @RequestMapping(value = "/createProduct",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> createProduct(HttpServletRequest request){
        return this.execute(request,atelierUpdateByProductService.create_product);
    }

    @RequestMapping(value = "/updateProduct",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> updateProduct(HttpServletRequest request){
        return this.execute(request,atelierUpdateByProductService.update_product);
    }

    private Map<String,Object> execute(HttpServletRequest request,String type) {
        long start = System.currentTimeMillis();
        Map<String,Object> resultMap = new HashMap<>();
        try {
            InputStream is = request.getInputStream();
            String body = IOUtils.toString(is, "utf-8");
            String storeID = request.getParameter("StoreID");
            String version = request.getParameter("Version");
            logger.info("AtelierFullUpdateProductControllerExecute,inputParams,type:"+type+",storeID:"+storeID+",version:"+version+",body:"+body+","+storeID+"_"+type);

            this.setRedisDate(type,storeID,body);

            JSONArray datas = this.parasJSONArray(body);

            if(datas == null) {
                logger.info("AtelierFullUpdateProductControllerExecute,params,type:"+type+",storeID:"+storeID+",version:"+version+",body:"+body+","+storeID+"_"+type);
                resultMap = atelierUpdateByProductService.updateProduct(body,storeID,version,type);
                logger.info("AtelierFullUpdateProductControllerExecute,updateProduct,type:"+type+",resultMap:"+JSONObject.toJSONString(resultMap)+",storeID:"+storeID+",version:"+version+",body:"+body+","+storeID+"_"+type);
            } else {
                for(int i=0,len=datas.size();i<len;i++){
                    JSONObject bodyJSON = datas.getJSONObject(i);
                    logger.info("AtelierFullUpdateProductControllerExecutes,params,type:"+type+",storeID:"+storeID+",version:"+version+",body:"+bodyJSON.toJSONString()+","+storeID+"_"+type);
                    resultMap = atelierUpdateByProductService.updateProduct(bodyJSON.toJSONString(),storeID,version,type);
                    logger.info("AtelierFullUpdateProductControllerExecutes,updateProduct,type:"+type+",resultMap:"+JSONObject.toJSONString(resultMap)+",storeID:"+storeID+",version:"+version+",body:"+bodyJSON.toJSONString()+","+storeID+"_"+type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierFullUpdateProductControllerExecute,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            resultMap.put("ResponseStatus","2000");
            resultMap.put("ErrorCode","1");
            resultMap.put("ErrorMsg","500: E500: Update product failed:"+ExceptionUtils.getExceptionDetail(e));
            resultMap.put("TimeStamp",new Date());
            logger.info("AtelierFullUpdateProductControllerExecute,outputParams,mapUtils:"+JSONObject.toJSONString(resultMap));
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,AtelierUpdateProductController_execute,start:"+start+",end:"+end+",time:"+(end-start));
        return resultMap;
    }

    public JSONArray parasJSONArray(String body){
        JSONArray datas = null;
        try {
            datas = JSONArray.parseArray(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    private void setRedisDate(String type,String storeID,String body){
        logger.info("AtelierUpdateProductController,setRedisDate,type:"+type+",storeID:"+storeID+",body:"+body);
        Map<String,Object> paramsMap = atelierUpdateByProductService.getParamsMap();

        if(!atelierUpdateByProductService.all_update_product.equals(type) || paramsMap == null || paramsMap.get(storeID) == null) {
            return;
        }
        Map<String,Object> paramMap = (Map<String, Object>) paramsMap.get(storeID);
        String vendor_id = paramMap.get("vendor_id").toString();
        String key = RedisKeyContants.atelier_all_product_zero+vendor_id;
        String value = DateUtils.getStrDate(new Date(),"yyyy-MM-dd HH:mm:ss");
        try {
            logger.info("AtelierUpdateProductController,start,setRedisDate,putRedis,key:"+key+",value:"+value+",body:"+body);
            redisService.put(key,value);
            logger.info("AtelierUpdateProductController,end,setRedisDate,putRedis,key:"+key+",value:"+value+",body:"+body);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateProductController,setRedisDate,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",type:"+type+",storeID:"+storeID+",body:"+body);
        }
    }
}
