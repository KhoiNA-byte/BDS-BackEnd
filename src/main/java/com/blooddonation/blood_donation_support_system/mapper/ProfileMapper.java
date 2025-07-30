package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    public static ProfileDto toDto(Profile Profile) {
        if (Profile == null) return null;

        return ProfileDto.builder()
                .id(Profile.getId())
                .accountId(Profile.getAccountId())
                .name(Profile.getName())
                .phone(Profile.getPhone())
                .address(Profile.getAddress())
                .ward(Profile.getWard())
                .district(Profile.getDistrict())
                .city(Profile.getCity())
                .bloodType(Profile.getBloodType())
                .gender(Profile.getGender())
                .dateOfBirth(Profile.getDateOfBirth())
                .lastDonationDate(Profile.getLastDonationDate())
                .nextEligibleDonationDate(Profile.getNextEligibleDonationDate())
                .status(Profile.getStatus())
                .personalId(Profile.getPersonalId())
                .build();
    }

    public static Profile toEntity(ProfileDto profileDto) {
        if (profileDto == null) return null;

        return Profile.builder()
                .id(profileDto.getId())
                .accountId(profileDto.getAccountId())
                .name(profileDto.getName())
                .phone(profileDto.getPhone())
                .address(profileDto.getAddress())
                .ward(profileDto.getWard())
                .district(profileDto.getDistrict())
                .city(profileDto.getCity())
                .bloodType(profileDto.getBloodType())
                .gender(profileDto.getGender())
                .dateOfBirth(profileDto.getDateOfBirth())
                .lastDonationDate(profileDto.getLastDonationDate())
                .nextEligibleDonationDate(profileDto.getNextEligibleDonationDate() == null ? profileDto.getLastDonationDate() : profileDto.getNextEligibleDonationDate())
                .status(profileDto.getStatus())
                .personalId(profileDto.getPersonalId())
                .build();
    }

    // New method to update existing entity from DTO
    public static void updateEntityFromDto(Profile profile, ProfileDto dto) {
        if (profile == null || dto == null) return;

        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setWard(dto.getWard());
        profile.setDistrict(dto.getDistrict());
        profile.setCity(dto.getCity());
        profile.setBloodType(dto.getBloodType());
        profile.setGender(dto.getGender());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setPersonalId(dto.getPersonalId());
        profile.setStatus(dto.getStatus());

        if (profile.getLastDonationDate() == null) {
            profile.setLastDonationDate(dto.getLastDonationDate());
        }
        if (profile.getNextEligibleDonationDate() == null) {
            profile.setNextEligibleDonationDate(dto.getLastDonationDate());
        }
    }

}