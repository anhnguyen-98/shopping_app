package com.mock2.shopping_app.security;

import com.mock2.shopping_app.exception.InvalidTokenRequestException;
import io.jsonwebtoken.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenValidator {
    private static final Logger logger = Logger.getLogger(JwtTokenValidator.class);
    @Value("${app.jwt.secret}")
    private String secretKey;

    public boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            throw new InvalidTokenRequestException("JWT", jwtToken, "Incorrect signature");

        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            throw new InvalidTokenRequestException("JWT", jwtToken, "Malformed jwt token");

        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            throw new InvalidTokenRequestException("JWT", jwtToken, "Token expired. Refresh required");

        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            throw new InvalidTokenRequestException("JWT", jwtToken, "Unsupported JWT token");

        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
            throw new InvalidTokenRequestException("JWT", jwtToken, "Illegal argument token");
        }
    }
}
