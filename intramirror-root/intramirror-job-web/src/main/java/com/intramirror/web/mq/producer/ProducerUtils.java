package com.intramirror.web.mq.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.intramirror.product.api.vo.product.ProductOptions;
import com.intramirror.web.mq.MqProperties;
import com.intramirror.web.mq.consumer.InitConsumer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/9/11.
 */
@Component
public class ProducerUtils {

    private static Logger logger = Logger.getLogger(ProducerUtils.class);

    @Autowired
    private InitProducer initProducer;

    public OrderProducer getProducer(String producerId){
        try {
            return initProducer.getProducerMap().get(producerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ProducerUtilsGetProducer,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return null;
    }

    public boolean sendMessage(String topic,String producerId, String msgBody,String msgKey){
        try {
            logger.info("ProducerUtilsSendMessage,inputParams,producerId:"+producerId+",msgBody:"+msgBody+",msgKey:"+msgKey);

            // set message : topic , tag , content
            Message message = new Message(topic, producerId, msgBody.getBytes());

            // set msg key
            message.setKey(msgKey);

            // get producer
            OrderProducer producer = this.getProducer(producerId);

            // send message
            SendResult sendResult = producer.send(message,msgKey);
            logger.info("ProducerUtilsSendMessage,outputParams,producerId:"+producerId+",msgBody:"+msgBody+",msgKey:"+msgKey+",messageId:"+sendResult.getMessageId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ProducerUtilsSendMessage,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            return false;
        }
        return true;
    }

}
