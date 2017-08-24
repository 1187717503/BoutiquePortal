package com.intramirror.web.util;

import com.google.gson.Gson;
import com.intramirror.web.enums.QueueNameJobEnum;

import org.apache.log4j.Logger;

import pk.shoplus.common.MessageHelper;
import pk.shoplus.common.vo.MessageRequestVo;
import pk.shoplus.mq.enums.QueueNameEnum;
import pk.shoplus.mq.enums.QueueTypeEnum;
import pk.shoplus.mq.vo.MessageInfo;


/**
 * Created by dingyifan on 2017/6/9.
 */
public class QueueUtils {

    private static Logger logger = Logger.getLogger(QueueUtils.class);

    public static void putMessage(Object mqDataMap,String offset,String url,QueueNameJobEnum queueNameEnum) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setBody(mqDataMap);
        messageInfo.setCurrentIndex(offset);
        messageInfo.setUrl(url);
        messageInfo.setQueueName(queueNameEnum.getMqCode());
        messageInfo.setQueueType(QueueTypeEnum.PENDING.getValue());

        MessageRequestVo messageRequestVo = new MessageRequestVo();
        messageRequestVo.setQueueName(queueNameEnum.getMqCode()+QueueTypeEnum.PENDING.getCode());
        messageRequestVo.setRequestBody(new Gson().toJson(messageInfo));
        MessageHelper.putMessage(messageRequestVo);
    }

    public static void putMessage(Object mqDataMap,QueueNameJobEnum queueNameEnum,QueueTypeEnum queueTypeEnum) {
        logger.info("start putMessage by : " + queueNameEnum.getCode() + queueTypeEnum.getCode());
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setBody(mqDataMap);
        messageInfo.setQueueName(queueNameEnum.getMqCode());

        MessageRequestVo messageRequestVo = new MessageRequestVo();
        messageRequestVo.setQueueName(queueNameEnum.getMqCode()+queueTypeEnum.getCode());
        messageRequestVo.setRequestBody(new Gson().toJson(messageInfo));
        MessageHelper.putMessage(messageRequestVo);
        logger.info("end putMessage by : " + queueNameEnum.getCode() + queueTypeEnum.getCode());
    }

}
