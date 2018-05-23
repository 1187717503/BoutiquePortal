package com.intramirror.order.core.utils;

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
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 设置utf-8或GBK编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        messageHelper.setFrom(MailConfig.emailFrom);
        // 处理收件人地址
        if (mailModelVO.getToEmails() != null) {
            String[] toEmailArray = mailModelVO.getToEmails().split(";");
            List<String> toEmailList = new ArrayList<String>();
            if (toEmailArray.length == 0) {
                logger.error("收件人邮箱不得为空！");
            } else {
                for (String s : toEmailArray) {
                    if (s != null && !s.equals("")) {
                        toEmailList.add(s);
                    }
                }
                if (toEmailList.size() <= 0) {
                    logger.error("收件人邮箱不得为空！");
                } else {
                    toEmailArray = new String[toEmailList.size()];
                    for (int i = 0; i < toEmailList.size(); i++) {
                        toEmailArray[i] = toEmailList.get(i);
                    }
                }
            }
            messageHelper.setTo(toEmailArray);
        } else {
            messageHelper.setTo(MailConfig.emailTo.split(";"));
        }
        if (StringUtils.isNotBlank(mailModelVO.getSubject())) {
            messageHelper.setSubject(mailModelVO.getSubject());
        } else {
            logger.error("邮件主题不能为空！");
        }
        messageHelper.setText(mailModelVO.getContent(), true);

        // 添加附件
        if (!CollectionUtils.isEmpty(mailModelVO.getAttachments())) {
            for (MailAttachmentVO mailAttachmentVO : mailModelVO.getAttachments()) {
                if (null == mailAttachmentVO.getFileName() || null == mailAttachmentVO.getFileUrl()) {
                    throw new RuntimeException("请确认每个附件的名称和地址是否齐全！");
                }
                File file = new File(mailAttachmentVO.getFileUrl());
                if (!file.exists()) {
                    throw new RuntimeException("附件" + mailAttachmentVO.getFileUrl() + "不存在！");
                }

                FileSystemResource fileResource = new FileSystemResource(file);
                messageHelper.addAttachment(mailAttachmentVO.getFileName(), fileResource);
            }
        }
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
