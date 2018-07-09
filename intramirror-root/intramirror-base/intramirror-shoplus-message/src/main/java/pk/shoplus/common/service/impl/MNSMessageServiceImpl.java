package pk.shoplus.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import pk.shoplus.common.MessageHelper;
import pk.shoplus.common.constant.MessageConstant;
import pk.shoplus.common.exception.MessageFailException;
import pk.shoplus.common.service.MessageService;
import pk.shoplus.common.service.OpMessageService;
import pk.shoplus.common.utils.MessageUtil;
import pk.shoplus.common.vo.BatchMessageRequestVo;
import pk.shoplus.common.vo.MessageRequestVo;
import pk.shoplus.common.vo.MessageResponseVo;

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
public class MNSMessageServiceImpl implements MessageService {

    private final Logger LOGGER = Logger.getLogger(MNSMessageServiceImpl.class);

    @Override
    public void putMessage(MessageRequestVo requestVo) throws MessageFailException {
        MNSClient client = null;
        try {
            if (null != requestVo && StringUtils.isNotBlank(requestVo.getRequestBody())) {
                client = MessageUtil.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
                CloudQueue queue = client.getQueueRef(requestVo.getQueueName());
                if(queue.isQueueExist()) {

                    Message message = new Message();
                    message.setMessageBody(requestVo.getRequestBody());
                    Message putMsg = queue.putMessage(message);
                    LOGGER.info("Send message id is: " + putMsg.getMessageId());
                } else {
                    LOGGER.error("message queue is not found !!!" + requestVo.getQueueName());
                }
//                else {
//                    OpMessageService messageService = new OPMNSMessageServiceImpl();
//                    messageService.createQueue(requestVo.getQueueName());
//                    queue = client.getQueueRef(requestVo.getQueueName());
//
//                    Message message = new Message();
//                    message.setMessageBody(requestVo.getRequestBody());
//                    Message putMsg = queue.putMessage(message);
//                    LOGGER.info("Send message id is: " + putMsg.getMessageId());
//                }

            }
        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service. Please check your network and DNS availablity.");
            ce.printStackTrace();
            throw ce;
        } catch (ServiceException se) {
            se.printStackTrace();
            LOGGER.error("MNS exception requestId:" + se.getRequestId(), se);
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    LOGGER.info("Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    LOGGER.info("The request is time expired. Please check your local machine timeclock");
                }
            }
            throw  se;
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
            throw  e;
        } finally {
            if (null != client) {
                client.close();// 程序退出时，需主动调用client的close方法进行资源释放
            }
        }
    }

    @Override
    public void batchPutMessage(BatchMessageRequestVo batchMessageRequestVo) throws MessageFailException {
        MNSClient client = null;
        List<Message> messages = null;
        try {
            List<MessageRequestVo> messageRequestVos = batchMessageRequestVo.getMessageRequestVos();
            if (null != messageRequestVos && messageRequestVos.size() > 0) {
                messages = new ArrayList<>(messageRequestVos.size());
                for (MessageRequestVo messageRequestVo : messageRequestVos) {
                    Message message = new Message();
                    message.setMessageBody(messageRequestVo.getRequestBody());
                    messages.add(message);
                }
            }

            if (null != messages && messages.size() > 0) {
                client = MessageUtil.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
                CloudQueue queue = client.getQueueRef(batchMessageRequestVo.getQueueName());
                List<Message> putMsgs = queue.batchPutMessage(messages);
                LOGGER.info("Batch send message is: " + JSON.toJSONString(putMsgs));
            }


        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service. Please check your network and DNS availablity.");
            ce.printStackTrace();
            throw ce;
        } catch (ServiceException se) {
            se.printStackTrace();
            LOGGER.error("MNS exception requestId:" + se.getRequestId(), se);
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    LOGGER.info("Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    LOGGER.info("The request is time expired. Please check your local machine timeclock");
                }
            }
            throw  se;
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
            throw  e;
        } finally {
            if (null != client) {
                client.close();// 程序退出时，需主动调用client的close方法进行资源释放
            }
        }
    }

    @Override
    public List<MessageResponseVo> getMessage(String queueName) throws MessageFailException {
        return getMessage(queueName, true);
    }
    
    @Override
    public List<MessageResponseVo> getMessage(String queueName, boolean isDeleted) throws MessageFailException {
        MNSClient client = MessageUtil.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        MessageResponseVo messageResponseVo = null;
        List<MessageResponseVo> messageList = null;

        try {
            CloudQueue queue = client.getQueueRef(queueName);
            Message msg = queue.peekMessage();
            if(msg != null) {
            	//Message popMsg = queue.popMessage();
                List<Message> list = queue.batchPopMessage(MessageConstant.BATCH_SIZE);
                if (null != list && list.size() > 0) {
                    messageList = new ArrayList<>(list.size());
                    for (Message popMsg : list) {
                        if (popMsg != null) {
                            LOGGER.info("message handle: " + popMsg.getReceiptHandle());
                            LOGGER.info("message body: " + popMsg.getMessageBodyAsString());
                            LOGGER.info("message id: " + popMsg.getMessageId());
                            LOGGER.info("message dequeue count:" + popMsg.getDequeueCount());

                            messageResponseVo = convertMessage(popMsg);
                            if (null != messageResponseVo) {
                                messageList.add(messageResponseVo);
                            }
                            //删除已经取出消费的消息
                            if (isDeleted) {
                                queue.deleteMessage(popMsg.getReceiptHandle());
                                LOGGER.info("delete message successfully.\n");
                            }

                        } else {
                            LOGGER.info("message not exist in createProduct.\n");
                        }
                    }
                }
            }

        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service. Please check your network and DNS availablity.");
            ce.printStackTrace();
            throw ce;
        } catch (ServiceException se) {
            se.printStackTrace();
            LOGGER.error("MNS exception requestId:" + se.getRequestId(), se);
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    LOGGER.info("Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    LOGGER.info("The request is time expired. Please check your local machine timeclock");
                }
            }
            throw se;
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
            throw e;
        } finally {
            client.close();
        }

        return messageList;
    }

    private MessageResponseVo convertMessage (Message message) {
        MessageResponseVo messageResponseVo = null;

        if (message != null) {
            messageResponseVo = new MessageResponseVo();
            messageResponseVo.setReceiptHandle(message.getReceiptHandle());
            messageResponseVo.setMessageId(message.getMessageId());
            messageResponseVo.setMessageBody(message.getMessageBody());
            messageResponseVo.setDequeueCount(message.getDequeueCount());
            messageResponseVo.setEnqueueTime(message.getEnqueueTime());
            messageResponseVo.setFirstDequeueTime(message.getFirstDequeueTime());
            messageResponseVo.setNextVisibleTime(message.getNextVisibleTime());
        }

        return  messageResponseVo;
    }

	@Override
	public boolean isMessageNotExist(String queueName) throws MessageFailException {
		MNSClient client = MessageUtil.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
		boolean isMessage = true;
        try {
            CloudQueue queue = client.getQueueRef(queueName);
            Message message = queue.peekMessage();
            if(message == null) {
            	isMessage = false;
            }
        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service. Please check your network and DNS availablity.");
            ce.printStackTrace();
            throw ce;
        } catch (ServiceException se) {
            se.printStackTrace();
            LOGGER.error("MNS exception requestId:" + se.getRequestId(), se);
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    LOGGER.info("Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    LOGGER.info("The request is time expired. Please check your local machine timeclock");
                }
            }
            throw se;
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
            throw e;
        } finally {
            client.close();
        }
		return isMessage;
	}

	@Override
	public void deleteMessageByReceiptHandle(String queueName, String receiptHandle) throws MessageFailException {
		MNSClient client = MessageUtil.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        try {
            CloudQueue queue = client.getQueueRef(queueName);
            queue.deleteMessage(receiptHandle);
        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service. Please check your network and DNS availablity.");
            ce.printStackTrace();
            throw ce;
        } catch (ServiceException se) {
            se.printStackTrace();
            LOGGER.error("MNS exception requestId:" + se.getRequestId(), se);
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    LOGGER.info("Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    LOGGER.info("The request is time expired. Please check your local machine timeclock");
                }
            }
            throw  se;
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
            throw  e;
        } finally {
            client.close();// 程序退出时，需主动调用client的close方法进行资源释放
        }
	}
}
