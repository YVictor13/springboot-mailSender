package springmailsender.demo.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import springmailsender.demo.Dto.MailDto;
import javax.mail.MessagingException;
import java.util.Date;
import java.util.Objects;

/**
 * 邮件业务类 Ma了Service
 */
@Service
public class MailService {

    private Logger logger = LoggerFactory.getLogger(getClass()); //提供日志类
    @Autowired
    private JavaMailSenderImpl mailSender ;  //注入邮件工具类

    /*
    发送邮件
     */
    public MailDto sendMail(MailDto mailDto){
        try {
            checkMail(mailDto); //检测邮件
            sendMimeMail(mailDto); //发送邮件
            return saveMail(mailDto);//保存邮件
        }catch(Exception e){
            logger.error("发送邮件失败！！");// 输出错误信息
            mailDto.setStatus("fail");
            mailDto.setError(e.getMessage());
            return mailDto;
        }
    }

    /*
    检查邮件信息
     */
    private void checkMail(MailDto mailDto){
        if(StringUtils.isEmpty(mailDto.getTo())){
            throw new RuntimeException("邮件收信人不能为空");
        }
        if(StringUtils.isEmpty(mailDto.getSubject())){
            throw new RuntimeException("邮件主题不能为空");
        }
        if(StringUtils.isEmpty(mailDto.getText())){
            throw new RuntimeException("邮件内容不能为空");
        }
    }

    /**
     *构建赋值有啊进信息类
     */
    private void sendMimeMail(MailDto mailDto){
        try {
            //true 表示支持复杂类型数据
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(),true);
            mailDto.setFrom(getMailSendFrom());  //从配置项中获取邮件发送人
            messageHelper.setFrom(mailDto.getFrom());
            messageHelper.setTo(mailDto.getTo().split(",")); //按照, 逗号进行分隔
            messageHelper.setSubject(mailDto.getSubject());
            messageHelper.setText(mailDto.getText());
            //抄送
            if(!StringUtils.isEmpty(mailDto.getCc())){
                messageHelper.setCc(mailDto.getCc());
            }
            //密送
            if(!StringUtils.isEmpty(mailDto.getBcc())){
                messageHelper.setBcc(mailDto.getBcc().split(",")); //密抄
            }
            //添加邮件附件
            if(mailDto.getMultipartFiles()!=null){
                for(MultipartFile multipartFile:mailDto.getMultipartFiles()){
                    messageHelper.addAttachment(Objects.requireNonNull(multipartFile.getOriginalFilename()),multipartFile);
                }
            }
            // 发送时间
            if(mailDto.getSentDate()==null){
                mailDto.setSentDate(new Date());
                messageHelper.setSentDate(mailDto.getSentDate());
            }
            mailSender.send(messageHelper.getMimeMessage()); // 正式发送邮件
            mailDto.setStatus("ok");
            // 打日志
            logger.info("发送邮件成功：{}->{}",mailDto.getFrom(),mailDto.getTo());

        } catch (MessagingException e) {
            throw new RuntimeException(e); //发送失败
        }
    }

    //保存邮件
    private MailDto saveMail(MailDto mailDto){
        /**
         * 将邮件保存发哦数据库，自行实现
         */
        return mailDto;
    }

    //获取邮件发信人
    public String getMailSendFrom(){
        // 通过getJavaMailProperties 与 getProperty 获取配置文件中的from 内容
        return mailSender.getJavaMailProperties().getProperty("from");
    }

}
