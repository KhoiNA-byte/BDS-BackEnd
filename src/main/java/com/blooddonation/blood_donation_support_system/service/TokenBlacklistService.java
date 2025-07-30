package com.blooddonation.blood_donation_support_system.service;

public interface TokenBlacklistService {
    void blacklistToken(String token);

    boolean isTokenBlacklisted(String token);

    void clearExpiredTokens();
}