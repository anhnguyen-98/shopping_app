package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.exception.ResourceAlreadyInUseException;
import com.mock2.shopping_app.exception.ResourceNotFoundException;
import com.mock2.shopping_app.exception.TokenRefreshException;
import com.mock2.shopping_app.model.request.LoginRequest;
import com.mock2.shopping_app.model.request.RefreshTokenRequest;
import com.mock2.shopping_app.model.request.RegistrationRequest;
import com.mock2.shopping_app.model.entity.RefreshToken;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.token.EmailVerificationToken;
import com.mock2.shopping_app.security.CustomUserDetails;
import com.mock2.shopping_app.security.JwtTokenProvider;
import com.mock2.shopping_app.service.AuthService;
import com.mock2.shopping_app.service.EmailVerificationTokenService;
import com.mock2.shopping_app.service.RefreshTokenService;
import com.mock2.shopping_app.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final Logger logger = Logger.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationTokenService emailVerificationTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                           UserService userService, RefreshTokenService refreshTokenService, EmailVerificationTokenService emailVerificationTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationTokenService = emailVerificationTokenService;
    }

    public Optional<Authentication> authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticate user");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (BadCredentialsException ex) {
            logger.error(ex.getMessage());
        }
        return Optional.ofNullable(authentication);
    }

    @Transactional
    public Optional<User> registerUser(RegistrationRequest registrationRequest) {
        String newRegistrationRequestEmail = registrationRequest.getEmail();
        if (userService.existsByEmail(newRegistrationRequestEmail)) {
            logger.error("Email already exists: " + newRegistrationRequestEmail);
            throw new ResourceAlreadyInUseException("Email", "Address", newRegistrationRequestEmail);
        }
        logger.info("Trying to register new user [" + newRegistrationRequestEmail + "]");
        User newUser = userService.createUser(registrationRequest);
        User registeredNewUser = userService.saveUser(newUser);
        return Optional.ofNullable(registeredNewUser);
    }

    public String generateJwtToken(CustomUserDetails customUserDetails) {
        return jwtTokenProvider.generateToken(customUserDetails);
    }

    public Optional<RefreshToken> createAndPersistRefreshToken(CustomUserDetails customUserDetails) {
        logger.info("Trying to create and persist refresh token");
        User currentUser = customUserDetails.getUser();
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(currentUser.getId());
        return Optional.ofNullable(refreshTokenService.saveRefreshToken(refreshToken));
    }

    public Optional<String> refreshJwtToken(RefreshTokenRequest refreshTokenRequest) {
        logger.info("Refresh jwt token to get new access token");
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();
        return Optional.of(refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    refreshTokenService.verifyExpiration(refreshToken);
                    refreshTokenService.increaseRefreshCount(refreshToken);
                    return refreshToken;
                })
                .map(RefreshToken::getUser)
                .map(CustomUserDetails::new)
                .map(this::generateJwtToken))
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Couldn't find refresh token in database. Please login again"));
    }

    public void deleteRefreshTokenByUserId(Long userId) {
        logger.info("Delete refresh token by user id");
        refreshTokenService.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public Optional<User> confirmEmailRegistration(String verificationToken) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenService.findByToken(verificationToken)
                .orElseThrow(() -> new ResourceNotFoundException("Verification Token", "Email verification", verificationToken));
        User registeredUser = emailVerificationToken.getUser();

        emailVerificationTokenService.verifyExpiration(emailVerificationToken);
        emailVerificationToken.setConfirmedStatus();
        emailVerificationTokenService.saveEmailVerificationToken(emailVerificationToken);
        registeredUser.verificationConfirmed();
        return Optional.ofNullable(userService.saveUser(registeredUser));
    }

    @Override
    public Optional<EmailVerificationToken> revalidateEmailVerificationToken(String existingToken) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenService.findByToken(existingToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "Existing email verification", existingToken));

        if (emailVerificationToken.getUser().getEmailVerified()) {
            return Optional.empty();
        }
        return Optional.ofNullable(emailVerificationTokenService.updateExistingTokenWithNewExpiryDate(emailVerificationToken));
    }
}
