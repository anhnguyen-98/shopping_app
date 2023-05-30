package com.mock2.shopping_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_CLAIM = "authorities";
    private final String issuer;
    private final Long expiredTimeInMs;
    private final String secretKey;

    public JwtTokenProvider(@Value("${app.jwt.issuer}") String issuer,
                            @Value("${app.jwt.accessTokenExpiration}") Long expiredTimeInMs,
                            @Value("${app.jwt.secret}") String secretKey) {
        this.issuer = issuer;
        this.expiredTimeInMs = expiredTimeInMs;
        this.secretKey = secretKey;
    }

    public String generateToken(CustomUserDetails customUserDetails) {
        Instant expiryDate = Instant.now().plusMillis(expiredTimeInMs);
        String authorities = getAuthorities(customUserDetails);
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
//                .setSubject(Long.toString(customUserDetails.getUser().getId()))
                .setIssuer(issuer)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiryDate))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .claim(AUTHORITIES_CLAIM, authorities)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<GrantedAuthority> getAuthoritiesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return Arrays.stream(claims.get(AUTHORITIES_CLAIM).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private String getAuthorities(CustomUserDetails customUserDetails) {
        return customUserDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public Long getExpiredTimeInMs() {
        return this.expiredTimeInMs;
    }
}
