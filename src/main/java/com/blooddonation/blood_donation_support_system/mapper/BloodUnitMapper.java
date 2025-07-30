package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.BloodUnitDto;
import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.BloodUnitStatus;
import org.springframework.stereotype.Component;

@Component
public class BloodUnitMapper {

    public static BloodUnitDto toDto(BloodUnit entity) {
        if (entity == null) return null;

        return BloodUnitDto.builder()
                .id(entity.getId())
                .eventId(entity.getEvent() != null ? entity.getEvent().getId() : null)
                .bloodRequestId(entity.getBloodRequest() != null ? entity.getBloodRequest().getId() : null)
                .accountId(entity.getDonor() != null ? entity.getDonor().getId() : null)
                .bloodType(entity.getBloodType())
                .componentType(entity.getComponentType())
                .volume(entity.getVolume())
                .status(entity.getStatus())
//                .bloodRequestId(entity.getBloodRequest().getId())
                .profileId(entity.getProfileId().getId())
                .build();
    }

    public static BloodUnit toEntity(BloodUnitDto dto, Account donor, DonationEvent event) {
        if (dto == null) return null;

        return BloodUnit.builder()
                .id(dto.getId())
                .donor(donor)
                .event(event)
                .bloodType(dto.getBloodType())
                .componentType(dto.getComponentType())
                .volume(dto.getVolume())
                .status(dto.getStatus())
                .build();
    }
    public static BloodUnit toEntity(BloodUnitDto dto, BloodRequest bloodRequest, Profile profile) {
        if (dto == null) return null;

        return BloodUnit.builder()
                .id(dto.getId())
                .bloodRequest(bloodRequest)
                .profileId(profile)
                .bloodType(dto.getBloodType())
                .componentType(dto.getComponentType())
                .volume(dto.getVolume())
                .status(dto.getStatus())
                .build();
    }

    public static BloodUnit toEntityFromRecord(SingleBloodUnitRecordDto dto,
                                               DonationEvent event,
                                               Account donor,
                                               Profile profile) {
        if (dto == null) return null;

        return BloodUnit.builder()
                .donor(profile.getAccountId() != null ? donor : null)
                .profileId(profile)
                .event(event)
                .volume(dto.getVolume())
                .bloodType(profile.getBloodType())
                .status(BloodUnitStatus.PENDING)
                .build();
    }

}