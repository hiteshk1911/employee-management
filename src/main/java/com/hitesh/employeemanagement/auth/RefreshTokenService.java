package com.hitesh.employeemanagement.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createToken(String username) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .username(username)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid refresh token"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteAllUserTokens(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}