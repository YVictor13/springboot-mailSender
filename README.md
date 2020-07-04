# springboot-mailSender

@[TOC](Springboot 实现发送邮件功能)
### 不是要做一个单纯优秀的人，而是要做一个不可替代的人。 - - 阳墨余
# 一、知识梳理
##  1.1、协议知识
### 1.1.1、SMTP协议
><font color=#03f>STMP全称“Simple Mail Transfer Protocol (简单邮件传输协议)”。</font><font color=#aa0>SMTP是一组用于从源地址到目的地址传送邮件的规则，并且控制信件的中转方式。</font>SMTP协议属于TCP/IP协议族，它帮助每台计算机在发送或者中转信件的时候找到下一个目的地。通过SMTP协议所指定的服务器，我们就可以把E-mail寄到收信人的服务器上，整个过程只需要几分钟。SMTP服务器是遵循SMTP协议的发送邮件服务器，用来发送或中转用户发送的电子邮件。

>【补充】SMTP认证要求必须提供账号和密码（或授权码）才能登录服务器，以此来避免用户受到垃圾邮箱的侵扰。

**工作过程**
SMTP协议的工作过程分为3个过程：
（1）、建立连接
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 在这个阶段，SMTP客户请求与服务器的25端口建立TCP连接，一旦连接建立，SMTP服务器和客户就开始互相通告自己的域名，同时确认对方的域名。
![第一阶段](https://img-blog.csdnimg.cn/2020070411460742.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)
（2）、邮件传送
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  利用命令，SMTP客户将邮件的源地址、目的地址和邮件的具体内容传递给SMTP服务器，SMTP服务器进行相应的响应并接收邮件。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704114902191.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)
（3）、连接释放
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SMTP客户退出命令，服务器在处理命令后进行响应，随后关闭与SMTP客户端的TCP连接

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704115032315.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)
### 1.1.2、IMAP协议
>IMAP全称 Internet Mail Access Protocol (互联网邮件访问协议)，以前称为交互邮件访问协议（Interative Mail Access Protocol）一个应用层协议。IMAP是一种邮件获取协议，它的主要作用是邮件在客户端可以通过协议从邮件服务器上获取邮件的信息，下载、阅读邮件等。它运行在TCP/IP协议族上，使用端口为143，支持在线\离线访问模式，存储邮件的模式为分布式，网络架构：C/S。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704150825137.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)

**IMAP协议流程**

```c
A01 LOGIN ubrabbit_2@fenbu.comingchina.com xxxxx
A02 LIST "" *
A03 SELECT INBOX   #选择收件箱
A04 SEARCH NEW     #查询收件箱所有新邮件
A05 FETCH 5 FULL   #获取第5封邮件头
A06 FETCH 5 RFC822 #获取第5封完整内容
A07 FETCH 5 FLAGS  #查询第5封邮件的标志位
A08 STORE 5 +FLAGS.SILENT (/DELETED) #设置标志位为删除
A09 EXPUNGE        #永久删除当前邮箱INBOX中所有设置了/deleted标志的信件
A10 LOGOUT         #退出登录
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704152354598.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)

### 1.1.3、POP3协议
> POP3是POP协议的第三版，POP协议全称 Post Office Protocol (邮局协议)，用于电子邮件的接收，它使用TCP的 110 端口。它规定了怎样将个人计算机连接到Internet 的邮件服务器和下载电子邮件的电子协议。是因特网电子邮件的第一个离线协议标准，POP3准许用户从服务器上把邮件转存到本地主机上，同时删除保存早邮件服务器上的邮件。支持客户端远程管理服务器端的邮件。

### 1.1.4、POP3与IMAP的区别
>1、IMAP允许双向通信，即客户端的操作会反馈到服务器上，例如在客户端上收取邮件、标记邮件已读等操作，服务器都会跟着同步这些操作。
>2、POP3协议允许客户端下载服务器端邮件，但是客户端的操作不会同步到服务器上面，例如在客户端收取或标记已读邮件，服务器不会同步这些操作。
><font color=red>3、POP与IMAP协议都是一种邮件获取协议。</font>
><font color=red>4、IMAP协议与POP3协议的主要区别在于用户可以不用把所有的邮件全部下载，可以通过客户端直接对服务器上的邮件进行操作。</font>
>5、IMAP协议中，用户可以在不同的客户端操作邮件，但是数据都是同步的，而POP协议搭建的邮件系统不会将数据同步到服务器端，所以说POP协议中的邮件由客户端管理，IMAP协议中的邮件由服务器端进行管理。

### 1.1.5、SMTP、IMAP、POP3协议搭建邮件功能
**SMTP与POP协议：**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704155836976.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)
**SMTP与IMAP协议：**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704160101196.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)


##  1.2、Springboot知识（邮件）
>邮件的发送分为两类，一类便是简单的文本类邮件发送不需要添加附件，另外一种是既有文本内容也有附件，而我们现在用的比较多的还是复杂邮件发送，下面简单介绍下两种邮件发送的SPringboot实现。

>在开始介绍两种实现之前，我们先来了解几个接口与实现类：
>Java后端实现邮件和集成邮件服务的主要工具：
>1、Spring 官方API： JavaMailSender 
>2、Spring API的实现类： JavaMailSenderImpl
>3、<font color =red>使用JavaMailSenderImpl的send方法来发送邮件</font>

>简单邮寄发送：SimpleMailMessage
>集成邮件发送：借助MimeMessageHelper 来构建MimeMessage发送邮件。

### 1.2.1、简单邮件发送
**简单邮件发送**
```java
@Autowired
private JavaMailSenderImpl mailSender;

public void simpleSendMail() throw MessageExcetion{
	SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	simpleMailMessage.setFrom("myemail@qq.com");//发送者的邮箱
	simpleMailMessage.setTo("othermail@qq.com");//接收者的邮箱（当发送内容给多个接收站时，使用逗号“，”隔开）
	simpleMailMessage.setSubject("新年快乐");//邮件主题
	simpleMailMessage.setText("尊敬的X老师：............");//邮件内容
	mailSender.send(simpleMailMessage); //发送邮件
}
```

### 1.2.2、复杂邮件发送
**复杂邮件发送（集成邮件发送）**

```java
@Autowired
private JavaMailSenderImpl mailSender;

public void complexSendMail() throw MessageException{
	//借助MimeMessageHelper来构建mimeMessage发送邮件
	MimeMessage mimeMessage = mailSender.createMimeMessage();
	MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
	messageHelper.setFrom("myemail@qq.com");//发送者的邮箱
	messageHelper.setTo("othermail@qq.com");//接收者的邮箱（当发送内容给多个接收站时，使用逗号“，”隔开）
	messageHelper.setSubject("新年快乐");//邮件主题
	messageHelper.setText("尊敬的X老师：............");//邮件内容
	messageHelper.addInline("happy.gif",new File("xx/xxx/happy.gif"));//添加动图附件
	messageHelper.addAttachment("happy.docx",new File("xx/xx/happy.docx"));//添加文本文件附件
	mailSender.send(mimeMessage);
}
```

### 1.2.3、JavaMailSenderImpl的开箱即用
>熟悉Springboot开发的人都知道，要在控制类中使用相应类中的方法，需要在Service类中声明，后在ServiceImpl类中实现后才能，而这里我们没有去实现它就直接可以注入到业务类中直接使用了，而这就是开箱即用。那么我们来看看它源码是如何实现的？

```java
@Configuration
@ConditionalOnProperty(prefix="spring.mail",name="host")
// 邮件自动配置类
/**
*邮件自动配置类为上下文提供了邮件服务实例（JavaMailSenderImpl）
*/
class MailSenderPropertiesConfiguration{
	private final MailProperties properties;
	MailPropertiesConfiguration(MailProperties properties){
		this.properties = properties ;
	}

	@Bean
	@ConditionalOnMissingBean
	public JavaMailSenderImpl mailSender(){
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		applyProperties(sender);
		return sender;
	}
}
```

**MailProperties（邮件服务器的配置信息）**

```java
@ConfigurationProperties(prefix="spring.mail")
public class MailProperties{
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	private String host;
	private Integer port;
	private String username;
	private String password;
	private String protocol = "smtp";
	private Charset defaultEndcoding = DEFAULT_CHARSET;
	private Map<String,String> properties = new HashMap<>();
}
```

# 二、开发文档
## 2.1、邮箱设置
>由于要实现发送邮件的功能需要SMTP、IMAP、POP3协议，所以我们需要在我们的邮箱中开启该服务（发送者），同时第三方登录邮箱需要进行授权，获取授权码才能进行授权登录，方可进行进行发送邮件操作。

1、进入QQ邮箱，点击设置（这里进行开发，鼓励大家尝试别的邮箱进行开发）
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020070419350621.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)2、进入设置，点击账户，找到POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704193954984.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)3、开启POP3/SMTP和IMAP/SMTP服务，并点击保存更改
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704194259248.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)4、使用QQ邮箱客户端扫描二维码获取授权码，并保存
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704194538702.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)
## 2.2、环境配置
**开发工具**
>IDEA 2019\ Maven

1、创建工程
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020070419540897.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)2、填写项目名称、信息
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704195834825.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)3、添加依赖
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704200146580.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)4、进入项目，创建如下目录与文件
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704201351642.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)
## 2.3、配置pom.xml与application.yml
**(1)、配置pom.xml**
1、pom.xml文件内容
这里只给出此处实现的部分所需要的依赖，如果需要扩展开发，自行添加依赖
```java
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>webjars-locator-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>3.3.1</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>bootstrap</artifactId>
            <version>3.3.7</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.6</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

```
2、更新pom.xml文件中的依赖
修改pom.xml文件后需要更新其中的依赖，不然添加的依赖没有作用
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704202336408.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)**（2）、配置application.yml**
由于实现发送邮件需要一个发送方的邮箱，密码（或者授权码），还有使用springbootMVC 开发需要指定静态文件（前端文件）所在路径

```java
spring:
  mail:
    host:smtp.qq.com #smtp服务器地址
    username:admin #登录账号
    password:12345 #登录密码（或者授权码）
    properties:
      from:admin@qq.com #邮件发送人
     
    # 让后端与前端进行数据交互的依赖包
  thymeleaf:
    cache:false
    prefix:classpath:/static #根据各自resources文件夹下存放前端文件的文件夹名进行更改，这里我的文件夹名是static，注意“/”
  
  servlet:
  	multipart:
  	  max-file-size:10MB # 限制上传文件的大小
  	  max-request-size:50MB # 限制请求总量
```
## 2.4、邮件信息类

>知识梳理中，我们提到发送邮件需要先构建SimpleMailMessage 或者MimeMessage邮件信息类来填写邮件标题、邮件内容等信息，然后将该信息类交给JavaMailSenderImpl发送邮件，而且我们在发送邮件的时候都会用到 ```mailSerice.send(message)```那么我们便可以构建一个邮件信息类来承接前端传递过来的邮件信息。所以我们需要先构建一个信息类，如果需要将邮件信息存储在服务器数据库中，那么可以使用该类进行数据传输。

```java
package springmailsender.demo.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

@Data // 该注解可实现get/set方法
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
```
## 2.5、发送邮件（含有附件）
**功能分析：**
>发送邮件功能，分析一下不难发现，我们在该步骤需要处理大概这个三个问题：
>1、检测邮件 checkMail()： 检验邮件收信人、邮件主题、邮件内容等这些必填项，如果内容为空则拒绝发送。
>2、发送邮件 sendMimMail(): 通过MimeMessageHelper 来解析MailDto，并构建MimeMessage传输邮件。
>3、保存邮件 saveMail() :将邮件保存到数据库，方便处理邮件问题以及统计邮件

**代码实现：**

```java
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
```

## 2.6、控制器MailController
>搞定后端业务逻辑，那么咱们编写前端页面来验证后端业务逻辑是否能够实现？
>在开搭建前端之前，我们先来编写前后端交互的控制器MailController类

```java
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
```
## 2.7、前端搭建
在开始编写前端代码之前，建议没有学过或者用过thymeaf的小伙伴，先学习一下，这里使用了thymeaf，同时它也是一个很重要的springboot前端开发的依赖包，可以大大提高前端数据渲染。
直接上代码吧！！！

```css
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh">
<head>
    <meta charset="UTF-8">
    <title>发送邮件</title>
    <!--- 使用bootstrap框架来搭建前端页面 --->
    <link th:href="@{/webjars/bootstrap/css/bootstrap.min.css}" rel="stylesheet" type="text/css"/>
      <!--- 使用Jquery框架处理前端页面事件 --->
    <script th:src="@{/webjars/jquery/jquery.min.js}"></script>
    <script th:href="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
</head>
<body>
	<div class="col-md-6" style="margin:20px;padding:20px;border: #E0E0E0 1px solid;">
	    <marquee behavior="alternate" onfinish="alert(12)" id="mq" onMouseOut="this.start();$('#egg').text('来抓我啊，啦啦啦...');"
	             onMouseOver="this.stop();$('#egg').text('还是被你抓住了，呜呜呜...');"><h5 id="egg">祝⼤家新年快乐！</h5><img id="doge"
	                                                                                                  src="http://pics.sc.chinaz.com/Files/pic/faces/3709/7.gif"
	                                                                                                  alt=""></marquee>
	    <form class="form-horizontal" id="mailForm">
	        <div class="form-group"><label class="col-md-2 control-label">邮件发信⼈:</label>
	            <div class="col-md-6"><input class="form-control" id="from" name="from" th:value="${from}"
	                                         readonly="readonly"></div>
	        </div>
	        <div class="form-group"><label class="col-md-2 control-label">邮件收信⼈:</label>
	            <label class="col-md-2 control-label">邮件收信⼈:</label>
	            <div class="col-md-6"><input class="form-control" id="to" name="to" title="多个邮箱使⽤,隔开"></div>
	        </div>
	        <div class="form-group"><label class="col-md-2 control-label">邮件主题:</label>
	            <div class="col-md-6"><input class="form-control" id="subject" name="subject"></div>
	        </div>
	        <div class="form-group"><label class="col-md-2 control-label">邮件内容:</label>
	            <div class="col-md-6"><textarea class="form-control" id="text" name="text" rows="5"></textarea></div>
	        </div>
	        <div class="form-group"><label class="col-md-2 control-label">邮件附件:</label>
	            <div class="col-md-6"><input class="form-control" id="files" name="files" type="file" multiple="multiple">
	            </div>
	        </div>
	        <div class="form-group"><label class="col-md-2 control-label">邮件操作:</label>
	            <div class="col-md-3"><a class="form-control btn btn-primary" onclick="sendMail()">发送邮件</a></div>
	            <div class="col-md-3"><a class="form-control btn btn-default" onclick="clearForm()">清空</a></div>
	        </div>
	    </form>
	    <script th:inline="javascript"> var appCtx = [[${#request.getContextPath()}]];
	
	    function sendMail() {
	        var formData = new FormData($('#mailForm')[0]);
	        $.ajax({
	            url: appCtx + '/mail/send',
	            type: "POST",
	            data: formData,
	            contentType: false,
	            processData: false,
	            success: function (result) {
	                alert(result.status === 'ok' ? "发送成功！" : "发送失败，失败原因：" + result.error);
	            },
	            error: function () {
	                alert("发送失败！");
	            }
	        });
	    }
	
	    function clearForm() {
	        $('#mailForm')[0].reset();
	    }
	    setInterval(function () {
	        var total = $('#mq').width();
	        var width = $('#doge').width();
	        var left = $('#doge').offset().left;
	        if (left <= width / 2 + 20) {
	            $('#doge').css('transform', 'rotateY(180deg)')
	            $('#doge').css('transform', 'rotateY(180deg)')
	        }
	        if (left >= total - width / 2 - 40) {
	            $('#doge').css('transform', 'rotateY(-360deg)')
	        }
	    }); </script>
	</div>
</body>
</html>
```
## 2.8、测试项目
1、启动Springboot项目
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704213209211.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)
2、查看启动
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704213252219.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)3、启动前端
在浏览器中输入【localhost:8080/】回车便可进入前端页面
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704213422352.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704213655983.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)
4、发送邮件
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704213938958.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)5、验证发送是否成功
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704214105647.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20200704214241320.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzQ1MjQyNA==,size_16,color_FFFFFF,t_70)

【总结】
  解决了springboot实现邮件发送功能，写的不多，希望对大家有帮助，如果觉得可以，请点赞鼓励，让我继续创造下去。
  [本人CSND链接](https://editor.csdn.net/md/?articleId=107120844)
