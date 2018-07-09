package com.intramirror.order.core.dao;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 如果我们需要在Spring容器完成Bean的实例化、配置和其他的初始化前后添加一些自己的逻辑处理，我们就可以定义一个或者多个BeanPostProcessor接口的实现，然后注册到容器中
 * @author dingyifan
 * @date 2016年7月4日下午5:16:03
 *
 */
@Component("orderServiceProcessor")
public class ServiceProcessor implements BeanPostProcessor{

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean.getClass().getSuperclass() == BaseDao.class) {
			BaseDao baseDao = (BaseDao) bean;
			baseDao.init();
		}
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
