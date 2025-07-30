package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.ProfileDistanceDto;
import com.blooddonation.blood_donation_support_system.entity.ProfileDistance;
import org.springframework.stereotype.Component;

@Component
public class ProfileDistanceMapper {
    
    public static ProfileDistanceDto toDto(ProfileDistance profileDistance) {
        if (profileDistance == null) return null;
        
        return ProfileDistanceDto.builder()
                .id(profileDistance.getId())
                .profileId(profileDistance.getProfile().getId())
                .profileName(profileDistance.getProfile().getName())
                .distanceInMeters(profileDistance.getDistanceInMeters())
                .distanceInKilometers(profileDistance.getDistanceInKilometers())
                .durationInSeconds(profileDistance.getDurationInSeconds())
                .durationText(profileDistance.getDurationText())
                .distanceText(profileDistance.getDistanceText())
                .profileAddress(profileDistance.getProfileAddress())
                .medicalFacilityAddress(profileDistance.getMedicalFacilityAddress())
                .calculatedAt(profileDistance.getCalculatedAt())
                .lastUpdated(profileDistance.getLastUpdated())
                .build();
    }
    
    public static ProfileDistance toEntity(ProfileDistanceDto dto) {
        if (dto == null) return null;
        
        return ProfileDistance.builder()
                .id(dto.getId())
                .distanceInMeters(dto.getDistanceInMeters())
                .distanceInKilometers(dto.getDistanceInKilometers())
                .durationInSeconds(dto.getDurationInSeconds())
                .durationText(dto.getDurationText())
                .distanceText(dto.getDistanceText())
                .profileAddress(dto.getProfileAddress())
                .medicalFacilityAddress(dto.getMedicalFacilityAddress())
                .calculatedAt(dto.getCalculatedAt())
                .lastUpdated(dto.getLastUpdated())
                .build();
    }
}
