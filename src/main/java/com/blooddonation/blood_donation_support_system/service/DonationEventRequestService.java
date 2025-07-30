package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventRequestDto;
import org.springframework.data.domain.Page;


public interface DonationEventRequestService {
    String createDonationRequest(DonationEventDto donationEventDto, String staffEmail);
    String updateDonationRequest(Long accountId, Long eventId, DonationEventDto donationEventDto);
    String deleteDonationRequest(Long accountId, Long eventId);
    String verifyDonation(Long requestId, String action);
    Page<DonationEventRequestDto> getSortedPaginatedRequests(int pageNumber, int pageSize, String sortBy, boolean ascending);
    DonationEventRequestDto getDonationRequestById(Long requestId);
    DonationEventRequestDto getDonationRequestByAuthor(Long requestId, Long accountId);
    Page<DonationEventRequestDto> getSortedPaginatedRequestsByAccount(String email, int pageNumber, int pageSize, String sortBy, boolean ascending);
}
