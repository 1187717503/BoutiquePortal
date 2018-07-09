package pk.shoplus.common.service.impl;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.QueueMeta;
import org.apache.log4j.Logger;
import pk.shoplus.common.constant.MessageConstant;
import pk.shoplus.common.exception.MessageFailException;
import pk.shoplus.common.service.OpMessageService;
import pk.shoplus.common.utils.MessageUtil;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class OPMNSMessageServiceImpl extends MNSMessageServiceImpl implements OpMessageService {

    private final Logger LOGGER = Logger.getLogger(OPMNSMessageServiceImpl.class);

    @Override
    public void createQueue(String queueName) throws MessageFailException {
        QueueMeta meta = new QueueMeta(); //生成本地QueueMeta属性，有关队列属性详细介绍见https://help.aliyun.com/document_detail/27476.html
        meta.setQueueName(queueName);  // 设置队列名
        meta.setPollingWaitSeconds(MessageConstant.POLLING_WAIT_SECONDS);
        meta.setMaxMessageSize(MessageConstant.MAXIMUN_MESSAGE_SIZE);
        meta.setLoggingEnabled(MessageConstant.LOGGING_ENABLED);

        MNSClient client = MessageUtil.getMNSClient();
        
        try {
            CloudQueue oldQueue = client.getQueueRef(queueName);
            
        	//判断是否存在该队列，不存在才去创建
            if(!oldQueue.isQueueExist()) {
                CloudQueue queue = client.createQueue(meta);
            }

        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service. Please check your network and DNS availablity.");
            ce.printStackTrace();
            throw ce;
        } catch (ServiceException se) {
            se.printStackTrace();
            LOGGER.error("MNS exception requestId:" + se.getRequestId(), se);
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist"))
                {
                    LOGGER.info("Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired"))
                {
                    LOGGER.info("The request is time expired. Please check your local machine timeclock");
                }
            }
            throw se;
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
            throw e;
        } finally {
            client.close();  // 程序退出时，需主动调用client的close方法进行资源释放
        }
    }

    @Override
    public void deleteQueue(String queueName) throws MessageFailException {
        MNSClient client = MessageUtil.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        try{
            CloudQueue queue = client.getQueueRef(queueName);
            queue.delete();
        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service. Please check your network and DNS availablity.");
            ce.printStackTrace();
            throw ce;
        } catch (ServiceException se) {
            se.printStackTrace();
            throw se;
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
            throw e;
        } finally {
            client.close();
        }
    }


    /*public static void main(String[] args) {
        new OPMNSMessageServiceImpl().createQueue("AtelierUpdateSyayus16");
        new OPMNSMessageServiceImpl().createQueue("AtelierUpdateSyayus16Success");
        new OPMNSMessageServiceImpl().createQueue("AtelierUpdateSyayus16Error");
        //new OPMNSMessageServiceImpl().deleteQueue("EDSCreateProduct1Success");
    }*/
}
