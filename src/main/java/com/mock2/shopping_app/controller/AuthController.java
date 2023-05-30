package com.mock2.shopping_app.controller;

import com.mock2.shopping_app.exception.TokenRefreshException;
import com.mock2.shopping_app.exception.UserLoginException;
import com.mock2.shopping_app.exception.UserRegistrationException;
import com.mock2.shopping_app.model.request.LoginRequest;
import com.mock2.shopping_app.model.request.RefreshTokenRequest;
import com.mock2.shopping_app.model.request.RegistrationRequest;
import com.mock2.shopping_app.model.entity.RefreshToken;
import com.mock2.shopping_app.model.response.ApiResponse;
import com.mock2.shopping_app.model.response.JwtResponse;
import com.mock2.shopping_app.security.CustomUserDetails;
import com.mock2.shopping_app.security.JwtTokenProvider;
import com.mock2.shopping_app.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.path}")
public class AuthController {
    private final Logger logger = Logger.getLogger(AuthController.class);
    private final AuthServiceImpl authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthServiceImpl authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authService.authenticateUser(loginRequest)
                .orElseThrow(() -> new UserLoginException("Couldn't login user " + loginRequest.getEmail() +
                        ". Check email and password again"));
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        logger.info("Logged in User returned [API]: " + customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authService.createAndPersistRefreshToken(customUserDetails)
                .map(RefreshToken::getToken)
                .map(refreshToken -> {
                    String accessToken = authService.generateJwtToken(customUserDetails);
                    return ResponseEntity.ok(JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .expiryDurationInMs(jwtTokenProvider.getExpiredTimeInMs())
                            .build());
                }).orElseThrow(() -> new UserLoginException("Couldn't create refresh token for: [" + loginRequest.getEmail() + "]"));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        authService.registerUser(registrationRequest)
                .orElseThrow(() -> new UserRegistrationException(
                        registrationRequest.getEmail(), "Missing user in database"));
        return ResponseEntity.ok(new ApiResponse(true,
                "User registered successfully. Check your email for verification"));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<JwtResponse> refreshJwtToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshJwtToken(refreshTokenRequest)
                .map(newAccessToken -> {
                    logger.info("Created new JWT access token: " + newAccessToken);
                    return ResponseEntity.ok(JwtResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(refreshTokenRequest.getRefreshToken())
                            .expiryDurationInMs(jwtTokenProvider.getExpiredTimeInMs())
                            .build());
                }).orElseThrow(() -> new TokenRefreshException(refreshTokenRequest.getRefreshToken(),
                        "Unexpected error during token refresh. Please logout and login again"));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse> logout(@AuthenticationPrincipal CustomUserDetails currentUser) {
        Long currentUserId = currentUser.getUser().getId();
        authService.deleteRefreshTokenByUserId(currentUserId);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new ApiResponse(true, "Successfully log out"));
    }
}
