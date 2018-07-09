package pk.shoplus.common.vo;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class MessageRequestVo {
    /**
     * 消息队列名称
     */
    private String queueName;

    /**
     * 消息体
     */
    private String requestBody;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

}
