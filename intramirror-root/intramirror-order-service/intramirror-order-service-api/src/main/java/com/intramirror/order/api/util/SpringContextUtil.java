package com.intramirror.order.api.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by zhongyu on 2017/12/15.
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

	/**
	 * 以静态变量保存ApplicationContext,可在任意代码中取出ApplicaitonContext.
	 */
	private static ApplicationContext context;

	/**
	 * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
	 */
	public void setApplicationContext(ApplicationContext context) {
		SpringContextUtil.context =context;
	}
	public static ApplicationContext getApplicationContext() {
		return context;
	}
	/**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(String name) {
		return (T) context.getBean(name);
	}

	// 获取当前环境
	public static String getActiveProfile() {
		return context.getEnvironment().getActiveProfiles()[0];
	}

	public static void main(String[] args) {
		System.out.println(getActiveProfile());
	}
}
