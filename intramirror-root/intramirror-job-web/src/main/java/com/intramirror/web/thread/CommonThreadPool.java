package com.intramirror.web.thread;

import com.intramirror.common.utils.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.util.ExceptionUtils;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by dingyifan on 2017/9/12.
 * 自定义可缓存线程池，特性
 * 1。无界限线程池
 * 2。自动回收60秒内不使用的线程
 */
@Service
public class CommonThreadPool {

    private static final Logger logger = Logger.getLogger(CommonThreadPool.class);

    /**
     * @param eventName 需要处理的事情
     * @param executor 线程池
     * @param threadNum 线程数量
     * @param runnable 线程
     */
    public static void execute(String eventName, ThreadPoolExecutor executor, int threadNum, Runnable runnable){
        try {
            logger.info("CommonThreadPoolExecuteProduct,threadInfo,执行器中线程中实际的线程数量:"+executor.getPoolSize()+"，执行器中正在执行任务的线程数量："
                    +executor.getActiveCount()
                    +"，执行器中已经完成的任务数量:"
                    +executor.getCompletedTaskCount()
                    +",eventName:"+eventName);

            logger.info("CommonThreadPoolExecuteProduct,start");
            if(executor.getActiveCount() <= threadNum) {
                executor.execute(runnable);
                logger.info("CommonThreadPoolExecuteProduct,end");
            } else {
                logger.info("CommonThreadPoolExecuteProduct,startSleep,date:"+ DateUtils.getStrDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
                Thread.sleep(1000);
                logger.info("CommonThreadPoolExecuteProduct,endSleep,date:"+ DateUtils.getStrDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
                execute(eventName,executor,threadNum,runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("CommonThreadPoolExecuteProduct,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }
    /*// 定义可缓存线程池
    private static ThreadPoolExecutor executor;

    private static CommonThreadPool commonThreadPool;

    public static CommonThreadPool getInstance(){
        if(commonThreadPool == null){
            return new CommonThreadPool();
        }
        return commonThreadPool;
    }

    public static void main(String[] args) throws InterruptedException {
         executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();

        System.out.println(executor);


        for(int i =1;i<10;i++){
            executor.execute(new testThread("ABC1"));
        }
        logger.info("CommonThreadPoolExecuteProduct,threadInfo,执行器中线程中实际的线程数量:"+executor.getPoolSize()+"，执行器中正在执行任务的线程数量："
                +executor.getActiveCount()
                +"，执行器中已经完成的任务数量:"
                +executor.getCompletedTaskCount());

        executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Thread.sleep(5000L);
        for(int i =1;i<10;i++){
            executor.execute(new testThread("ABC2"));
        }
        logger.info("CommonThreadPoolExecuteProduct,threadInfo,执行器中线程中实际的线程数量:"+executor.getPoolSize()+"，执行器中正在执行任务的线程数量："
                +executor.getActiveCount()
                +"，执行器中已经完成的任务数量:"
                +executor.getCompletedTaskCount());

    }*/
}
/*

class testThread implements Runnable {

    private String code ;
    @Override
    public void run() {
        try {
            System.out.println(code);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public testThread(String code) {
        this.code = code;
    }
}*/
