package com.intramirror.payment.api.model;

import com.alibaba.fastjson15.JSONObject;



public class ResultMsg {
	private Boolean status = true;
	
	private JSONObject data;
	
	private String msg;

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	public ResultMsg status(boolean status){
		this.status = status;
		return this;
	}
	
}
