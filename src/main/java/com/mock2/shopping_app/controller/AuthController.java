package com.mock2.shopping_app.controller;

import com.mock2.shopping_app.event.OnResendEmailVerificationEvent;
import com.mock2.shopping_app.event.OnUserRegistrationCompleteEvent;
import com.mock2.shopping_app.exception.InvalidTokenRequestException;
import com.mock2.shopping_app.exception.TokenRefreshException;
import com.mock2.shopping_app.exception.UserLoginException;
import com.mock2.shopping_app.exception.UserRegistrationException;
import com.mock2.shopping_app.model.entity.RefreshToken;
import com.mock2.shopping_app.model.request.LoginRequest;
import com.mock2.shopping_app.model.request.RefreshTokenRequest;
import com.mock2.shopping_app.model.request.RegistrationRequest;
import com.mock2.shopping_app.model.response.ApiResponse;
import com.mock2.shopping_app.model.response.JwtResponse;
import com.mock2.shopping_app.model.token.EmailVerificationToken;
import com.mock2.shopping_app.security.CustomUserDetails;
import com.mock2.shopping_app.security.JwtTokenProvider;
import com.mock2.shopping_app.service.AuthService;
import jakarta.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("${app.api.path}")
public class AuthController {
    private final Logger logger = Logger.getLogger(AuthController.class);
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${app.api.path}")
    private String apiPath;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider, ApplicationEventPublisher applicationEventPublisher) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.applicationEventPublisher = applicationEventPublisher;
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
    @Transactional
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return authService.registerUser(registrationRequest)
                .map(user -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder
                            .fromCurrentContextPath().path(apiPath + "/auth/registrationConfirmation");
                    OnUserRegistrationCompleteEvent onUserRegistrationCompleteEvent
                            = new OnUserRegistrationCompleteEvent(urlBuilder, user);
                    applicationEventPublisher.publishEvent(onUserRegistrationCompleteEvent);
                    return ResponseEntity.ok(new ApiResponse(true,
                            "User registered successfully. Check your email for verification"));
                })
                .orElseThrow(() -> new UserRegistrationException(
                        registrationRequest.getEmail(), "Missing user in database"));
    }

    @GetMapping("/auth/registrationConfirmation")
    public ResponseEntity<ApiResponse> confirmRegistration(@RequestParam String token) {
        return authService.confirmEmailRegistration(token)
                .map(user -> ResponseEntity.ok(new ApiResponse(true, "User verified successfully")))
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", token,
                        "Failed to confirm. Please generate a new email verification request"));
    }

    @GetMapping("/auth/resendRegistrationConfirmation")
    public ResponseEntity<ApiResponse> resendRegistrationEmail(@RequestParam String existingToken) {
        EmailVerificationToken revalidatedToken = authService.revalidateEmailVerificationToken(existingToken)
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken,
                        "User is already registered. No need to resend verification email"));

        return Optional.ofNullable(revalidatedToken.getUser())
                .map(user -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder
                            .fromCurrentContextPath().path(apiPath + "/auth/registrationConfirmation");
                    OnResendEmailVerificationEvent onResendEmailVerificationEvent
                            = new OnResendEmailVerificationEvent(urlBuilder, user, revalidatedToken);
                    applicationEventPublisher.publishEvent(onResendEmailVerificationEvent);
                    return ResponseEntity.ok(new ApiResponse(true, "Email verification resent successfully"));
                })
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken,
                        "No user associated with this request. Re-verification denied"));

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
