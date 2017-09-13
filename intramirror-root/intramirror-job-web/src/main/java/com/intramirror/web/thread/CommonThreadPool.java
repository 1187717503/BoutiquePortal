package com.intramirror.web.thread;

import org.springframework.stereotype.Service;

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

    // 定义可缓存线程池
    private static ThreadPoolExecutor executor;

    private static CommonThreadPool commonThreadPool;

    public static CommonThreadPool getInstance(){
        if(commonThreadPool == null){
            return new CommonThreadPool();
        }
        return commonThreadPool;
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor2 = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        System.out.println(executor);
        System.out.println(executor2);

        executor.execute(new testThread("ABC"));

        for(int i =1;i<100;i++){
//            executor.execute(new testThread("ABC"));
        }
        for (int i = 0;i<100;i++){
//            Thread.sleep(1000L);

//            System.out.printf(i+"service:pool执行器中线程中实际的线程数量:%d，执行器中正在执行任务的线程数量：%d，执行器中已经完成的任务数量:%d\n",executor.getPoolSize(),executor.getActiveCount(),executor.getCompletedTaskCount());
//            System.out.println(executor);
            System.out.println(executor2);
        }
    }
}



class testThread implements Runnable {

    private String code ;
    @Override
    public void run() {
        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName()+code);
    }

    public testThread(String code) {
        this.code = code;
    }
}
