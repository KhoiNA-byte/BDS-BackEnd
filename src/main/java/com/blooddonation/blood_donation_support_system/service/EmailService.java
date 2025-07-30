package com.blooddonation.blood_donation_support_system.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationEmail(String to, String subject, String htmlMessage) throws MessagingException;
}