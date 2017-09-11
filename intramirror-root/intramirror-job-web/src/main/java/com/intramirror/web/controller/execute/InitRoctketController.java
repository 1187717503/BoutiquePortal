package com.intramirror.web.controller.execute;

import com.intramirror.web.mq.MqName;
import com.intramirror.web.mq.consumer.InitConsumer;
import com.intramirror.web.mq.producer.InitProducer;
import com.intramirror.web.mq.producer.ProducerUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.util.MapUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/11.
 */
@Controller
@RequestMapping("/init/roctket")
public class InitRoctketController {

    private static Logger logger = Logger.getLogger(InitRoctketController.class);

    @Autowired
    private ProducerUtils producerUtils;

    @Resource(name = "updateProduct")
    private MqName mqName;

    @RequestMapping("/execute")
    @ResponseBody
    public Map<String,Object> execute(@Param(value = "event")String event){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        return mapUtils.getMap();
    }
}
