package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.exception.TokenRefreshException;
import com.mock2.shopping_app.model.entity.RefreshToken;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.repository.RefreshTokenRepository;
import com.mock2.shopping_app.repository.UserRepository;
import com.mock2.shopping_app.service.RefreshTokenService;
import com.mock2.shopping_app.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
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
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id = " + userId + " doesn't exist"));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(Util.generateRandomUUID());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setUser(user);
        refreshToken.setRefreshCount(0L);
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(token.getToken(), "Expired token. Please issue a new request");
        }
    }

    @Override
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void deleteByTokenId(Long id) {
        refreshTokenRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    public void increaseRefreshCount(RefreshToken refreshToken) {
        refreshToken.incrementRefreshCount();
        saveRefreshToken(refreshToken);
    }
}
