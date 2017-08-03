package pk.shoplus.vo;

import org.apache.commons.lang.StringUtils;
import pk.shoplus.model.Result;

/** 
 * 响应消息
 * @author dingyifan
 *
 */
public class ResultMessage {
	// true-success,false-error
	public Boolean status = true;
	
	// 响应消息
	public String msg = "";
	
	// 响应数据
	public Object data = null;

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public ResultMessage sMsg(String msg){
		if(StringUtils.isNotBlank(this.msg)) {
			this.msg = this.msg + "|" + msg;
		} else {
			this.msg = msg;
		}
		return this;
	}

	public ResultMessage sStatus(boolean status){
		this.status = status;
		return this;
	}

	public ResultMessage sData(Object data){
		this.data = data;
		return this;
	}

	public ResultMessage(Boolean status, String msg, Object data) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	public ResultMessage(Boolean status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	public ResultMessage() {
		super();
	}
	
}
