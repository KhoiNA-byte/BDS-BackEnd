package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;

public interface AuthService {
    String registerUser(AccountDto accountDto, String name);

    String resendVerificationCode(String email);

    String verifyUser(String code);

    AccountDto login(AccountDto accountDto);

    String initiatePasswordReset(String email);

    String verifyPasswordReset(String resetCode);

    String resetPassword(String resetCode, String newPassword);

    void cleanupExpiredRegistrations();
}
