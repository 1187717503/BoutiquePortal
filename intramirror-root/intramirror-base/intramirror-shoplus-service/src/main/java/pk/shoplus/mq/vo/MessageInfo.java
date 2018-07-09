package pk.shoplus.mq.vo;

import java.util.Date;

import com.google.gson.Gson;

import pk.shoplus.mq.enums.QueueNameEnum;
import pk.shoplus.mq.enums.QueueTypeEnum;

/**
 * 队列BODY(封装的消息)
 * @author dingyifan
 *
 */
public class MessageInfo {
	
	/** 处理数据 */
	private Object body = null;
	
	/** 队列名称 {@link QueueNameEnum}*/
	private String queueName = null;
	
	/** 队列类型  {@link QueueTypeEnum}*/
	private String queueType = null;
	
	/**	消息备注 */
	private String desc = null;
	
	/** 创建时间 */
	private Date createDate = new Date();
	
	/** 修改时间 */
	private Date updateDate = new Date();
	
	/** 当前消息所在位置 */
	private String currentIndex;

	private Object objBody;
	
	/** URL */
	private String url = null;

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public String toJson(){
		return new Gson().toJson(this);
	}

	public String getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(String currentIndex) {
		this.currentIndex = currentIndex;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Object getObjBody() {
		return objBody;
	}

	public void setObjBody(Object objBody) {
		this.objBody = objBody;
	}
}