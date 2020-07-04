package springmailsender.demo.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

@Data
public class MailDto {
    private String id; //邮件Id
    private String from; //邮件发送人
    private String to; //邮件接收人。多个邮件用逗号隔开
    private String Subject; // 邮件主题
    private String text; // 邮件内容
    private Date sentDate; // 发送时间
    private String cc;  // 抄送（多好有效用逗号隔开）
    private String bcc; // 密送（多个邮箱用逗号隔开）
    private String status; //状态
    private String error; //报错信息
    @JsonIgnore
    private MultipartFile[] multipartFiles; //邮件附件
}
