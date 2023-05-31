package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.token.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenService {

    String generateNewToken();

    void createVerificationToken(User user, String token);

    EmailVerificationToken saveEmailVerificationToken(EmailVerificationToken token);

    void verifyExpiration(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByToken(String token);

    EmailVerificationToken updateExistingTokenWithNewExpiryDate(EmailVerificationToken existingToken);
}
