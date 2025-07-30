package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.ComponentRequestDto;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;
import com.blooddonation.blood_donation_support_system.entity.ComponentRequest;

public class ComponentRequestMapper {

    public static ComponentRequestDto toDto(ComponentRequest entity) {
        return ComponentRequestDto.builder()
                .id(entity.getId())
                .componentType(entity.getComponentType())
                .volume(entity.getVolume())
                .request_id(entity.getBloodRequest().getId())
                .expiredDate(entity.getExpiredDate())
                .build();
    }

    public static ComponentRequest toEntity(ComponentRequestDto dto, BloodRequest bloodRequest) {
        return ComponentRequest.builder()
                .id(dto.getId())
                .componentType(dto.getComponentType())
                .volume(dto.getVolume())
                .bloodRequest(bloodRequest)
                .expiredDate(dto.getExpiredDate())
                .build();
    }
}
