package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ResetPasswordDto;
import com.blooddonation.blood_donation_support_system.service.AuthService;
import com.blooddonation.blood_donation_support_system.service.TokenBlacklistService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AccountDto accountDto, HttpServletResponse response) {
        try {
            AccountDto loggedInAccount = authService.login(accountDto);

            String jwtToken = jwtUtil.generateToken(loggedInAccount.getEmail());
            Cookie cookie = new Cookie("jwt-token", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // HTTPS only — use false for localhost HTTP dev
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setDomain("localhost"); // Optional, but helps in some setups
            response.addCookie(cookie);

            return ResponseEntity.ok(loggedInAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while log in user information");
        }
    }

    // Logout
    @GetMapping("/logout")
    public void logout(@CookieValue("jwt-token") String token, HttpServletResponse response) throws IOException, java.io.IOException {
        tokenBlacklistService.blacklistToken(token);
        Cookie cookie = new Cookie("jwt-token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody AccountDto accountDto,
                                               @RequestParam String name) {
        try {
            String result = authService.registerUser(accountDto, name);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed");
        }
    }

    //Verify User email before login
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String verificationCode) {
        String result = authService.verifyUser(verificationCode);
        if (result.equals("Verification code invalid") ||
                result.equals("Verification code expired") ||
                result.equals("Invalid verification code")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Resend Verification Code if they don't receive it
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) {
        String result = authService.resendVerificationCode(email);
        if (result.equals("No temporary registration found for this email")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Initialize Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String result = authService.initiatePasswordReset(email);
        if (result.equals("Email not found")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

@PostMapping("/verify-password-reset")
    public ResponseEntity<String> verifyPasswordReset(@RequestParam String verificationCode) {
        try {
            String result = authService.verifyPasswordReset(verificationCode);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Reset Password using the code sent to the email
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        String result = authService.resetPassword(resetPasswordDto.getCode(), resetPasswordDto.getNewPassword());
        return ResponseEntity.ok(result);
    }


}
