package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.UserDonationHistoryDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDonationHistoryMapper {

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    public UserDonationHistoryDto toDto(EventRegistration eventRegistration) {
        if (eventRegistration == null) return null;

        DonationEvent donationEvent = eventRegistration.getEvent();
        Account account = eventRegistration.getAccount();
        BloodUnit bloodUnit = bloodUnitRepository.findByDonorIdAndEvent_Id(account.getId(), donationEvent.getId());
        if (bloodUnit == null) {
            bloodUnit = bloodUnitRepository.findByProfileIdAndEvent_Id(eventRegistration.getProfileId(), donationEvent.getId());
        };
        return UserDonationHistoryDto.builder()
                .registrationId(eventRegistration.getId())
                .registrationDate(eventRegistration.getRegistrationDate())
                .registrationDonationRegistrationStatus(eventRegistration.getStatus())
                .donationDate(donationEvent.getDonationDate())
                .donationType(eventRegistration.getDonationType())
                .donationLocation(donationEvent.getAddress() + ", " +
                        donationEvent.getWard() + ", " +
                        donationEvent.getDistrict() + ", " +
                        donationEvent.getCity())
                .donationName(donationEvent.getName())
                .donationVolume(bloodUnit != null ? bloodUnit.getVolume() : null)
                .accountId(account.getId())
                .profileId(eventRegistration.getProfileId().getId())
                .donationId(eventRegistration.getEvent().getId())
                .build();
    }

    public List<UserDonationHistoryDto> toDtoList(List<EventRegistration> registrations) {
        if (registrations == null || registrations.isEmpty()) return Collections.emptyList();

        return registrations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}

