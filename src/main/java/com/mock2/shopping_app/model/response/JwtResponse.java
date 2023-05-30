package com.mock2.shopping_app.model.response;

import lombok.*;

@Getter
@Setter
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private final String tokenType = "Bearer ";
    private Long expiryDurationInMs;
}
