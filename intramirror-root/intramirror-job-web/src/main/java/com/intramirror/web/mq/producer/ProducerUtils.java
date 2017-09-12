package com.intramirror.web.mq.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.web.mq.vo.Region;
import com.intramirror.web.mq.vo.Tag;
import com.intramirror.web.util.RandomUtils;
import com.intramirror.web.vo.ApiProductVo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/9/11.
 */
@Component
public class ProducerUtils {

    private static Logger logger = Logger.getLogger(ProducerUtils.class);

    @Autowired
    private InitProducer initProducer;

    private OrderProducer getProducer(String producerId){
        try {
            return initProducer.getProducerMap().get(producerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ProducerUtilsGetProducer,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return null;
    }

    public boolean sendMessage(Region region, Tag tag ,String msgBody){
        try {
            logger.info("ProducerUtilsSendMessage,inputParams,region:"+new Gson().toJson(region)+",tag:"+new Gson().toJson(tag)+",msgBody:"+msgBody);

            if(region == null || StringUtils.isBlank(msgBody) || tag == null) {
                logger.info("ProducerUtilsSendMessage,checkError,region:"+new Gson().toJson(region)+",tag:"+new Gson().toJson(tag)+",msgBody:"+msgBody);
                return false;
            }

            String topic = region.getTopic();
            String producerId = region.getProducerId();
            String tagCode = tag.getCode();

            // set message : topic , tag , content
            Message message = new Message(topic, tagCode, msgBody.getBytes());

            // set msg key 设置一个唯一的key,用于分区排序
            String shardingKey = RandomUtils.getDateRandom();
            message.setKey(shardingKey);

            // get producer
            OrderProducer producer = this.getProducer(producerId);

            // send message
            SendResult sendResult = producer.send(message,shardingKey);
            logger.info("ProducerUtilsSendMessage,outputParams,messageId:"+sendResult.getMessageId()+",region:"+new Gson().toJson(region)+",tag:"+new Gson().toJson(tag)+",msgBody:"+msgBody);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ProducerUtilsSendMessage,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            return false;
        }
        return true;
    }

    public ResultMessage sendMessage(Region region, Tag tag , ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions){
        ResultMessage resultMessage = new ResultMessage();
        try {
            logger.info("ProducerUtilsSendMessage,inputParams,region:"+new Gson().toJson(region)+",tag:"+new Gson().toJson(tag)
                    +",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions));

            if(productOptions == null || vendorOptions == null) {
                return resultMessage.errorStatus().putMsg("info","productOptions or vendorOptions is null !!!");
            }

            ApiProductVo apiProductVo = new ApiProductVo();
            apiProductVo.setVendorOptions(vendorOptions);
            apiProductVo.setProductOptions(productOptions);
            String msgBody = new Gson().toJson(apiProductVo);

            logger.info("ProducerUtilsSendMessage,startSendMessage,region:"+new Gson().toJson(region)+",tag:"+new Gson().toJson(tag)+",msgBody:"+msgBody);
            boolean flag = this.sendMessage(region,tag,msgBody);
            logger.info("ProducerUtilsSendMessage,endSendMessage,flag:"+flag+",region:"+new Gson().toJson(region)+",tag:"+new Gson().toJson(tag)+",msgBody:"+msgBody);

            resultMessage.successStatus().putMsg("info","success");
        } catch (Exception e) {
            logger.info("ProducerUtilsSendMessage,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            resultMessage.errorStatus().putMsg("info","errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            e.printStackTrace();
        }
        return resultMessage;
    }

}
