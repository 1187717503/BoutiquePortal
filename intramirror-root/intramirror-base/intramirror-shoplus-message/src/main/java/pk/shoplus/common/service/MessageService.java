package pk.shoplus.common.service;

import pk.shoplus.common.exception.MessageFailException;
import pk.shoplus.common.vo.BatchMessageRequestVo;
import pk.shoplus.common.vo.MessageRequestVo;
import pk.shoplus.common.vo.MessageResponseVo;

import java.util.List;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface MessageService {

    /**
     * 推送消息到MQ
     * @param requestVo 消息对象
     * @throws MessageFailException
     */
    public void putMessage (MessageRequestVo requestVo) throws MessageFailException;

    /**
     * 批量推送消息到MQ单批最多不能超过15条
     * @param requestVos
     * @throws MessageFailException
     */
    public void batchPutMessage (BatchMessageRequestVo requestVos) throws MessageFailException;

    /**
     * 从MQ里获取消息内容
     * @param queueName 队列名称
     * @return
     * @throws MessageFailException
     */
    public List<MessageResponseVo> getMessage (String queueName) throws MessageFailException;
    
    /**
     * 从MQ里获取消息内容 
     * @param queueName 队列名称
     * @param isDeleted 获取到队列里的消息后,是否从当前队列里删除该消息 true 删除, false 不删除
     * @return
     * @throws MessageFailException
     */
    public List<MessageResponseVo> getMessage (String queueName,boolean isDeleted) throws MessageFailException;
    
    /**
     * 判断MQ中是否存在消息
     * @param queueName 队列名称
     * @return
     * @throws MessageFailException
     */
    public boolean isMessageNotExist (String queueName) throws MessageFailException;
    
    /**
     * 删除MQ中的消息
     * @param queueName 队列名称
     * @param receiptHandle 消息句柄
     * @return
     * @throws MessageFailException
     */
    public void deleteMessageByReceiptHandle (String queueName,String receiptHandle) throws MessageFailException;
}
