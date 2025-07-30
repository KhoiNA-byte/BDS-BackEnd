package com.blooddonation.blood_donation_support_system.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ImageUploadService {
    String uploadImage(MultipartFile image);
}
