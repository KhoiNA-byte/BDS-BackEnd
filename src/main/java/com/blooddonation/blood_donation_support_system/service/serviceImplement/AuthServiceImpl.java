package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.TempUserDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import com.blooddonation.blood_donation_support_system.service.AuthService;
import com.blooddonation.blood_donation_support_system.service.EmailService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import com.blooddonation.blood_donation_support_system.validator.UserValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserValidator validator;

    private final Map<String, TempUserDto> temporaryUsers = new HashMap<>();
    private final Map<String, LocalDateTime> codeExpiration = new HashMap<>();

    public String registerUser(AccountDto accountDto, String name) {
        if (accountRepository.findByEmail(accountDto.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        // Encode the password
        accountDto.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        Account account = AccountMapper.toEntity(accountDto);

        removeOldCode(accountDto.getEmail());

        TempUserDto tempUser = TempUserDto.builder()
                .account(account)
                .name(name)
                .build();

        // Generate a verification code and store it temporarily
        String verificationCode = generateVerificationCode();
        temporaryUsers.put(verificationCode, tempUser);
        codeExpiration.put(verificationCode, LocalDateTime.now().plusMinutes(10));

        // Send verification email
        sendVerificationEmail(account, verificationCode);
        return "verification email sent";
    }

    public String resendVerificationCode(String email) {
        TempUserDto tempUser = temporaryUsers.values().stream()
                .filter(u -> u.getAccount().getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if (tempUser == null) {
            return "No temporary registration found for this email";
        }

        // Remove old verification code
        removeOldCode(email);

        String newVerificationCode = generateVerificationCode();
        temporaryUsers.put(newVerificationCode, tempUser);
        codeExpiration.put(newVerificationCode, LocalDateTime.now().plusMinutes(10));

        sendVerificationEmail(tempUser.getAccount(), newVerificationCode);
        return "New verification email sent";
    }

    @Transactional
    public String verifyUser(String code) {
        if (!codeExpiration.containsKey(code)) {
            return "Verification code invalid";
        }
        if (LocalDateTime.now().isAfter(codeExpiration.get(code))) {
            return "Verification code expired";
        }

        TempUserDto tempUser = temporaryUsers.get(code);
        if (tempUser != null) {
            // Create and set profile
            Profile profile = new Profile();
            profile.setName(tempUser.getName());
            Account account = tempUser.getAccount();
            account.setProfile(profile);

            // Save account to get generated ID
            Account savedAccount = accountRepository.save(account);

            // Set the account ID in the profile and save it
            Profile updatedProfile = savedAccount.getProfile();
            updatedProfile.setAccountId(savedAccount.getId());
            profileRepository.save(updatedProfile);

            temporaryUsers.remove(code);
            codeExpiration.remove(code);
            return "User registered successfully";
        }
        return "Invalid verification code";
    }

    @Override
    public AccountDto login(AccountDto accountDto) {
        Account account = validator.getEmailOrThrow(accountDto.getEmail());
        validator.validateLogin(account, accountDto);
        return AccountMapper.toDto(account);
    }

    public String initiatePasswordReset(String email) {
        Account account = validator.getEmailOrThrow(email);

        removeOldCode(email);

        String resetCode = generateVerificationCode();
        codeExpiration.put(resetCode, LocalDateTime.now().plusMinutes(10));

        // Create TempUserDto for password reset
        TempUserDto tempUser = TempUserDto.builder()
                .account(account)
                .name(account.getProfile().getName())
                .build();

        temporaryUsers.put(resetCode, tempUser);
        sendResetPassword(account, resetCode);
        return "New password reset code sent";
    }

    public String verifyPasswordReset(String resetCode) {
        if (!codeExpiration.containsKey(resetCode)) {
            return "Reset code invalid";
        }
        if (LocalDateTime.now().isAfter(codeExpiration.get(resetCode))) {
            return "Reset code expired";
        }
        TempUserDto tempUser = temporaryUsers.get(resetCode);
        if (tempUser == null) {
            return "Invalid reset code";
        }
        return "Verify password reset code successfully";
    }

    public String resetPassword(String resetCode ,String newPassword) {
        TempUserDto tempUser = temporaryUsers.get(resetCode);
        Account account = tempUser.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        // Clean up temporary data
        temporaryUsers.remove(resetCode);
        codeExpiration.remove(resetCode);

        return "Password reset successfully";
    }

    private String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    private void removeOldCode(String email) {
        String oldCode = temporaryUsers.entrySet().stream()
                .filter(entry -> entry.getValue().getAccount().getEmail().equals(email))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (oldCode != null) {
            temporaryUsers.remove(oldCode);
            codeExpiration.remove(oldCode);
        }
    }

    private void sendVerificationEmail(Account account, String verificationCode) {
        String subject = "Account Verification";
        String htmlMessage = "<html>"
                + "<body>"
                + "<h2>Welcome to Blood Donation Support System!</h2>"
                + "<p>Please use the following code to verify your account:</p>"
                + "<h3>" + verificationCode + "</h3>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(account.getEmail(), subject, htmlMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResetPassword(Account account, String resetCode) {
        String subject = "Password Reset Request";
        String htmlMessage = "<html>"
                + "<body>"
                + "<h2>Password Reset Request</h2>"
                + "<p>Your password reset code is:</p>"
                + "<h3>" + resetCode + "</h3>"
                + "<p>This code will expire in 10 minutes.</p>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(account.getEmail(), subject, htmlMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 600000)
    public void cleanupExpiredRegistrations() {
        LocalDateTime now = LocalDateTime.now();
        codeExpiration.entrySet().removeIf(entry -> {
            if (now.isAfter(entry.getValue())) {
                temporaryUsers.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}
