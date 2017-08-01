package com.intramirror.web.properties.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by dingyifan on 2017/8/1.
 * 这个类是项目启动时读取消息队列(MQ)的配置
 *
 */
@Component
public class MessageProperties {

    @Value("message.mns.accountendpoint")
    private String mnsAccountEndPoint;

    @Value("message.mns.accesskeyid")
    private String mnsAccessKeyId;

    @Value("message.mns.accesskeysecret")
    private String mnsAccessKeySecret;

    public String getMnsAccountEndPoint() {
        return mnsAccountEndPoint;
    }

    public void setMnsAccountEndPoint(String mnsAccountEndPoint) {
        this.mnsAccountEndPoint = mnsAccountEndPoint;
    }

    public String getMnsAccessKeyId() {
        return mnsAccessKeyId;
    }

    public void setMnsAccessKeyId(String mnsAccessKeyId) {
        this.mnsAccessKeyId = mnsAccessKeyId;
    }

    public String getMnsAccessKeySecret() {
        return mnsAccessKeySecret;
    }

    public void setMnsAccessKeySecret(String mnsAccessKeySecret) {
        this.mnsAccessKeySecret = mnsAccessKeySecret;
    }
}
