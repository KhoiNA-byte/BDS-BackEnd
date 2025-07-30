package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.ComponentRequest;
import com.blooddonation.blood_donation_support_system.entity.Profile;

import java.util.List;
import java.util.stream.Collectors;

public class BloodRequestMapper {

    public static BloodRequestDto toBloodRequestDto(BloodRequest bloodRequest) {
        return BloodRequestDto.builder()
                .id(bloodRequest.getId())
                .profileId(bloodRequest.getProfile() != null ? bloodRequest.getProfile().getId() : null)
                .profile(bloodRequest.getProfile() != null ? ProfileMapper.toDto(bloodRequest.getProfile()) : null)
                .bloodType(bloodRequest.getBloodType())
                .endTime(bloodRequest.getEndTime())
                .requiredDate(bloodRequest.getRequiredDate())
                .createdTime(bloodRequest.getCreatedTime())
                .status(bloodRequest.getStatus())
                .urgency(bloodRequest.getUrgency())
                .medicalConditions(bloodRequest.getMedicalConditions())
                .additionalMedicalInformation(bloodRequest.getAdditionalMedicalInformation())
                .additionalNotes(bloodRequest.getAdditionalNotes())
                .isDisabled(bloodRequest.isDisabled())
                .haveServed(bloodRequest.isHaveServed())
                .isPregnant(bloodRequest.isPregnant())
                .isAutomation(bloodRequest.isAutomation())
                .componentRequests(
                        bloodRequest.getComponentRequests() != null ?
                                bloodRequest.getComponentRequests().stream()
                                        .map(ComponentRequestMapper::toDto)
                                        .collect(Collectors.toList()) : null
                )
                .bloodUnits(
                        bloodRequest.getBloodUnits() != null ?
                                bloodRequest.getBloodUnits().stream()
                                        .map(BloodUnitMapper::toDto)
                                        .collect(Collectors.toList()) : null
                )
                .build();
    }

    public static BloodRequest toBloodRequestEntity(BloodRequestDto dto, Profile profile) {
        BloodRequest bloodRequest = BloodRequest.builder()
                .id(dto.getId())
                .profile(profile)
                .bloodType(dto.getBloodType())
                .status(dto.getStatus())
                .endTime(dto.getEndTime())
                .requiredDate(dto.getRequiredDate())
                .createdTime(dto.getCreatedTime())
                .urgency(dto.getUrgency())
                .medicalConditions(dto.getMedicalConditions())
                .additionalMedicalInformation(dto.getAdditionalMedicalInformation())
                .additionalNotes(dto.getAdditionalNotes())
                .isPregnant(dto.isPregnant())
                .isDisabled(dto.isDisabled())
                .haveServed(dto.isHaveServed())
                .isAutomation(dto.isAutomation())
                .build();

        if (dto.getComponentRequests() != null) {
            List<ComponentRequest> componentRequests = dto.getComponentRequests().stream()
                    .map(dto1 -> ComponentRequestMapper.toEntity(dto1, bloodRequest))
                    .collect(Collectors.toList());
            bloodRequest.setComponentRequests(componentRequests);
        }

        if (dto.getBloodUnits() != null) {
            List<BloodUnit> bloodUnits = dto.getBloodUnits().stream()
                    .map(dto1 -> {
                        ProfileDto profileDto = new ProfileDto();
                        profileDto.setId(dto1.getProfileId());
                        Profile bloodUnitProfile = ProfileMapper.toEntity(profileDto);
                        return BloodUnitMapper.toEntity(dto1, bloodRequest, bloodUnitProfile);
                    })
                    .collect(Collectors.toList());
            bloodRequest.setBloodUnits(bloodUnits);
        }
        return bloodRequest;
    }
//    public static BloodRequest toBloodRequestEntity(BloodRequestDto dto) {
//        BloodRequest bloodRequest = BloodRequest.builder()
//                .id(dto.getId())
//                .bloodType(dto.getBloodType())
//                .status(dto.getStatus())
//                .endTime(dto.getEndTime())
//                .name(dto.getName())
//                .address(dto.getAddress())
//                .personalId(dto.getPersonalId())
//                .phone(dto.getPhone())
//                .createdTime(dto.getCreatedTime())
//                .urgency(dto.getUrgency())
//                .isPregnant(dto.isPregnant())
//                .isDisabled(dto.isDisabled())
//                .haveServed(dto.isHaveServed())
//                .isAutomation(dto.isAutomation())
//                .build();
//
//        if (dto.getComponentRequests() != null) {
//            List<ComponentRequest> componentRequests = dto.getComponentRequests().stream()
//                    .map((ComponentRequestDto dto1) -> ComponentRequestMapper.toEntity(dto1, bloodRequest))
//                    .collect(Collectors.toList());
//
//            bloodRequest.setComponentRequests(componentRequests);
//        }
//        return bloodRequest;
//    }
}
