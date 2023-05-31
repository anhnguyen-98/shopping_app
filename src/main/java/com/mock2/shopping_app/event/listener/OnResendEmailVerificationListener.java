package com.mock2.shopping_app.event.listener;

import com.mock2.shopping_app.event.OnResendEmailVerificationEvent;
import com.mock2.shopping_app.exception.MailSendException;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.token.EmailVerificationToken;
import com.mock2.shopping_app.service.MailService;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OnResendEmailVerificationListener implements ApplicationListener<OnResendEmailVerificationEvent> {
    private final Logger logger = Logger.getLogger(OnResendEmailVerificationListener.class);
    private final MailService mailService;

    public OnResendEmailVerificationListener(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    @Async
    public void onApplicationEvent(OnResendEmailVerificationEvent onResendEmailVerificationEvent) {
        resendEmailVerification(onResendEmailVerificationEvent);
    }

    private void resendEmailVerification(OnResendEmailVerificationEvent event) {
        User user = event.getUser();
        EmailVerificationToken token = event.getToken();

        String emailConfirmationUrl = event.getRedirectUrl().queryParam("token", token.getToken()).toUriString();
        String recipientEmail = user.getEmail();
        try {
            mailService.sendEmailVerification(emailConfirmationUrl, recipientEmail);
        } catch (IOException | TemplateException | MessagingException e) {
            logger.error(e);
            throw new MailSendException(recipientEmail, "Email Verification");
        }
    }
}
