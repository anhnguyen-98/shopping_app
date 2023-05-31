package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.InvalidTokenRequestException;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.enums.TokenStatus;
import com.mock2.shopping_app.model.token.EmailVerificationToken;
import com.mock2.shopping_app.repository.EmailVerificationTokenRepository;
import com.mock2.shopping_app.service.EmailVerificationTokenService;
import com.mock2.shopping_app.util.Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {
    private final Logger logger = Logger.getLogger(EmailVerificationTokenServiceImpl.class);
    @Value("${app.token.email.verification.duration}")
    private Long emailVerificationTokenExpiryDuration;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    public EmailVerificationTokenServiceImpl(EmailVerificationTokenRepository emailVerificationTokenRepository) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
    }

    @Override
    public String generateNewToken() {
        return Util.generateRandomUUID();
    }

    @Override
    public void createVerificationToken(User user, String token) {
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setTokenStatus(TokenStatus.STATUS_PENDING);
        emailVerificationToken.setExpiryDate(Instant.now().plusMillis(emailVerificationTokenExpiryDuration));
        logger.info("Generated Email verification token = " + emailVerificationToken);
        emailVerificationTokenRepository.save(emailVerificationToken);
    }

    @Override
    public EmailVerificationToken saveEmailVerificationToken(EmailVerificationToken token) {
        return emailVerificationTokenRepository.save(token);
    }

    @Override
    public void verifyExpiration(EmailVerificationToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new InvalidTokenRequestException("Email Verification Token", token.getToken(),
                    "Expired token. Please issue a new request");
        }
    }

    @Override
    public Optional<EmailVerificationToken> findByToken(String token) {
        return emailVerificationTokenRepository.findByToken(token);
    }

    @Override
    public EmailVerificationToken updateExistingTokenWithNewExpiryDate(EmailVerificationToken existingToken) {
        existingToken.setTokenStatus(TokenStatus.STATUS_PENDING);
        existingToken.setExpiryDate(Instant.now().plusMillis(emailVerificationTokenExpiryDuration));
        logger.info("Updated Email verification token: " + existingToken);
        return saveEmailVerificationToken(existingToken);
    }
}
