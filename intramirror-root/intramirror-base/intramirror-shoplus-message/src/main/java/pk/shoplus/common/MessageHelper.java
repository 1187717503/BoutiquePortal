
package pk.shoplus.common;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pk.shoplus.common.exception.MessageFailException;
import pk.shoplus.common.service.MessageService;
import pk.shoplus.common.service.OpMessageService;
import pk.shoplus.common.service.impl.MNSMessageServiceImpl;
import pk.shoplus.common.service.impl.OPMNSMessageServiceImpl;
import pk.shoplus.common.utils.ConfigurationProperties;
import pk.shoplus.common.vo.MessageRequestVo;
import pk.shoplus.common.vo.MessageResponseVo;
import pk.shoplus.common.vo.BatchMessageRequestVo;
import pk.shoplus.common.vo.MessageRequestVo;
import pk.shoplus.common.vo.MessageResponseVo;
import pk.shoplus.mq.enums.QueueTypeEnum;
import pk.shoplus.service.RedisService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 消息队列
 * @author jerry
 */
public class MessageHelper {

    private final Logger LOGGER = Logger.getLogger(MessageHelper.class);

    /**
     * 推送消息到MQ
     * @param messageRequestVo
     * @throws MessageFailException
     */
    public static String putMessage (MessageRequestVo messageRequestVo) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(messageRequestVo != null){
            messageRequestVo.setQueueName(messageRequestVo.getQueueName() + ConfigurationProperties.getInstance().getMq_env());}
        /** end update 2017-06-20 */

        MessageService messageService = new MNSMessageServiceImpl();

        //如果是error 则存入redis
        if(messageRequestVo != null && messageRequestVo.getQueueName().contains(QueueTypeEnum.ERROR.getCode())){
        	return putMessageToRedis(messageRequestVo);
        
        //其他则存入MQ
        }else{
            messageService.putMessage(messageRequestVo);
        }
        return "SUCCESS";
    }
    

    /**
     * 批量推送消息到MQ单批最多不能超过15条
     * @param batchMessageRequestVo
     * @throws MessageFailException
     */
    public static void batchPutMessage (BatchMessageRequestVo batchMessageRequestVo) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(batchMessageRequestVo != null){
            batchMessageRequestVo.setQueueName(batchMessageRequestVo.getQueueName() + ConfigurationProperties.getInstance().getMq_env());}
        /** end update 2017-06-20 */

        MessageService messageService = new MNSMessageServiceImpl();
        messageService.batchPutMessage(batchMessageRequestVo);
    }

    /**
     * 从MQ里获取消息内容
     * @param queueName
     * @return
     * @throws MessageFailException
     */
    public static List<MessageResponseVo> getMessages (String queueName) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */

        MessageService messageService = new MNSMessageServiceImpl();
        //如果是error 则从redis中获取
        if(StringUtils.isNotBlank(queueName) && queueName.contains(QueueTypeEnum.ERROR.getCode())){
        	return getMessagesFromRedis(queueName,15);
        }else{
        	return messageService.getMessage(queueName);
        }
    }

    /**
     * 从MQ里获取消息内容
     * @param queueName
     * @param isDeleted
     * @return
     * @throws MessageFailException
     */
    public static List<MessageResponseVo> getMessages (String queueName,boolean isDeleted) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */
        MessageService messageService = new MNSMessageServiceImpl();
        
        //如果是error 则从redis中获取
        if(StringUtils.isNotBlank(queueName) && queueName.contains(QueueTypeEnum.ERROR.getCode())){
        	return getMessagesFromRedis(queueName,15);
        }else{
            return messageService.getMessage(queueName,isDeleted);
        }

    }

    /**
     * 判断MQ中是否存在消息
     * @param queueName
     * @return
     * @throws MessageFailException
     */
    public static boolean isMessageNotExist(String queueName) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */
    	 MessageService messageService = new MNSMessageServiceImpl();
         return messageService.isMessageNotExist(queueName);
    }

    /**
     * 删除MQ中的消息
     * @param queueName
     * @param receiptHandle
     * @throws MessageFailException
     */
    public static void deleteMessageByReceiptHandle(String queueName,String receiptHandle) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */
   	 	MessageService messageService = new MNSMessageServiceImpl();
        //如果是error 则从redis中删除
        if(StringUtils.isNotBlank(queueName) && queueName.contains(QueueTypeEnum.ERROR.getCode())){
        	deleteMessageFromRedis(queueName,receiptHandle);
        }else{
            messageService.deleteMessageByReceiptHandle(queueName,receiptHandle);
        }

    }

    /**
     * 创建队列
     * @param queueName
     * @throws MessageFailException
     */
    public static void createQueue(String queueName) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */
    	OpMessageService messageService = new OPMNSMessageServiceImpl();
    	messageService.createQueue(queueName);
    }

    /**
     * 删除队列
     * @param queueName
     * @throws MessageFailException
     */
    public static void deleteQueue(String queueName) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */
    	OpMessageService messageService = new OPMNSMessageServiceImpl();
        //如果是error 则从redis中删除
        if(StringUtils.isNotBlank(queueName) && queueName.contains(QueueTypeEnum.ERROR.getCode())){
        	deleteMapFromRedis(queueName);
        }else{
        	messageService.deleteQueue(queueName);
        }
    }

    /**
     * 推送消息到redis
     * @param messageRequestVo
     * @throws MessageFailException
     */
    private static String putMessageToRedis (MessageRequestVo messageRequestVo) throws MessageFailException {
//        RedisService redis = new RedisService();
        /** start update 2017-06-20 环境配置
        if(messageRequestVo != null){
            messageRequestVo.setQueueName(messageRequestVo.getQueueName() + ConfigurationProperties.getInstance().getMq_env());}
        * end update 2017-06-20 */
        String key = Helper.getUUID();
    	RedisService.getInstance().putMap(messageRequestVo.getQueueName(), key, messageRequestVo.getRequestBody());
    	return key;
    }
    
    
    /**
     * 从redis里获取消息内容
     * @param queueName 
     * @param limit 获取指定条数 
     * @return
     * @throws MessageFailException
     */
    private static List<MessageResponseVo> getMessagesFromRedis (String queueName,int limit) throws MessageFailException {
        /** start update 2017-06-20 环境配置
        //if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        * end update 2017-06-20 */

//    	RedisService redis = new RedisService();
    	Map<String,Object> map = RedisService.getInstance().getMap(queueName);
    	System.out.println(map.size());
    	List<MessageResponseVo> resultList = new ArrayList<MessageResponseVo>();
    	
    	//遍历redis对象，转换成mq对象
    	if(map != null){
    		int i = 0;
    		for(Entry<String, Object> entry : map.entrySet()){
    			i ++;
    			MessageResponseVo messageResponseVo = new MessageResponseVo();
    			messageResponseVo.setReceiptHandle(entry.getKey());
    			messageResponseVo.setMessageId(entry.getKey());
    			messageResponseVo.setMessageBody(entry.getValue().toString());
    			resultList.add(messageResponseVo);
    			
    			//到达指定条数,退出循环
    			if(i >= limit){
    				break;
    			}
    			
    		}
    	}
    	
        return resultList;
    }

    public static Map<String,Object> getMessagesFromRedis(String queueName) {
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */
        Map<String,Object> map = RedisService.getInstance().getMap(queueName);
        return map;
    }

    /**
     * 根据receiptHandle 获取redis中 map的消息
     * @param queueName
     * @param receiptHandle
     * @throws MessageFailException
     */
    public static Object getMessageFromRedis (String queueName,String receiptHandle) throws MessageFailException {
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */

        return RedisService.getInstance().getMapInfoByKey(queueName, receiptHandle);
    }
    
    
    
    /**
     * 删除redis中消息
     * @param queueName
     * @param receiptHandle
     * @throws MessageFailException
     */
    public static void deleteMessageFromRedis (String queueName,String receiptHandle) throws MessageFailException {
//        RedisService redis = new RedisService();
        /** start update 2017-06-20 环境配置
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */
        RedisService.getInstance().removeMapInfo(queueName, receiptHandle);
    }
    
    
    /**
     * 删除redis 中 map对象
     * @param queueName
     * @throws MessageFailException
     */
    public static void deleteMapFromRedis (String queueName) throws MessageFailException {
//        RedisService redis = new RedisService();
        /** start update 2017-06-20 环境配置*/
        if(StringUtils.isNotBlank(queueName)){queueName = queueName + ConfigurationProperties.getInstance().getMq_env();}
        /** end update 2017-06-20 */
    	RedisService.getInstance().deleteMap(queueName);
    }

    /*public static void main(String[] args) {
        MessageRequestVo messageRequestVo = new MessageRequestVo();
        messageRequestVo.setQueueName("AtelierCreateProduct13");
        messageRequestVo.setRequestBody("{\"results\":{\"reqCode\":200,\"count\":1,\"items\":[{\"product_id\":\"587e34d9fd7955c0bff4515c1\",\"product_reference\":\"387111KHNGN\",\"color_reference\":\"9692\",\"first_category\":\"BAGS\",\"second_category\":\"SHOULDER BAGS\",\"gender\":\"MAN\",\"brand\":\"GUCCI\",\"item_name\":\"GG SUPREME SHOULDER BAG\",\"item_intro\":\"SHOULDER BAG\",\"item_description\":\"Leather shoulder bag\",\"color\":\"Beige\",\"season_year\":\"2017\",\"season_reference\":\"PE\",\"made_in\":\"Italy\",\"suitable\":[{\"name\":\"Width \",\"value\":\"10,63\"},{\"name\":\"Height \",\"value\":\"10,63\"},{\"name\":\"Depth \",\"value\":\"1,18\"}],\"technical_info\":[{\"name\":\"Leather\",\"percentage\":\"\"}],\"price\":483.61,\"currency\":\"EUR\",\"variants\":[{\"sizerange\":\"5\",\"sizeposition\":\"01\",\"size\":\"UNI\",\"quantity\":0,\"barcodes\":[\"010047551701\",\"010052160401\"]}],\"item_images\":[],\"retail_price\":590}]}}");

        MessageRequestVo messageRequestVo2 = new MessageRequestVo();
        messageRequestVo2.setQueueName("AtelierCreateProduct13");
        messageRequestVo2.setRequestBody("{\"results\":{\"reqCode\":200,\"count\":1,\"items\":[{\"product_id\":\"587e34d9fd7955c0bff4515c2\",\"product_reference\":\"387111KHNGN\",\"color_reference\":\"9692\",\"first_category\":\"BAGS\",\"second_category\":\"SHOULDER BAGS\",\"gender\":\"MAN\",\"brand\":\"GUCCI\",\"item_name\":\"GG SUPREME SHOULDER BAG\",\"item_intro\":\"SHOULDER BAG\",\"item_description\":\"Leather shoulder bag\",\"color\":\"Beige\",\"season_year\":\"2017\",\"season_reference\":\"PE\",\"made_in\":\"Italy\",\"suitable\":[{\"name\":\"Width \",\"value\":\"10,63\"},{\"name\":\"Height \",\"value\":\"10,63\"},{\"name\":\"Depth \",\"value\":\"1,18\"}],\"technical_info\":[{\"name\":\"Leather\",\"percentage\":\"\"}],\"price\":483.61,\"currency\":\"EUR\",\"variants\":[{\"sizerange\":\"5\",\"sizeposition\":\"01\",\"size\":\"UNI\",\"quantity\":0,\"barcodes\":[\"010047551701\",\"010052160401\"]}],\"item_images\":[],\"retail_price\":590}]}}");

        MessageRequestVo messageRequestVo3 = new MessageRequestVo();
        messageRequestVo3.setQueueName("AtelierCreateProduct13");
        messageRequestVo3.setRequestBody("{\"results\":{\"reqCode\":200,\"count\":1,\"items\":[{\"product_id\":\"587e34d9fd7955c0bff4515c23\",\"product_reference\":\"387111KHNGN\",\"color_reference\":\"9692\",\"first_category\":\"BAGS\",\"second_category\":\"SHOULDER BAGS\",\"gender\":\"MAN\",\"brand\":\"GUCCI\",\"item_name\":\"GG SUPREME SHOULDER BAG\",\"item_intro\":\"SHOULDER BAG\",\"item_description\":\"Leather shoulder bag\",\"color\":\"Beige\",\"season_year\":\"2017\",\"season_reference\":\"PE\",\"made_in\":\"Italy\",\"suitable\":[{\"name\":\"Width \",\"value\":\"10,63\"},{\"name\":\"Height \",\"value\":\"10,63\"},{\"name\":\"Depth \",\"value\":\"1,18\"}],\"technical_info\":[{\"name\":\"Leather\",\"percentage\":\"\"}],\"price\":483.61,\"currency\":\"EUR\",\"variants\":[{\"sizerange\":\"5\",\"sizeposition\":\"01\",\"size\":\"UNI\",\"quantity\":0,\"barcodes\":[\"010047551701\",\"010052160401\"]}],\"item_images\":[],\"retail_price\":590}]}}");

        MessageRequestVo messageRequestVo4 = new MessageRequestVo();
        messageRequestVo4.setQueueName("AtelierCreateProduct13");
        messageRequestVo4.setRequestBody("{\"results\":{\"reqCode\":200,\"count\":1,\"items\":[{\"product_id\":\"587e34d9fd7955c0bff4515c24\",\"product_reference\":\"387111KHNGN\",\"color_reference\":\"9692\",\"first_category\":\"BAGS\",\"second_category\":\"SHOULDER BAGS\",\"gender\":\"MAN\",\"brand\":\"GUCCI\",\"item_name\":\"GG SUPREME SHOULDER BAG\",\"item_intro\":\"SHOULDER BAG\",\"item_description\":\"Leather shoulder bag\",\"color\":\"Beige\",\"season_year\":\"2017\",\"season_reference\":\"PE\",\"made_in\":\"Italy\",\"suitable\":[{\"name\":\"Width \",\"value\":\"10,63\"},{\"name\":\"Height \",\"value\":\"10,63\"},{\"name\":\"Depth \",\"value\":\"1,18\"}],\"technical_info\":[{\"name\":\"Leather\",\"percentage\":\"\"}],\"price\":483.61,\"currency\":\"EUR\",\"variants\":[{\"sizerange\":\"5\",\"sizeposition\":\"01\",\"size\":\"UNI\",\"quantity\":0,\"barcodes\":[\"010047551701\",\"010052160401\"]}],\"item_images\":[],\"retail_price\":590}]}}");

        MessageRequestVo messageRequestVo5 = new MessageRequestVo();
        messageRequestVo5.setQueueName("AtelierCreateProduct13");
        messageRequestVo5.setRequestBody("{\"results\":{\"reqCode\":200,\"count\":1,\"items\":[{\"product_id\":\"587e34d9fd7955c0bff4515c25\",\"product_reference\":\"387111KHNGN\",\"color_reference\":\"9692\",\"first_category\":\"BAGS\",\"second_category\":\"SHOULDER BAGS\",\"gender\":\"MAN\",\"brand\":\"GUCCI\",\"item_name\":\"GG SUPREME SHOULDER BAG\",\"item_intro\":\"SHOULDER BAG\",\"item_description\":\"Leather shoulder bag\",\"color\":\"Beige\",\"season_year\":\"2017\",\"season_reference\":\"PE\",\"made_in\":\"Italy\",\"suitable\":[{\"name\":\"Width \",\"value\":\"10,63\"},{\"name\":\"Height \",\"value\":\"10,63\"},{\"name\":\"Depth \",\"value\":\"1,18\"}],\"technical_info\":[{\"name\":\"Leather\",\"percentage\":\"\"}],\"price\":483.61,\"currency\":\"EUR\",\"variants\":[{\"sizerange\":\"5\",\"sizeposition\":\"01\",\"size\":\"UNI\",\"quantity\":0,\"barcodes\":[\"010047551701\",\"010052160401\"]}],\"item_images\":[],\"retail_price\":590}]}}");

        MessageRequestVo messageRequestVo6 = new MessageRequestVo();
        messageRequestVo6.setQueueName("AtelierCreateProduct13");
        messageRequestVo6.setRequestBody("{\"results\":{\"reqCode\":200,\"count\":1,\"items\":[{\"product_id\":\"587e34d9fd7955c0bff4515c26\",\"product_reference\":\"387111KHNGN\",\"color_reference\":\"9692\",\"first_category\":\"BAGS\",\"second_category\":\"SHOULDER BAGS\",\"gender\":\"MAN\",\"brand\":\"GUCCI\",\"item_name\":\"GG SUPREME SHOULDER BAG\",\"item_intro\":\"SHOULDER BAG\",\"item_description\":\"Leather shoulder bag\",\"color\":\"Beige\",\"season_year\":\"2017\",\"season_reference\":\"PE\",\"made_in\":\"Italy\",\"suitable\":[{\"name\":\"Width \",\"value\":\"10,63\"},{\"name\":\"Height \",\"value\":\"10,63\"},{\"name\":\"Depth \",\"value\":\"1,18\"}],\"technical_info\":[{\"name\":\"Leather\",\"percentage\":\"\"}],\"price\":483.61,\"currency\":\"EUR\",\"variants\":[{\"sizerange\":\"5\",\"sizeposition\":\"01\",\"size\":\"UNI\",\"quantity\":0,\"barcodes\":[\"010047551701\",\"010052160401\"]}],\"item_images\":[],\"retail_price\":590}]}}");

        MessageRequestVo messageRequestVo7 = new MessageRequestVo();
        messageRequestVo7.setQueueName("AtelierCreateProduct13");
        messageRequestVo7.setRequestBody("{\"results\":{\"reqCode\":200,\"count\":1,\"items\":[{\"product_id\":\"587e34d9fd7955c0bff4515c27\",\"product_reference\":\"387111KHNGN\",\"color_reference\":\"9692\",\"first_category\":\"BAGS\",\"second_category\":\"SHOULDER BAGS\",\"gender\":\"MAN\",\"brand\":\"GUCCI\",\"item_name\":\"GG SUPREME SHOULDER BAG\",\"item_intro\":\"SHOULDER BAG\",\"item_description\":\"Leather shoulder bag\",\"color\":\"Beige\",\"season_year\":\"2017\",\"season_reference\":\"PE\",\"made_in\":\"Italy\",\"suitable\":[{\"name\":\"Width \",\"value\":\"10,63\"},{\"name\":\"Height \",\"value\":\"10,63\"},{\"name\":\"Depth \",\"value\":\"1,18\"}],\"technical_info\":[{\"name\":\"Leather\",\"percentage\":\"\"}],\"price\":483.61,\"currency\":\"EUR\",\"variants\":[{\"sizerange\":\"5\",\"sizeposition\":\"01\",\"size\":\"UNI\",\"quantity\":0,\"barcodes\":[\"010047551701\",\"010052160401\"]}],\"item_images\":[],\"retail_price\":590}]}}");

        List<MessageRequestVo> list = new ArrayList<>();
        list.add(messageRequestVo);
        list.add(messageRequestVo2);
        list.add(messageRequestVo3);
        list.add(messageRequestVo4);
        list.add(messageRequestVo5);
        list.add(messageRequestVo6);
        list.add(messageRequestVo7);

        BatchMessageRequestVo batchMessageRequestVo = new BatchMessageRequestVo();
        batchMessageRequestVo.setQueueName("AtelierCreateProduct13");
        batchMessageRequestVo.setMessageRequestVos(list);

        MessageHelper.batchPutMessage(batchMessageRequestVo);

        //System.out.println(JSON.toJSONString(MessageHelper.getMessages("EDSCreateProduct1Success")));
    }*/
}
