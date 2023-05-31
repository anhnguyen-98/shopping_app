package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.other.Mail;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface MailService {
    void sendEmailVerification(String emailVerificationUrl, String to) throws IOException, TemplateException, MessagingException;
    void sendMail(Mail mail) throws MessagingException;
}
