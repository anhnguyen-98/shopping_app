package com.mock2.shopping_app.event.listener;

import com.mock2.shopping_app.event.OnUserRegistrationCompleteEvent;
import com.mock2.shopping_app.exception.MailSendException;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.service.EmailVerificationTokenService;
import com.mock2.shopping_app.service.MailService;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OnUserRegistrationCompleteListener implements ApplicationListener<OnUserRegistrationCompleteEvent> {
    private static final Logger logger = Logger.getLogger(OnUserRegistrationCompleteListener.class);
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final MailService mailService;

    public OnUserRegistrationCompleteListener(EmailVerificationTokenService emailVerificationTokenService, MailService mailService) {
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.mailService = mailService;
    }

    @Override
    @Async
    public void onApplicationEvent(OnUserRegistrationCompleteEvent event) {
        this.sendEmailVerification(event);
    }

    private void sendEmailVerification(OnUserRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = emailVerificationTokenService.generateNewToken();
        emailVerificationTokenService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String emailConfirmationUrl = event.getRedirectUrl().queryParam("token", token).toUriString();

        try {
            mailService.sendEmailVerification(emailConfirmationUrl, recipientAddress);
        } catch (IOException | TemplateException | MessagingException e) {
            logger.error(e);
            throw new MailSendException(recipientAddress, "Email Verification");
        }
    }
}
