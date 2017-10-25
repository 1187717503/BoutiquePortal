package com.intramirror.common.help;

import java.io.FileOutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * 邮件发送
 *
 * @author wzh
 */
public class SendMailUtil {

    public static void sendMail(SendEmailVo info) throws Exception {
        Properties prop = new Properties();
        // 设置邮件服务器主机名
//		     prop.setProperty("mail.host", "smtp.qq.com");
        prop.setProperty("mail.host", info.getHost());
        // 发送邮件协议名称
        prop.setProperty("mail.transport.protocol", "smtp");
        // 发送服务器需要身份验证
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");

        //1、创建session
        Session session = Session.getInstance(prop);

        //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        session.setDebug(true);

        //2、通过session得到transport对象
        Transport ts = session.getTransport();

        //3、连上邮件服务器，需要发件人提供邮箱的用户名和密码进行验证
        ts.connect(info.getHost(), Integer.valueOf(info.getPort()), info.getUserName(), info.getPassWord());


        //4、创建邮件
        Message message = createSimpleMail(session, info);
//		     Message message =createImageMail(session);
//		     Message message =createAttachMail(session);

        //5、发送邮件
        ts.sendMessage(message, message.getAllRecipients());
        ts.close();
    }


    /**
     * 发送纯文字邮件信息
     *
     * @param session
     * @param SendEmailVo info
     * @return
     * @throws Exception
     */
    public static MimeMessage createSimpleMail(Session session, SendEmailVo info)
            throws Exception {
        //创建邮件对象
        MimeMessage message = new MimeMessage(session);

        //指明邮件的发件人
        message.setFrom(new InternetAddress(info.getUserName()));

        //指明邮件的收件人，发件人和收件人是一样的，那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(info.getAddressee()));

        //邮件的标题
        message.setSubject(info.getTitle());

        //邮件的文本内容
        message.setContent(info.getContent(), "text/html;charset=UTF-8");

        //返回创建好的邮件对象
        return message;
    }


    /**
     * 发送带图片的邮件
     *
     * @param session
     * @param SendEmailVo info
     * @return
     * @throws Exception
     */
    public static MimeMessage createImageMail(Session session, SendEmailVo info) throws Exception {
        //创建邮件
        MimeMessage message = new MimeMessage(session);

        //发件人
        message.setFrom(new InternetAddress(info.getUserName()));

        //收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(info.getAddressee()));

        //邮件标题
        message.setSubject(info.getTitle());

        // 准备邮件正文数据
        MimeBodyPart text = new MimeBodyPart();
        text.setContent("这是一封邮件正文带图片的邮件</br><img src='cid:123.png'>", "text/html;charset=UTF-8");

        // 准备图片数据
        MimeBodyPart image = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource("D:\\123.png"));
        image.setDataHandler(dh);
        image.setContentID("123.png");

        // 描述数据关系
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(text);
        mm.addBodyPart(image);
        mm.setSubType("related");

        message.setContent(mm);
        message.saveChanges();
        //将创建好的邮件写入到E盘以文件的形式进行保存
//	          message.writeTo(new FileOutputStream("D:\\ImageMail.eml"));
        //返回创建好的邮件
        return message;
    }


    /**
     * 发送带附件的邮件
     *
     * @param session
     * @param info
     * @return
     * @throws Exception
     */
    public static MimeMessage createAttachMail(Session session, SendEmailVo info) throws Exception {
        MimeMessage message = new MimeMessage(session);

        //发件人
        message.setFrom(new InternetAddress(info.getUserName()));

        //收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(info.getAddressee()));

        //邮件标题
        message.setSubject(info.getTitle());

        //创建邮件正文，为了避免邮件正文中文乱码问题，需要使用charset=UTF-8指明字符编码
        MimeBodyPart text = new MimeBodyPart();
        text.setContent("使用JavaMail创建的带附件的邮件", "text/html;charset=UTF-8");

        //创建邮件附件
        MimeBodyPart attach = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource("D:\\123.png"));
        attach.setDataHandler(dh);
        attach.setFileName(dh.getName());

        //创建容器描述数据关系
        MimeMultipart mp = new MimeMultipart();
        mp.addBodyPart(text);
        mp.addBodyPart(attach);
        mp.setSubType("mixed");

        message.setContent(mp);
        message.saveChanges();
        //将创建的Email写入到E盘存储
//    	       message.writeTo(new FileOutputStream("D:\\attachMail.eml"));
        //返回生成的邮件
        return message;
    }


}
