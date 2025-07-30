package com.blooddonation.blood_donation_support_system.component;

import com.blooddonation.blood_donation_support_system.service.ProfileDistanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileDistanceStartupComponent implements ApplicationRunner {
    
    private final ProfileDistanceService profileDistanceService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting profile distance calculation check...");
        
        try {
            // Calculate distances for profiles that don't have them
            profileDistanceService.calculateMissingDistances();
            log.info("Profile distance calculation check completed successfully");
        } catch (Exception e) {
            log.error("Error during profile distance calculation check", e);
            // Don't throw the exception to prevent application startup failure
        }
    }
}
