//package com.intramirror.web.mq;
//
//import com.alibaba.fastjson.JSON;
//import com.aliyun.openservices.ons.api.Action;
//import com.aliyun.openservices.ons.api.ConsumeContext;
//import com.aliyun.openservices.ons.api.Message;
//import com.aliyun.openservices.ons.api.MessageListener;
//import com.intramirror.order.api.service.ILogisticsProductService;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.nio.charset.Charset;
//
///**
// * <一句话功能简述>
// * <功能详细描述>
// *
// * @auth:jerry
// * @see: [相关类/方法]（可选）
// * @since [产品/模块版本] （可选）
// */
//public class RocketMessageListener implements MessageListener {
//
//    /**
//     * 日志服务
//     */
//    private static Logger logger = Logger.getLogger(RocketMessageListener.class);
//
//    @Autowired
//    private ILogisticsProductService logisticsProductService;
//
//    @Override
//    public Action consume(Message message, ConsumeContext consumeContext) {
//        logger.info("Receive Message ID : " + message.getMsgID());
//        logger.info("Receive Message : " + JSON.toJSONString(message));
//
//        try {
//            String csn = Charset.defaultCharset().name();
//            byte [] bodyByte = message.getBody();
//            String body = new String(bodyByte, csn);
//            if (StringUtils.isNotBlank(body)) {
//                Long logisticsProductId = Long.valueOf(body);
//                logisticsProductService.invalidOrderById(logisticsProductId);
//            }
//
//            return Action.CommitMessage;
//        }catch (Exception e) {
//            //消费失败
//            logger.error("消息 "+ message.getMsgID() +" 消费失败! "+ e.toString());
//            return Action.ReconsumeLater;
//        }
//    }
//}
