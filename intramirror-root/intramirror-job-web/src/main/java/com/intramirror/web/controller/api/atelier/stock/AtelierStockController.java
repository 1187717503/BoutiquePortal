package com.intramirror.web.controller.api.atelier.stock;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.contants.RedisKeyContants;
import com.intramirror.web.controller.api.atelier.AtelierUpdateByProductService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.service.RedisService;

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

    private static RedisService redisService = RedisService.getInstance();

    @RequestMapping(value = "/setZero",method = RequestMethod.GET)
    @ResponseBody
    public String setZero(@Param(value = "store_code")String store_code){
        logger.info("AtelierStockController,setZero,inputParams,store_code:"+store_code);
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


            if(this.timeInterval(vendor_id)) {
                logger.info("AtelierStockControllerSetZero,zeroClearing,start,vendorMap:"+ JSONObject.toJSONString(vendorMap));
                iUpdateStockService.zeroClearing(vendor_id);
                logger.info("AtelierStockControllerSetZero,zeroClearing,end,vendorMap:"+ JSONObject.toJSONString(vendorMap));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierStockControllerSetZero,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return "success";
    }

    public boolean timeInterval(Long vendor_id) throws Exception {
        // 如果10分钟内没有收到全量更新库存清零
        String key = RedisKeyContants.atelier_all_product_zero+vendor_id;
        String result = redisService.getKey(key);

        logger.info("AtelierStockControllerSetZero,timeInterval,key:"+key+",result:"+result);

        if(StringUtils.isBlank(result) || result.equals("[]")) {
            return false;
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        Date lastDate = timeFormat.parse(result);
        logger.info("AtelierStockControllerSetZero,打印,today:"+timeFormat.format(today) +",lastDate:"+timeFormat.format(lastDate));

        long from = today.getTime();
        long to = lastDate.getTime();
        int minutes = (int) ((to - from)/(1000 * 60));
        int absMinutes = Math.abs(minutes);

        if(absMinutes > 10 && absMinutes < 20) {
            logger.info("AtelierStockControllerSetZero,true,today:"+timeFormat.format(today) +",lastDate:"+timeFormat.format(lastDate));
            return true;
        }
        return false;
    }
}
