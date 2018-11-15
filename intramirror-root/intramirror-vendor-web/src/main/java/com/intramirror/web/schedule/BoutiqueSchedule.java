package com.intramirror.web.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Created by caowei on 2018/11/15.
 */
@Component
public class BoutiqueSchedule {

    @Scheduled(cron = "0 0/1 * * * ?")
    public void retryShipEmail(){
        System.out.println("定时任务："+new Random(10).nextInt());
    }
}
