package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.DonationTimeSlotDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.DonationTimeSlot;

import java.util.List;

public interface DonationTimeSlotService {
    List<DonationTimeSlot> createTimeSlotsForEvent(List<DonationTimeSlotDto> donationTimeSlotDto, DonationEvent event);
}