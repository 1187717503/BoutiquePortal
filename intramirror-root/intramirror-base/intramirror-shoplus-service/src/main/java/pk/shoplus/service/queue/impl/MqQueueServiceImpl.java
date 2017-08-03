package pk.shoplus.service.queue.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import pk.shoplus.common.Contants;
import pk.shoplus.common.vo.MessageRequestVo;
import pk.shoplus.mq.enums.QueueNameEnum;
import pk.shoplus.mq.enums.QueueTypeEnum;
import pk.shoplus.mq.vo.MessageInfo;
import pk.shoplus.service.queue.api.IMqQueueService;
import pk.shoplus.vo.ResultMessage;

import java.util.Iterator;

//import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.iterator;

public class MqQueueServiceImpl implements IMqQueueService {

    private static Logger logger = Logger.getLogger(MqQueueServiceImpl.class);

    @Override
    public ResultMessage putGetEventsData(JSONObject jsonObject) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            int type = jsonObject.getInteger("type");
            MessageRequestVo messageRequestVo = null;

            if(type == Contants.EVENTS_TYPE_0 || type == Contants.EVENTS_TYPE_1 || type == Contants.EVENTS_TYPE_4) {

                // update stock
                messageRequestVo = this.putMessage(QueueNameEnum.CloudStoreSynStock.getMqCode(),jsonObject.toString());
            } else if(type == Contants.EVENTS_TYPE_2) {

                // TODO 多个商品时,数据格式???
                JSONObject additionalInfo = jsonObject.getJSONObject("additional_info");
                JSONArray items = additionalInfo.getJSONArray("item");
                Iterator<Object> iterators =  items.iterator();
                while (iterators.hasNext()) {
                    JSONObject item = (JSONObject) iterators.next();
                    this.putMessage(QueueNameEnum.CloudStoreSynProduct.getMqCode(),item.toString());
                }

            } else if(type == Contants.EVENTS_TYPE_3) {

                // update product
                JSONObject additionalInfo = jsonObject.getJSONObject("additional_info");
                JSONObject item = additionalInfo.getJSONObject("item");
                this.putMessage(QueueNameEnum.CloudStoreSynProduct.getCode(),item.toString());
            }

            if(messageRequestVo != null) {
                resultMessage.sStatus(true).sMsg("SUCCESS").setData(messageRequestVo);
            } else {
                resultMessage.sStatus(false).sMsg("errorMessage : type 类型不正确!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MqQueueServiceImpl.putGetEventsData errorMessage:"+e.getMessage());
            resultMessage.sStatus(false).sMsg("errorMessage : 未知异常!!!" + e.getMessage());
        } finally {

        }

        return resultMessage;
    }

    private MessageRequestVo putMessage(String queueName,String mqDataMap){
        MessageRequestVo messageRequestVo = new MessageRequestVo();
        if(StringUtils.isNotBlank(mqDataMap)) {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setBody(mqDataMap);
            messageInfo.setQueueName(queueName);
            messageInfo.setQueueType(QueueTypeEnum.PENDING.getValue());

            messageRequestVo.setQueueName(queueName+QueueTypeEnum.PENDING.getCode());
            messageRequestVo.setRequestBody(messageInfo.toJson());
        }
        return messageRequestVo;
    }

}
