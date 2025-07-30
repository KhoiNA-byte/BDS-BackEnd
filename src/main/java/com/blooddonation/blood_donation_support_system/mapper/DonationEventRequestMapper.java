package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventRequestDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.DonationEventRequest;
import com.blooddonation.blood_donation_support_system.enums.CRUDType;
import com.blooddonation.blood_donation_support_system.enums.DonationRequestStatus;
import org.springframework.stereotype.Component;

@Component
public class DonationEventRequestMapper {
    public static DonationEventRequestDto toDto(DonationEventRequest eventRequest) {
        if (eventRequest == null) {
            return null;
        }

        return DonationEventRequestDto.builder()
                .id(eventRequest.getId())
                .eventId(eventRequest.getId())
                .authorId(eventRequest.getAccount() != null ? eventRequest.getAccount().getId() : null)
                .oldDonationEventDto(eventRequest.getOldDonationEventDto())
                .newDonationEventDto(eventRequest.getNewDonationEventDto())
                .status(eventRequest.getStatus())
                .crudType(eventRequest.getCrudType())
                .build();
    }

    public static DonationEventRequest toEntity(DonationEventRequestDto eventRequestDto, Account account, DonationEvent donationEvent) {
        if (eventRequestDto == null) {
            return null;
        }

        return DonationEventRequest.builder()
                .id(eventRequestDto.getId())
                .donationEvent(donationEvent)
                .account(account)
                .oldDonationEventDto(eventRequestDto.getOldDonationEventDto())
                .newDonationEventDto(eventRequestDto.getNewDonationEventDto())
                .status(eventRequestDto.getStatus())
                .crudType(eventRequestDto.getCrudType())
                .build();
    }

    public static DonationEventRequest createDonation(DonationEventDto eventDto, Account account) {
        if (eventDto == null) {
            return null;
        }

        return DonationEventRequest.builder()
                .id(eventDto.getId())
                .account(account)
                .newDonationEventDto(eventDto)
                .status(DonationRequestStatus.PENDING)
                .crudType(CRUDType.CREATE)
                .build();
    }

    public static DonationEventRequest updateDonation(DonationEventDto eventDto, Account account, DonationEvent donationEvent) {
        if (eventDto == null) {
            return null;
        }

        return DonationEventRequest.builder()
                .id(eventDto.getId())
                .account(account)
                .donationEvent(donationEvent)
                .newDonationEventDto(eventDto)
                .oldDonationEventDto(DonationEventMapper.toDto(donationEvent))
                .status(DonationRequestStatus.PENDING)
                .crudType(CRUDType.UPDATE)
                .build();
    }

    public static DonationEventRequest deleteDonation(DonationEventDto eventDto, Account account, DonationEvent donationEvent) {
        if (eventDto == null) {
            return null;
        }

        return DonationEventRequest.builder()
                .id(null)
                .donationEvent(donationEvent)
                .account(account)
                .oldDonationEventDto(eventDto)
                .newDonationEventDto(null)
                .status(DonationRequestStatus.PENDING)
                .crudType(CRUDType.DELETE)
                .build();
    }
}
