package com.intramirror.web.thread;

import com.intramirror.web.service.ConsumeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 自定义定时线程池
 * @author yfding
 *
 */
public class ThreadPoolTimer {
	private static final Logger logger = Logger.getLogger(ThreadPoolTimer.class);
	
	public static ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(3);
	
	public static ThreadPoolTimer threadPoolTimer;
	
	private static final Long initialDelay = 5L;// 10L;
	
	private static final Long period = 3L;// ;


	
	public static ThreadPoolTimer getInstance(){
		if(scheduExec.isShutdown()) {
			scheduExec = Executors.newScheduledThreadPool(3);
		}

		if(threadPoolTimer == null || scheduExec.isShutdown()){
			return new ThreadPoolTimer();
		}

		return threadPoolTimer;
	}
	
	/**
	 * 
	 * @param task 要执行的任务
	 * @param initialDelay 首次执行要延迟的时间
	 * @param period 一次执行终止和下一次执行开始之间的延迟
	 */
	@Deprecated
	private void executeTask(Runnable task,Long initialDelay,Long period){
		scheduExec.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
	}
	
	public void executeTask(List<Runnable> tasks){
		try {
			if(tasks != null && tasks.size() > 0) {
				for(Runnable task : tasks) {
					scheduExec.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ThreadPoolTimer ErrorMessage:" + e.getMessage());
		}
	}
	
	public void executeTask(Runnable task){
		try {
			scheduExec.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ThreadPoolTimer ErrorMessage:" + e.getMessage());
		}
	}
	
	public void destory(){
		if (scheduExec != null)
			scheduExec.shutdown();
	}
}