package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface DonationEventService {
    DonationEventDto getDonationEventById(Long eventId);

    DonationEventDto getDonationEventByAuthor(Long eventId, Long accountId);

    String recordMultipleBloodDonations(Long eventId, List<SingleBloodUnitRecordDto> records, String userEmail);

    Page<AccountDto> getEventDonors(Long eventId, Long timeSlotId, int pageNumber, int pageSize, String sortBy, boolean ascending);

    List<ProfileDto> getEventDonorProfiles(Long eventId);

    Page<ProfileDto> getEventDonorProfilesPage(Long eventId, int pageNumber, int pageSize, String sortBy, boolean ascending);

    Page<DonationEventDto> getSortedPaginatedEvents(int pageNumber, int pageSize, String sortBy, boolean ascending);

    Page<DonationEventDto> getSortedPaginatedEventsByAccount(Long accountId, int pageNumber, int pageSize, String sortBy, boolean ascending);

    Page<DonationEventDto> getPaginatedEventsByDateRange(LocalDate start, LocalDate end, int pageNumber, int pageSize, String sortBy, boolean ascending);

    List<DonationEventDto> getOngoingDonationEvents();
}