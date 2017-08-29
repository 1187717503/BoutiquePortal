package com.intramirror.web.service;

import com.google.gson.Gson;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.ApiErrorProcessing;
import com.intramirror.product.api.model.ApiErrorType;
import com.intramirror.product.api.model.ApiMq;
import com.intramirror.product.api.service.IApiErrorProcessingService;
import com.intramirror.product.api.service.IApiErrorTypeService;
import com.intramirror.product.api.service.IApiMqService;
import com.intramirror.web.enums.QueueNameJobEnum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.shoplus.common.MessageHelper;
import pk.shoplus.common.vo.MessageRequestVo;
import pk.shoplus.common.vo.MessageResponseVo;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.mq.enums.QueueTypeEnum;
import pk.shoplus.mq.vo.MessageInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/8/23.
 * 消费MQ消息
 */
@Service(value = "consumeService")
public class ConsumeService {
    /** logger */
    private static Logger logger = Logger.getLogger(ConsumeService.class);

    @Autowired
    private IApiMqService iApiMqService;

    @Autowired
    private IApiErrorTypeService iApiErrorTypeService;

    @Autowired
    private IApiErrorProcessingService iApiErrorProcessingService;

    public void main(){
        try {
            List<ApiMq> apiMqs = iApiMqService.getMqs();

            if(apiMqs != null && apiMqs.size() > 0) {
                for(ApiMq apiMq : apiMqs) {
                    String mqName = apiMq.getName() + apiMq.getApiConfigurationId();
                    this.consumeApiMq(mqName,apiMq);
                }
            } else {
                logger.info("consumeMain apiMqs is null !!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("consumeMain errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }

    public void consumeApiMq(String mqName,ApiMq apiMq){
        try {
            List<MessageResponseVo> pendingMessageList = MessageHelper.getMessages(mqName);
            if(pendingMessageList != null && pendingMessageList.size() > 0) {
                for(MessageResponseVo message : pendingMessageList)  {
                    this.consumeMessage(message,apiMq,mqName);
                }
            } else {
                logger.info("consumeMain pendingMessageList is null !!!,mqName" +mqName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("consumeApiMq errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }

    public void consumeMessage(MessageResponseVo message,ApiMq apiMq,String mqName){
        try {
            MessageInfo messageInfo = new Gson().fromJson(message.getMessageBody(), MessageInfo.class);
            for(QueueNameJobEnum queueNameEnum : QueueNameJobEnum.values()) {
                if(queueNameEnum.getCode().equalsIgnoreCase(apiMq.getName())) {
                    Map<String,Object> resultMap = queueNameEnum.getMapping().handleMappingAndExecute(new Gson().toJson(messageInfo.getBody()));
                    consumeResult(resultMap,mqName,messageInfo,queueNameEnum,apiMq);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("consumeMessage errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }

    public void consumeResult(Map<String,Object> resultMap,String queueName,MessageInfo message,QueueNameJobEnum queueNameEnum,ApiMq apiMq){
        try {
            logger.info("consumeResultResultMap:"+new Gson().toJson(resultMap));
            MessageRequestVo mrv = new MessageRequestVo();
            String status = resultMap.get("status") == null ? "" : resultMap.get("status").toString();
            resultMap.put("data", message);
            if(status.equals(StatusType.SUCCESS+"")) {
                mrv.setQueueName(queueName+ QueueTypeEnum.SUCCESS.getCode());
                mrv.setRequestBody(new Gson().toJson(resultMap));
                MessageHelper.putMessage(mrv);
                logger.info("consumeResultPutMessageBySuccess:"+new Gson().toJson(mrv));
            } else {
                Object obj = resultMap.get("warningMaps");
                if(obj != null){
                    resultMap.remove("warningMaps");
                    resultMap.put("warning",obj);
                }

                mrv.setQueueName(queueName+QueueTypeEnum.ERROR.getCode());
                mrv.setRequestBody(new Gson().toJson(resultMap));
                String redisKey = MessageHelper.putMessage(mrv);
                if(obj == null) {
                    this.putErrorMessage(resultMap,queueNameEnum,redisKey,apiMq);
                } else {
                    List<Map<String,Object>> warningMaps = (List<Map<String, Object>>) obj;
                    for(Map<String,Object> map : warningMaps) {
                        map.put("product_code",resultMap.get("product_code"));
                        map.put("color_code",resultMap.get("color_code"));
                        map.put("brand_id",resultMap.get("brand_id"));
                        this.putErrorMessage(map,queueNameEnum,redisKey,apiMq);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("consumeResult errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }

    public void putErrorMessage(Map<String,Object> resultMap,QueueNameJobEnum queueNameEnum,String redisKey,ApiMq apiMq){
        try {
            String product_code = resultMap.get("product_code") == null ? "" : resultMap.get("product_code").toString();
            String color_code = resultMap.get("color_code") == null ? "" : resultMap.get("color_code").toString();
            String brand_id = resultMap.get("brand_id") == null ? "" : resultMap.get("brand_id").toString();
            String key = resultMap.get("key") == null ? "" : resultMap.get("key").toString();
            String sku_size = resultMap.get("sku_size") == null ? "" : resultMap.get("sku_size").toString();
            String value = resultMap.get("value") == null ? "" : resultMap.get("value").toString();
            String info = resultMap.get("info") == null ? "" : resultMap.get("info").toString();
            ApiErrorTypeEnum.errorType errorType = resultMap.get("error_enum") == null ? null : (ApiErrorTypeEnum.errorType)resultMap.get("error_enum");

            if(errorType != null) {
                long api_error_type_id = 0L;
                ApiErrorType apiErrorType = iApiErrorTypeService.selectByName(errorType.getCode());
                if(apiErrorType == null) {
                    apiErrorType = new ApiErrorType();
                    apiErrorType.setErrorTypeDesc(errorType.getDesc());
                    apiErrorType.setEnabled(EnabledType.USED);
                    apiErrorType.setErrorTypeName(errorType.getCode());
                    api_error_type_id = iApiErrorTypeService.insert(apiErrorType);
                } else {
                    api_error_type_id = apiErrorType.getApiErrorTypeId();
                }

                ApiErrorProcessing apiErrorProcessing = new ApiErrorProcessing();
                apiErrorProcessing.setApiMqId(apiMq.getApiMqId());
                apiErrorProcessing.setApiErrorTypeId(apiErrorType.getApiErrorTypeId());
                apiErrorProcessing.setErrorId(redisKey);
                apiErrorProcessing.setBoutiqueId(product_code);
                apiErrorProcessing.setSkuSize(sku_size);
                apiErrorProcessing.setBrandId(brand_id);
                apiErrorProcessing.setColorCode(color_code);
                apiErrorProcessing.setDataField(key);
                apiErrorProcessing.setBoutiqueData(value);
                apiErrorProcessing.setNoProcess(true);
                apiErrorProcessing.setEnabled(true);
                apiErrorProcessing.setCreateTime(new Date());
                if(errorType.getCode().equals(ApiErrorTypeEnum.errorType.Runtime_exception.getCode())) {
                    apiErrorProcessing.setDataField("Exception");
                    apiErrorProcessing.setBoutiqueData(info);
                }
                logger.info("iApiErrorProcessingServiceInSert:"+new Gson().toJson(apiErrorProcessing));
                iApiErrorProcessingService.insert(apiErrorProcessing);
            } else {
                logger.info("putErrorMessage errorType is null !!!" + new Gson().toJson(resultMap));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("putErrorMessage errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
    }
}
