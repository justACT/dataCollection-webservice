package com.dataCollection.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * Created by xiangrchen on 8/17/17.
 */
@Service
public class EmailUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String from;

    public void sendEmail(List<String> emailList, String subject, String content) throws MessagingException {
        final MimeMessage mimeMailMessage=this.mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMailMessage);
        messageHelper.setFrom(from);
        messageHelper.setTo(emailList.toArray(new String[emailList.size()]));
        messageHelper.setSubject(subject);
        messageHelper.setText(content);
        this.mailSender.send(mimeMailMessage);
    }
}
