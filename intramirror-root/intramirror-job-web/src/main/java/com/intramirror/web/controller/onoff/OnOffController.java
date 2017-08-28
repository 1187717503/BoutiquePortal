package com.intramirror.web.controller.onoff;

import com.google.gson.Gson;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.ApiMq;
import com.intramirror.product.api.service.IApiMqService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.util.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/8/28.
 * 队列的开关接口
 */
@Controller
@RequestMapping("/onoff")
public class OnOffController {
    /** logger */
    private static Logger logger = Logger.getLogger(OnOffController.class);

    private static final String stop = "stop";

    private static final String start = "start";

    @Autowired
    private IApiMqService iApiMqService;

    /** 停止队列消费和生产 */
    @RequestMapping(value = "/stop/queue")
    @ResponseBody
    public Map<String,Object> stopQueue(@Param(value = "mqName") String mqName){
        return this.execute(mqName,stop);
    }

    /** 开启队列消费和生产 */
    @RequestMapping(value = "/start/queue")
    @ResponseBody
    public Map<String,Object> startQueue(@Param(value = "mqName") String mqName){
        return this.execute(mqName,start);
    }

    private Map<String,Object> execute(String mqName,String event){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        if(StringUtils.isBlank(mqName) || StringUtils.isBlank(event)) {
            return mapUtils.putData("status", StatusType.FAILURE).putData("info","mqName or event is null !!!").getMap();
        }

        logger.info("OnOffQueue mqName:" +mqName+",event:"+event);
        ApiMq apiMq = new ApiMq();
        if(event.equals(stop)) {
            apiMq.setEnabled(EnabledType.DISCARD);
        } else {
            apiMq.setEnabled(EnabledType.USED);
        }
        apiMq.setName(mqName);
        int result = iApiMqService.updateByMqName(apiMq);
        mapUtils.putData("staus",StatusType.SUCCESS).putData("info",new Gson().toJson(apiMq)).putData("result",result);
        return mapUtils.getMap();
    }
}
