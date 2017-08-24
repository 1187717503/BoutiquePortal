package com.intramirror.web.controller.execute;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.service.ConsumeService;
import com.intramirror.web.thread.ThreadPoolTimer;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.util.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/8/23.
 * 消费MQ线程池
 */
@Controller
@RequestMapping("/timer")
public class TimerJobHandlePendingQueueController {

    /** logger */
    private static Logger logger = Logger.getLogger(TimerJobHandlePendingQueueController.class);

    /** 线程数量 */
    private static final int threadNum = 5;

    /** 线程池对象 */
    public static ThreadPoolTimer threadPoolTimer;

    @Autowired
    private ConsumeService consumeService;

    @ResponseBody
    @RequestMapping(value = "/consume",method = RequestMethod.GET)
    public Map<String,Object> consume(@Param(value = "event") String event){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
            if(StringUtils.isNotBlank(event)) {

                if(event.equals("execute")) {
                	 consumeService.main();
                } else if(event.equals("start")) {
                    this.start();
                    mapUtils.putData("status",StatusType.SUCCESS).putData("info","timer start success!!!");
                } else if(event.equals("stop")) {
                    if(threadPoolTimer != null) {threadPoolTimer.destory();}
                    mapUtils.putData("status",StatusType.SUCCESS).putData("info","desctory success!!!");
                }
            } else {
                mapUtils.putData("status",StatusType.FAILURE).putData("info","event is null !!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mapUtils.putData("status", StatusType.FAILURE).putData("info",e.getMessage());
            logger.info("execue consume error !!!" + e.getMessage());
        }
        return mapUtils.getMap();
    }

    public TimerJobHandlePendingQueueController(){
//        this.start();
    }

    public void start(){
        threadPoolTimer = ThreadPoolTimer.getInstance();
        for(int i = 0;i<threadNum;i++) {
            threadPoolTimer.executeTask(new Consume());
        }
    }

    /** 消费线程 */
    class Consume implements Runnable {

        @Override
        public void run() {
            try {
                consumeService.main();
                System.out.println(Thread.currentThread().getName()+"-----");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("consume queue error !!!" + e.getMessage());
            }
        }
    }
}

