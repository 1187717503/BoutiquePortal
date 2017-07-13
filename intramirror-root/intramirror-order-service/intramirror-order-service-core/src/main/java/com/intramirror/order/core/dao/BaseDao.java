package com.intramirror.order.core.dao;

import javax.annotation.Resource;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;

/**
 * 注入sqlSessionTemplate,使用SqlSessionDaoSupport
 * @author dingyifan
 * @date 2016年7月4日下午5:15:43
 *
 */
public abstract class BaseDao extends SqlSessionDaoSupport{
	
	/** 注入sqlSessionTemplate */
	@Resource(name="orderSqlSessionTemplate")
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate)
    {
    	super.setSqlSessionTemplate(sqlSessionTemplate);
    }
	
	/** 初始化Mapper */
	public abstract void init();
	
}