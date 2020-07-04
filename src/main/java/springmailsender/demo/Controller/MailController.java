package springmailsender.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import springmailsender.demo.Dto.MailDto;
import springmailsender.demo.Service.MailService;

@RestController
public class MailController {
    @Autowired
    private MailService mailService;

    /**
     * 发送邮件的主界面
     */
    @GetMapping("/")
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("/sendMail"); //打开发送邮箱界面
        mv.addObject("from",mailService.getMailSendFrom()); //邮件发信人
        return mv;
    }

    /**
     * 发送邮件
     */
    @PostMapping("/mail/send")
    public MailDto sendMail(MailDto mailDto, MultipartFile[] files){
        mailDto.setMultipartFiles(files);
        return mailService.sendMail(mailDto); //发送邮件和附件
    }
}
