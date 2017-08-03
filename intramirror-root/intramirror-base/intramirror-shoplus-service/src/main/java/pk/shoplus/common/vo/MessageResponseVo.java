package pk.shoplus.common.vo;

import com.alibaba.fastjson15.JSONObject;

import java.util.Date;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class MessageResponseVo {

    /**
     * 消息编号，在一个 Queue 中唯一
     */
    public String messageId;

    /**
     * 消息正文
     */
    public String messageBody;

    public JSONObject jsonObject;


    /**
     * 消息发送到队列的时间
     */
    public Date enqueueTime;

    /**
     * 第一次被消费的时间
     */
    public Date firstDequeueTime;

    /**
     * 下次可被再次消费的时间
     */
    public Date nextVisibleTime;

    /**
     * 总共被消费的次数
     */
    public Integer dequeueCount;
    
    /**
     * 消息句柄
     */
    public String receiptHandle;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Date getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(Date enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public Date getFirstDequeueTime() {
        return firstDequeueTime;
    }

    public void setFirstDequeueTime(Date firstDequeueTime) {
        this.firstDequeueTime = firstDequeueTime;
    }

    public Date getNextVisibleTime() {
        return nextVisibleTime;
    }

    public void setNextVisibleTime(Date nextVisibleTime) {
        this.nextVisibleTime = nextVisibleTime;
    }

    public Integer getDequeueCount() {
        return dequeueCount;
    }

    public void setDequeueCount(Integer dequeueCount) {
        this.dequeueCount = dequeueCount;
    }

	public String getReceiptHandle() {
		return receiptHandle;
	}

	public void setReceiptHandle(String receiptHandle) {
		this.receiptHandle = receiptHandle;
	}

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
