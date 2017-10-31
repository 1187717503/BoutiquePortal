package com.intramirror.web.controller.api.atelier.stock;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.controller.api.atelier.AtelierUpdateByProductService;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dingyifan on 2017/10/31.
 */
@Controller
@RequestMapping("/atelier/synStock")
public class AtelierStockController {

    /** logger */
    private static Logger logger = Logger.getLogger(AtelierStockController.class);

    @Resource(name = "updateStockService")
    private IUpdateStockService iUpdateStockService;

    @Autowired
    private AtelierUpdateByProductService atelierUpdateByProductService;

    @RequestMapping(value = "/setZero",method = RequestMethod.GET)
    @ResponseBody
    public String setZero(@Param(value = "store_code")String store_code){
        try {
            if(StringUtils.isBlank(store_code)) {
                return "store_code is null.";
            }

            Map<String,Object> initParams = atelierUpdateByProductService.getParamsMap();
            if(initParams.get(store_code) == null) {
                return "initParams is null.";
            }

            Map<String,Object> vendorMap = (Map<String, Object>) initParams.get(store_code);
            Long vendor_id = Long.parseLong(vendorMap.get("vendor_id").toString());

            logger.info("AtelierStockControllerSetZero,zeroClearing,start,vendorMap:"+ JSONObject.toJSONString(vendorMap));
            iUpdateStockService.zeroClearing(vendor_id);
            logger.info("AtelierStockControllerSetZero,zeroClearing,end,vendorMap:"+ JSONObject.toJSONString(vendorMap));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierStockControllerSetZero,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return "success";
    }
}
