package wisp.utils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javax.mail.*;

public class EmailUtil
{
    // 发件人电子邮箱
    private static String from = "hawke0323@163.com";
    // 指定发送邮件的主机为 localhost
    private static String host = "smtp.163.com";
    //账号密码
    private static String userName = "hawke0323@163.com";
    private static String password = "TINRZCSVUYXQXRVR";

    public static void sendEmail(String paraSendTo, String paraSubject, String paraText){

        // 获取系统属性
        Properties properties = System.getProperties();

        properties.put("mail.smtp.auth", "true");
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(userName, password);
            }
        });

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);
            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));
            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(paraSendTo));
            // Set Subject: 头部头字段
            message.setSubject(paraSubject);
            // 设置消息体
            message.setText(paraText);
            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void sendEmail(String paraSubject, String paraText){
        sendEmail("issachawke@hotmail.com", paraSubject, paraText);
    }

    public static void main(String [] args)
    {
        sendEmail("issachawke@hotmail.com", "Hello", "Happy Lucky Smile Yeah");
    }
}