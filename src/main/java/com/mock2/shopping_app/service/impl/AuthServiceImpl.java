package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.ResourceAlreadyInUseException;
import com.mock2.shopping_app.exception.TokenRefreshException;
import com.mock2.shopping_app.model.request.LoginRequest;
import com.mock2.shopping_app.model.request.RefreshTokenRequest;
import com.mock2.shopping_app.model.request.RegistrationRequest;
import com.mock2.shopping_app.model.entity.RefreshToken;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.security.CustomUserDetails;
import com.mock2.shopping_app.security.JwtTokenProvider;
import com.mock2.shopping_app.service.AuthService;
import com.mock2.shopping_app.service.RefreshTokenService;
import com.mock2.shopping_app.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final Logger logger = Logger.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                           UserService userService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    public Optional<Authentication> authenticateUser(LoginRequest loginRequest) {
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
        User currentUser = customUserDetails.getUser();
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(currentUser.getId());
        return Optional.ofNullable(refreshTokenService.saveRefreshToken(refreshToken));
    }

    public Optional<String> refreshJwtToken(RefreshTokenRequest refreshTokenRequest) {
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
        refreshTokenService.deleteByUserId(userId);
    }
}
