package pk.shoplus.common.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class BatchMessageRequestVo {
    /**
     * 消息队列名称
     */
    private String queueName;

    /**
     * 消息对象集合
     */
    private List<MessageRequestVo> messageRequestVos = new ArrayList<>();


    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public List<MessageRequestVo> getMessageRequestVos() {
        return messageRequestVos;
    }

    public void setMessageRequestVos(List<MessageRequestVo> messageRequestVos) {
        this.messageRequestVos = messageRequestVos;
    }
}
