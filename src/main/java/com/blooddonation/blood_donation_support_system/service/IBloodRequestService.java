package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.dto.BloodUnitDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IBloodRequestService {
    BloodRequestDto createBloodRequest(BloodRequestDto bloodRequestDto);
    List<BloodRequestDto> findAllBloodRequest();
    BloodRequestDto findBloodRequestById(int id);
    BloodRequestDto addBloodRequestDonor(BloodRequestDto bloodRequestDto, BloodUnitDto bloodUnitDto, ProfileDto profileDto);
    BloodRequestDto fulfillBloodRequest(BloodRequestDto bloodRequestDto);
    List<BloodRequestDto> getEmergencyBloodRequest();
}

