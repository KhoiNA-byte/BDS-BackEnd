package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.OrganizerDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Organizer;
import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrganizerMapper {

    public static OrganizerDto toDto(Organizer organizer) {
        if (organizer == null) return null;

        return OrganizerDto.builder()
                .id(organizer.getId())
                .organizationName(organizer.getOrganizationName())
                .contactPersonName(organizer.getContactPersonName())
                .email(organizer.getEmail())
                .phoneNumber(organizer.getPhoneNumber())
                .address(organizer.getAddress())
                .ward(organizer.getWard())
                .district(organizer.getDistrict())
                .city(organizer.getCity())
                .description(organizer.getDescription())
                .websiteUrl(organizer.getWebsiteUrl())
                .status(organizer.getStatus())
                .createdBy(organizer.getCreatedBy() != null ? organizer.getCreatedBy().getId() : null)
                .createdDate(organizer.getCreatedDate())
                .updatedDate(organizer.getUpdatedDate())
                .build();
    }

    public static Organizer toEntity(OrganizerDto dto, Account createdByAccount) {
        if (dto == null) return null;

        return Organizer.builder()
                .id(dto.getId())
                .organizationName(dto.getOrganizationName())
                .contactPersonName(dto.getContactPersonName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .ward(dto.getWard())
                .district(dto.getDistrict())
                .city(dto.getCity())
                .description(dto.getDescription())
                .websiteUrl(dto.getWebsiteUrl())
                .status(dto.getStatus() != null ? dto.getStatus() : AccountStatus.ENABLE)
                .createdBy(createdByAccount)
                .createdDate(dto.getCreatedDate() != null ? dto.getCreatedDate() : LocalDateTime.now())
                .updatedDate(dto.getUpdatedDate() != null ? dto.getUpdatedDate() : LocalDateTime.now())
                .build();
    }

    public static void updateEntityFromDto(Organizer organizer, OrganizerDto dto) {
        if (organizer == null || dto == null) return;

        organizer.setOrganizationName(dto.getOrganizationName());
        organizer.setContactPersonName(dto.getContactPersonName());
        organizer.setEmail(dto.getEmail());
        organizer.setPhoneNumber(dto.getPhoneNumber());
        organizer.setAddress(dto.getAddress());
        organizer.setWard(dto.getWard());
        organizer.setDistrict(dto.getDistrict());
        organizer.setCity(dto.getCity());
        organizer.setDescription(dto.getDescription());
        organizer.setWebsiteUrl(dto.getWebsiteUrl());
        
        if (dto.getStatus() != null) {
            organizer.setStatus(dto.getStatus());
        }
        
        organizer.setUpdatedDate(LocalDateTime.now());
    }
}
