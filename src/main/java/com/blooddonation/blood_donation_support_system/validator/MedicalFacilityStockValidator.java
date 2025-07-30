package com.blooddonation.blood_donation_support_system.validator;

import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MedicalFacilityStockValidator {
    @Autowired
    private BloodUnitRepository bloodUnitRepository;
    @Autowired
    private DonationEventRepository donationEventRepository;

    public List<BloodUnit> validateAndGetBloodUnits(Long eventId) {
        List<BloodUnit> bloodUnits = bloodUnitRepository.findByEventId(eventId);
        if (bloodUnits == null || bloodUnits.isEmpty()) {
            throw new RuntimeException(String.format("No blood units found for event ID: %d", eventId));
        }
        return bloodUnits;
    }

    public DonationEvent getEventOrThrow(Long eventId) {
        return donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));
    }

    public BloodUnit getBloodUnitOrThrow(Long id) {
        return bloodUnitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood Unit not found with id: " + id));
    }
}
