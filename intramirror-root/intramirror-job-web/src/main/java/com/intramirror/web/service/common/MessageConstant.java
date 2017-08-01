package com.intramirror.web.service.common;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:jerry
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class MessageConstant {

    /**
     * 每次获取消息记录数
     */
    public static final int BATCH_SIZE = 15;

    /**
     * 消息延迟时间，单位为秒；
     * 取值范围：0 ~ 604800秒(7天)；
     * 当该属性大于0时，发送到队列的所有消息是延迟消息，经过该属性指定的秒数后变为可消费消息；
     * 如果发送消息请求指定了DelaySeconds，以请求指定的DelaySeconds为准；
     */
    public static final Integer DELAY_SECONDS = 0;

    /**
     * 消息体最大长度，单位为Byte；
     * 取值范围：1024 ~ 65536Byte(64KB)；
     * 该属性用于限制发送到队列的消息体最大长度，超过该长度发送消息失败；
     */
    public static final Long MAXIMUN_MESSAGE_SIZE = 65536L;

    /**
     * 消息最长保留时间，单位为秒；
     * 取值范围：60 ~ 1296000秒(15天)；
     * 发送到队列的消息最长保留该时长，超过指定时间，无论消息是否被消费都将被删除；
     */
    public static final Integer MESSAGE_RETENTION_PERIOD = 1296000;

    /**
     * 消息被receive后的隐藏时长，单位为秒；
     * 取值范围：1 ~ 43200秒(12小时)；
     * 消息被receive后，在该属性指定的这段时间内消息处于隐藏状态，在这段时间内，可以删除消息或者修改消息隐藏时长；超过这段时间，消息可以再次被receive；
     */
    public static final Integer VISIBILITY_TIMEOUT = 43200;

    /**
     * (batch)receive message请求最长等待时间，单位为秒；
     * 取值范围：0 ~ 30秒；
     * 当队列中没有消息时，(batch)receive message请求将挂在 MNS 服务器端；在该属性指定的时间范围内，如果有消息发送到队列中，立即返回消息给用户；如果超过该时间，仍然没有消息，返回MessageNotExist；
     * 如果(batch)receive message请求指定了wait seconds，以请求指定的时间为准；
     */
    public static final Integer POLLING_WAIT_SECONDS = 30;

    /**
     * 是否开启日志管理功能；
     * 取值范围：True/False；
     * 当该属性为True时，MNS 将收集队列的消息操作日志推送到指定的地方，日志管理请参考详情；
     */
    public static final Boolean LOGGING_ENABLED = true;

}
