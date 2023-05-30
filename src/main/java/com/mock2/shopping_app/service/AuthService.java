package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.request.LoginRequest;
import com.mock2.shopping_app.model.request.RefreshTokenRequest;
import com.mock2.shopping_app.model.request.RegistrationRequest;
import com.mock2.shopping_app.model.entity.RefreshToken;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.security.CustomUserDetails;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface AuthService {
    Optional<Authentication> authenticateUser(LoginRequest loginRequest);

    Optional<User> registerUser(RegistrationRequest registrationRequest);

    String generateJwtToken(CustomUserDetails customUserDetails);

    Optional<RefreshToken> createAndPersistRefreshToken(CustomUserDetails customUserDetails);

    Optional<String> refreshJwtToken(RefreshTokenRequest refreshTokenRequest);

    void deleteRefreshTokenByUserId(Long userId);
}
