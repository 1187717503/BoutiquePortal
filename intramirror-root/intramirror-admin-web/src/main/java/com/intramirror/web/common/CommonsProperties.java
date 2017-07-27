package com.intramirror.web.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonsProperties {
	
	@Value("#{commons.baseUrl}")
	private String baseUrl;

	@Value("#{commons.filterEnabled}")
	private String filterEnabled;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getFilterEnabled() {
		return filterEnabled;
	}

	public void setFilterEnabled(String filterEnabled) {
		this.filterEnabled = filterEnabled;
	}
}
