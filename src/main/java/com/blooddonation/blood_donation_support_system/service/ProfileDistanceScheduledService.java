package com.blooddonation.blood_donation_support_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileDistanceScheduledService {
    
    private final ProfileDistanceService profileDistanceService;
    
    /**
     * Runs every day at 2 AM to calculate missing distances
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void calculateMissingDistancesScheduled() {
        log.info("Starting scheduled calculation of missing profile distances...");
        
        try {
            profileDistanceService.calculateMissingDistances();
            log.info("Scheduled calculation of missing profile distances completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled calculation of missing profile distances", e);
        }
    }
}
