package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    @Value("${upload.dir}")
    private String uploadDir;

    public String uploadImage(MultipartFile image){
        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path path = Paths.get(uploadDir, filename);
        try {
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "/uploads/" + filename;
    }
}
