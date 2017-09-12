package com.intramirror.web.util;

import com.intramirror.web.mq.producer.ProducerUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dingyifan on 2017/9/12.
 */
public class RandomUtils {

    /**
     * 日期+时间戳+随机数
     * @return
     */
    public static String getDateRandom(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        // 日期
        String temp = simpleDateFormat.format(new Date());

        // 时间戳
        long currentTimeMillis = System.currentTimeMillis();

        // 随机数
        int random=(int) ((Math.random()+1)*100000);

        return temp+currentTimeMillis+random;
    }
}
