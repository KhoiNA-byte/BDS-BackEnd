package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.EventRegistrationDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EventRegistrationMapper {

    public static EventRegistrationDto toDto(EventRegistration registration) {
        if (registration == null) return null;

        return EventRegistrationDto.builder()
                .id(registration.getId())
                .accountId(registration.getAccount() != null ? registration.getAccount().getId() : null)
                .eventId(registration.getEvent() != null ? registration.getEvent().getId() : null)
                .bloodType(registration.getBloodType())
                .donationType(registration.getDonationType())
                .registrationDate(registration.getRegistrationDate())
                .status(registration.getStatus())
                .qrCode(registration.getQrCode())
                .jsonForm(registration.getJsonForm())
                .build();
    }

    public static EventRegistration toEntity(EventRegistrationDto dto,
                                             Account account,
                                             DonationEvent event,
                                             DonationTimeSlot timeSlot,
                                             Profile profile,
                                             String jsonForm) {
        if (dto == null) return null;

        return EventRegistration.builder()
                .id(dto.getId())
                .account(account)
                .event(event)
                .timeSlot(timeSlot)
                .profileId(account.getProfile())
                .bloodType(profile.getBloodType())
                .donationType(event.getDonationType())
                .registrationDate(dto.getRegistrationDate())
                .status(dto.getStatus())
                .qrCode(dto.getQrCode())
                .jsonForm(jsonForm)
                .build();
    }

    public static EventRegistration createGuestRegistration(DonationEvent event,
                                                            Account account,
                                                            Profile profile, String jsonForm) {
        if (event == null) return null;

        return EventRegistration.builder()
                .account(account)
                .event(event)
                .profileId(profile)
                .donationType(event.getDonationType())
                .bloodType(profile.getBloodType())
                .status(DonationRegistrationStatus.CHECKED_IN)
                .jsonForm(jsonForm)
                .build();
    }

    public static EventRegistration createOfflineRegistration(Account member, DonationEvent event, String jsonForm) {
        if (event == null) return null;

        return EventRegistration.builder()
                .account(member)
                .event(event)
                .registrationDate(LocalDate.now())
                .bloodType(member.getProfile().getBloodType())
                .donationType(event.getDonationType())
                .status(DonationRegistrationStatus.CHECKED_IN)
                .profileId(member.getProfile())
                .jsonForm(jsonForm)
                .build();
    }
}