package com.intramirror.web.properties.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonProperties {
	
	@Value("#{common.baseUrl}")
	private String baseUrl;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
