package com.blooddonation.blood_donation_support_system.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDistanceDto {
    private Long id;
    private Long profileId;
    private String profileName;
    private Double distanceInMeters;
    private Double distanceInKilometers;
    private Long durationInSeconds;
    private String durationText;
    private String distanceText;
    private String profileAddress;
    private String medicalFacilityAddress;
    private LocalDateTime calculatedAt;
    private LocalDateTime lastUpdated;
}
