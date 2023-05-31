package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.exception.TokenRefreshException;
import com.mock2.shopping_app.model.entity.RefreshToken;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.repository.RefreshTokenRepository;
import com.mock2.shopping_app.repository.UserRepository;
import com.mock2.shopping_app.service.RefreshTokenService;
import com.mock2.shopping_app.util.Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final Logger logger = Logger.getLogger(RefreshTokenServiceImpl.class);
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${app.jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshToken generateRefreshToken(Long userId) {
        logger.info("Generating refresh token");
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found with id: " + userId));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(Util.generateRandomUUID());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setUser(user);
        refreshToken.setRefreshCount(0L);
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        logger.info("Find refresh token by token");
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public void verifyExpiration(RefreshToken token) {
        logger.info("Verifying expiration of refresh token");
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            logger.error("Refresh token is expired");
            throw new TokenRefreshException(token.getToken(), "Expired token. Please issue a new request");
        }
    }

    @Override
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        logger.info("Save refresh token into database");
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void deleteByTokenId(Long id) {
        logger.info("Delete Refresh Token by token id");
        refreshTokenRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        logger.info("Delete Refresh Token by user id");
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    public void increaseRefreshCount(RefreshToken refreshToken) {
        refreshToken.incrementRefreshCount();
        saveRefreshToken(refreshToken);
    }
}
