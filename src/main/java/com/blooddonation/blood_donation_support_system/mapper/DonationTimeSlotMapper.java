package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.DonationTimeSlotDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.DonationTimeSlot;
import org.springframework.stereotype.Component;

@Component
public class DonationTimeSlotMapper {

    public static DonationTimeSlotDto toDto(DonationTimeSlot timeSlot) {
        if (timeSlot == null) return null;

        return DonationTimeSlotDto.builder()
                .id(timeSlot.getId())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .maxCapacity(timeSlot.getMaxCapacity())
                .currentRegistrations(timeSlot.getCurrentRegistrations())
                .build();
    }

    public static DonationTimeSlot toEntity(DonationTimeSlotDto dto, DonationEvent event) {
        if (dto == null) return null;

        return DonationTimeSlot.builder()
                .id(dto.getId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .maxCapacity(dto.getMaxCapacity())
                .currentRegistrations(0) // Always start with 0 for new entities
                .event(event)
                .build();
    }
}