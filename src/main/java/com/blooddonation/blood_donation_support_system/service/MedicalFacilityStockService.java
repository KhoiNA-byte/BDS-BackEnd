package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.dto.MedicalFacilityStockDto;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;

import java.util.List;


public interface MedicalFacilityStockService {
    String addBloodUnitsToStockByEventId(Long eventId, String userEmail);
    int withdrawBloodFromStock(BloodRequestDto bloodRequestDto);
    String updateBeforeWithdraw(String userEmail);
    List<MedicalFacilityStockDto> getAllAvailableBlood();
    List<MedicalFacilityStockDto> getAvailableBloodByType(BloodType bloodType, List<ComponentType> componentTypes);
    int addToStock(BloodRequestDto bloodRequestDto);
    List<MedicalFacilityStockDto> getAvailableBloodByType(BloodType bloodType, ComponentType componentType);
    MedicalFacilityStockDto addBloodIntoStock(MedicalFacilityStockDto medicalFacilityStockDto);
    void deleteStockById(Long id);
}