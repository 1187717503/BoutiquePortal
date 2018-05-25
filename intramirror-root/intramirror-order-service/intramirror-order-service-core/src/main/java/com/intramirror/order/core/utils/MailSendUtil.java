package com.intramirror.order.core.utils;

import com.google.gson.Gson;
import com.intramirror.common.help.StringUtils;
import com.intramirror.order.api.vo.MailAttachmentVO;
import com.intramirror.order.api.vo.MailModelVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by yaods on 2018/5/21.
 */
public class MailSendUtil {

    private static Logger logger = LoggerFactory.getLogger(MailSendUtil.class);

    private static JavaMailSenderImpl mailSender = createMailSender();
    /**
     * 邮件发送器
     *
     * @return 配置好的工具
     */
    private static JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(MailConfig.host);
        sender.setPort(MailConfig.port);
        sender.setUsername(MailConfig.userName);
        sender.setPassword(MailConfig.password);
        sender.setDefaultEncoding("Utf-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.timeout", MailConfig.timeout);
        p.setProperty("mail.smtp.auth", "false");
        sender.setJavaMailProperties(p);
        return sender;
    }

    /**
     * 发送邮件
     * @param mailModelVO
     * @throws MessagingException 异常
     * @throws UnsupportedEncodingException 异常
     */
    public static void sendMail(MailModelVO mailModelVO) throws MessagingException,UnsupportedEncodingException {
        logger.info("sendMail params={}", new Gson().toJson(mailModelVO));
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 设置utf-8或GBK编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        messageHelper.setFrom(MailConfig.emailFrom);
        logger.info("sendMail 处理收件人地址");
        // 处理收件人地址
        if (mailModelVO.getToEmails() != null) {
            messageHelper.setTo(mailModelVO.getToEmails().split(";"));
        } else {
            messageHelper.setTo(MailConfig.emailTo.split(";"));
        }
        logger.info("sendMail 设置邮件标题");
        if (StringUtils.isNotBlank(mailModelVO.getSubject())) {
            messageHelper.setSubject(mailModelVO.getSubject());
        } else {
            logger.warn("Subject is blank.");
            messageHelper.setSubject("IntraMirror Mail");
        }
        logger.info("sendMail 设置邮件内容");
        messageHelper.setText(mailModelVO.getContent(), true);

        // 添加附件
        logger.info("sendMail 设置邮件附件");
        if (!CollectionUtils.isEmpty(mailModelVO.getAttachments())) {
            for (MailAttachmentVO mailAttachmentVO : mailModelVO.getAttachments()) {
                if (StringUtils.isBlank(mailAttachmentVO.getFileUrl())) {
                    logger.warn("Attachment file url is blank.");
                    continue;
                }
                File file = new File(mailAttachmentVO.getFileUrl());
                if (!file.exists()) {
                    logger.warn("Attachment file is not exists.");
                    continue;
                }
                if (null == mailAttachmentVO.getFileName()) {
                    logger.warn("Attachment file name is null.");
                    mailAttachmentVO.setFileName(String.valueOf(System.currentTimeMillis()));
                }
                FileSystemResource fileResource = new FileSystemResource(file);
                messageHelper.addAttachment(mailAttachmentVO.getFileName(), fileResource);
            }
        }
        logger.info("sendMail 开始发送邮件");
        mailSender.send(mimeMessage);
    }

    public static void main(String[] args) {
        try {
            MailModelVO vo = new MailModelVO();
            vo.setSubject("今天的测试结果");
            vo.setContent("无");
            vo.setToEmails("duoshi.yao@intramirror.com");
            sendMail(vo);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
