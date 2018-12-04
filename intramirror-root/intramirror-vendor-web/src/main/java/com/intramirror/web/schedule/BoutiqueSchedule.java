package com.intramirror.web.schedule;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.order.api.model.ShipEmailLog;
import com.intramirror.order.api.vo.MailModelVO;
import com.intramirror.order.core.utils.MailSendManageService;
import com.intramirror.order.core.utils.MailSendUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;


/**
 * Created by caowei on 2018/11/15.
 */
@Component
public class BoutiqueSchedule {

    private static final Logger logger = LoggerFactory.getLogger(BoutiqueSchedule.class);

    @Autowired
    private MailSendManageService mailSendManageService;

    @Scheduled(cron = "0 0 2 1/1 * ?")
    //@Scheduled(cron = "0 0/2 * * * ?")
    public void retryShipEmail() {
        List<ShipEmailLog> emailLogs = mailSendManageService.getEmailLog(null);
        if (CollectionUtils.isNotEmpty(emailLogs)) {
            try {
                for (ShipEmailLog emailLog : emailLogs) {

                    String emailBody = emailLog.getEmailBody();
                    logger.info("ship email发送失败的邮件重推，emailBody:{}",emailBody);
                    MailModelVO modelVO = JSONObject.parseObject(emailBody, MailModelVO.class);
                    MailSendUtil.sendMail(modelVO);

                    //更新log
                    emailLog.setIsDeleted(1);
                    emailLog.setUpdateTime(new Date());
                    mailSendManageService.updateEmailLog(emailLog);
                }
            } catch (MessagingException e) {
                e.printStackTrace();
                logger.error("ship email发送失败的邮件重推失败，errMag:{}",e.getMessage());
            }
        } else {
        }
    }
}
