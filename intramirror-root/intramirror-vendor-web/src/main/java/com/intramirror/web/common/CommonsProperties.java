package com.intramirror.web.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonsProperties {
	
	@Value("#{commons.baseUrl}")
	private String baseUrl;

	@Value("#{commons.orderPath}")
	private String orderPath;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getOrderPath() {
		return orderPath;
	}

	public void setOrderPath(String orderPath) {
		this.orderPath = orderPath;
	}
}
