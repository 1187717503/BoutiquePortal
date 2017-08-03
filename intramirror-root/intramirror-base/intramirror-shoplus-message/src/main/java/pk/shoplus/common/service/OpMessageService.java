package pk.shoplus.common.service;

import pk.shoplus.common.exception.MessageFailException;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface OpMessageService extends MessageService {

    public void createQueue (String queueName) throws MessageFailException;

    public void deleteQueue (String queueName) throws MessageFailException;
}
