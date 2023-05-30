package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.entity.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(Long userId);

    Optional<RefreshToken> findByToken(String token);

    void verifyExpiration(RefreshToken token);

    RefreshToken saveRefreshToken(RefreshToken refreshToken);

    void deleteByTokenId(Long id);

    void deleteByUserId(Long userId);

    void increaseRefreshCount(RefreshToken refreshToken);
}
