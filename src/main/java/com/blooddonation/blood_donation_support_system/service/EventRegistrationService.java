package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;

public interface EventRegistrationService {
    String registerForEventOnline(Long eventId, Long timeSlotId, String userEmail, String jsonForm);
    String registerForEventOffline(Long eventId, String personalId, String userEmail, String jsonForm);
    ProfileDto registerForGuest(Long eventId, ProfileDto profileDto, String userEmail, String jsonForm);
    byte[] getQRCodeForUser(Long eventId, String email);
    String checkInMember(Long eventId, String action, String userEmail, ProfileDto profileDto);
    String cancelEventRegistration(Long eventId, String userEmail);

}
