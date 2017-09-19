package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson15.JSONObject;
import com.intramirror.product.api.service.IApiMqService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/3.
 */
@Controller
@RequestMapping("/")
public class AtelierUpdateProductController {

    private static Logger logger = Logger.getLogger(AtelierUpdateProductController.class);

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
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
            InputStream is = request.getInputStream();
            String body = IOUtils.toString(is, "utf-8");
            String storeID = request.getParameter("StoreID");
            String version = request.getParameter("Version");

            logger.info("AtelierFullUpdateProductControllerExecute,params,type:"+type+",storeID:"+storeID+",version:"+version+",body:"+body);
            Map<String,Object> resultMap = atelierUpdateByProductService.updateProduct(body,storeID,version,type);
            logger.info("AtelierFullUpdateProductControllerExecute,updateProduct,type:"+type+",resultMap:"+JSONObject.toJSONString(resultMap)+",storeID:"+storeID+",version:"+version+",body:"+body);

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierFullUpdateProductControllerExecute,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("ResponseStatus","2000")
                    .putData("ErrorCode","1")
                    .putData("ErrorMsg","500: E500: Update product failed:"+ExceptionUtils.getExceptionDetail(e))
                    .putData("TimeStamp",new Date());
            logger.info("AtelierFullUpdateProductControllerExecute,outputParams,mapUtils:"+JSONObject.toJSONString(mapUtils));
            return mapUtils.getMap();
        }
    }
}
