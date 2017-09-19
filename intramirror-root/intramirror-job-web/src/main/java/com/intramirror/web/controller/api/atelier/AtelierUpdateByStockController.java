package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.ResultHelper;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Controller
@RequestMapping("/")
public class AtelierUpdateByStockController  implements InitializingBean {

    // logger
    private static Logger logger = Logger.getLogger(AtelierUpdateByStockController.class);

    // init params
    private Map<String,Object> paramsMap;

    @RequestMapping("/updateSKUStock")
    @ResponseBody
    public Map<String,Object> execute(HttpServletRequest request){
        logger.info("AtelierUpdateByStockControllerExecute,start");
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());

        try{
            // 获取入参
            InputStream is = request.getInputStream();
            String body = IOUtils.toString(is, "utf-8");
            JSONObject data = JSON.parseObject(body);
            String storeID = request.getParameter("StoreID");
            String version = request.getParameter("Version");
            logger.info("AtelierUpdateByStockControllerExecute,inputParams,storeID:"+storeID+",version:"+version+",body:"+body);

            // 校验入参
            if(StringUtils.isBlank(storeID)) {return ResultHelper.createErrorResult("1", "E001: Parameter StoreID is mandatory");}
            if (version == null || !version.equals("1.0")) {return ResultHelper.createErrorResult("1", "E001: Parameter Version is mandatory");}
            if (body == null) {return ResultHelper.createErrorResult("30", "E030: Empty Request");}
            String boutiqueId = data.getString("boutique_id");
            if (boutiqueId == null || boutiqueId.equals("")) {return ResultHelper.createErrorResult("205", "E205: Invalid boutique_id");}



        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByStockControllerExecute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            return ResultHelper.createErrorResult("206", "E206: error exception msg:"+e.getMessage());
        }

        logger.info("AtelierUpdateByStockControllerExecute,outputParams:"+ JSONObject.toJSONString(mapUtils));
        return mapUtils.getMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // luciana
        Map<String,Object> x4zmp = new HashMap<>();
    }
}

/*
{"data":[{"store_code":"X4ZMP","api_end_point_id":4,"system":"intramirror","vendor_id":8,"vendor_name":"Luciana Bari","api_configuration_id":13,"enabled":true},{"store_code":"XIW2E","api_end_point_id":4,"system":"intramirror","vendor_id":10,"vendor_name":"Dante 5","api_configuration_id":15,"enabled":true},{"store_code":"UIWK2","api_end_point_id":4,"system":"intramirror","vendor_id":11,"vendor_name":"I Cinque Fiori","api_configuration_id":17,"enabled":true},{"store_code":"ERS4S","api_end_point_id":4,"system":"intramirror","vendor_id":12,"vendor_name":"Mimma Ninni","api_configuration_id":19,"enabled":true},{"store_code":"UEYHD","api_end_point_id":4,"system":"intramirror","vendor_id":13,"vendor_name":"Di Pierro","api_configuration_id":21,"enabled":true},{"store_code":"IEK7W","api_end_point_id":4,"system":"intramirror","vendor_id":14,"vendor_name":"Gisa Boutique","api_configuration_id":23,"enabled":true},{"store_code":"WISE","api_end_point_id":29,"system":"intramirror","vendor_id":18,"vendor_name":"Wise Boutique","api_configuration_id":38,"enabled":true}]}
select ac.api_configuration_id,v.vendor_name,ac.vendor_id,ac.store_code,ac.api_end_point_id,ac.system,ac.enabled from api_configuration ac
left join api_end_point aep on(ac.api_end_point_id = aep.api_end_point_id and aep.enabled = 1)
left join vendor v on(v.vendor_id = ac.vendor_id and v.enabled = 1)
where ac.enabled = 1  and ac.system = 'intramirror' and aep.`name` = 'updateSKUStock';
*/
