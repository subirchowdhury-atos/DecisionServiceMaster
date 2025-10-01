package com.decisionservicemaster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Objects;

@Service
public class ApplicationEmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${app.mail.default-from:from@example.com}")
    private String defaultFromAddress;
    
    /**
     * Send simple text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(defaultFromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        mailSender.send(message);
    }
    
    /**
     * Send HTML email with template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setFrom(defaultFromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        
        // Process the template
        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);
        
        mailSender.send(mimeMessage);
    }
    
    /**
     * Send HTML email with custom from address
     */
    public void sendHtmlEmail(String from, String to, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setFrom(Objects.requireNonNullElse(from, defaultFromAddress));
        helper.setTo(to);
        helper.setSubject(subject);
        
        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);
        
        mailSender.send(mimeMessage);
    }
    
    /**
     * Get default from address
     */
    public String getDefaultFromAddress() {
        return defaultFromAddress;
    }
}