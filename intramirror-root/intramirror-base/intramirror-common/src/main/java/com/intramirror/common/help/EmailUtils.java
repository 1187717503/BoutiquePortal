package com.intramirror.common.help;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.poi.util.IOUtils;

/**
 * Created by dingyifan on 2017/12/5.
 */
public class EmailUtils {

    /* email配置文件路径 */
    private static final String emailPath = "/email.properties";

    /* email vo */
    private static SendEmailVo emailVo ;

    static {
        /* 初始化配置 */
        InputStream in = EmailUtils.class.getResourceAsStream(emailPath);
        try {
            Properties props = new Properties();
            props.load(in);

            emailVo = new SendEmailVo();
            emailVo.setHost(props.getProperty("host"));
            emailVo.setPort(props.getProperty("port"));
            emailVo.setUserName(props.getProperty("userName"));
            emailVo.setPassWord(props.getProperty("passWord"));

            emailVo.setHost("smtp.mxhichina.com");
            emailVo.setPort("80");
            emailVo.setUserName("noreply@intramirror.com");
            emailVo.setPassWord("Women4kou%");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }

        /*emailVo = new SendEmailVo();
        emailVo.setHost("smtp.mxhichina.com");
        emailVo.setPort("80");
        emailVo.setUserName("noreply@intramirror.com");
        emailVo.setPassWord("Women4kou%");*/
    }

    /* 发送纯文件邮件 一个收件人 */
    public static void sendTextEmail(String consignee,String title,String content) throws Exception {
        Properties prop = new Properties();
        prop.setProperty("mail.host", emailVo.getHost());
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(prop);
        session.setDebug(true);

        Transport transport = session.getTransport();
        transport.connect(emailVo.getHost(), Integer.valueOf(emailVo.getPort()), emailVo.getUserName(), emailVo.getPassWord());
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailVo.getUserName()));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(consignee));
        message.setSubject(title);
        message.setContent(content, "text/html;charset=UTF-8");
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    /* 发送纯文本邮件 多个收件人 */
    public static void sendTextEmails(List<String> consignees,String title,String content) throws Exception {
        Properties prop = new Properties();
        prop.setProperty("mail.host", emailVo.getHost());
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(prop);
        session.setDebug(true);

        Transport transport = session.getTransport();
        transport.connect(emailVo.getHost(), Integer.valueOf(emailVo.getPort()), emailVo.getUserName(), emailVo.getPassWord());
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailVo.getUserName()));
        for(String consignee : consignees) {
            message.addRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress(consignee) });
        }
        message.setSubject(title);
        message.setContent(content, "text/html;charset=UTF-8");
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
