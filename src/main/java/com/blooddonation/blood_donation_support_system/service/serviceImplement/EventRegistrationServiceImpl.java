package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.CheckinTokenDto;
import com.blooddonation.blood_donation_support_system.dto.EventRegistrationDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import com.blooddonation.blood_donation_support_system.mapper.CheckinTokenMapper;
import com.blooddonation.blood_donation_support_system.mapper.EventRegistrationMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.repository.*;
import com.blooddonation.blood_donation_support_system.service.CheckinTokenService;
import com.blooddonation.blood_donation_support_system.service.EventRegistrationService;
import com.blooddonation.blood_donation_support_system.service.QRCodeService;
import com.blooddonation.blood_donation_support_system.validator.DonationEventValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventRegistrationServiceImpl implements EventRegistrationService {
    @Autowired
    private DonationEventRepository donationEventRepository;
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private DonationEventValidator validator;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private DonationTimeSlotRepository donationTimeSlotRepository;
    @Autowired
    private CheckinTokenService checkinTokenService;

    @Transactional
    public String registerForEventOnline(Long eventId, Long timeSlotId, String userEmail, String jsonForm) {
        //Fetch Data
        Account account = accountRepository.findByEmail(userEmail);
        Profile profile = account.getProfile();
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        DonationTimeSlot timeSlot = validator.getSlotOrThrow(timeSlotId);

        // Validate Registration Eligibility
        validator.validateRegistrationEligibility(account, donationEvent, timeSlot);

        // Create And Save Registration
        EventRegistrationDto eventRegistrationDto = new EventRegistrationDto();
        EventRegistration registration = EventRegistrationMapper.toEntity(eventRegistrationDto,account,donationEvent,timeSlot,profile,jsonForm);
        eventRegistrationRepository.save(registration);

        // Generate CheckinToken
        CheckinTokenDto tokenDto = checkinTokenService.generateTokenForProfile(profile, donationEvent);

        // Generate QR code URL and image
//        String qrUrl = String.format("http://localhost:8080/api/checkin/info/%d?checkinToken=%s", eventId, tokenDto.getToken());
        try {
//            byte[] qrCode = qrCodeService.generateQRCode(qrUrl);
//            registration.setQrCode(qrCode);
            CheckinToken checkinToken = CheckinTokenMapper.toEntity(tokenDto);
            registration.setCheckinToken(checkinToken);
            eventRegistrationRepository.save(registration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage());
        }
        return "Registration successful";
    }

    @Transactional
    public String registerForEventOffline(Long eventId, String personalId, String userEmail, String jsonForm) {
        // Fetch Data
        DonationEvent event = validator.getEventOrThrow(eventId);
        Account member = validator.validateAndGetMemberAccount(personalId);
        EventRegistration registration = validator.validateAndGetExistingRegistration(member, event);
        if (registration != null) {
            registration.setStatus(DonationRegistrationStatus.CHECKED_IN);
//            registration.setProfileId(member.getProfile());
            eventRegistrationRepository.save(registration);
            return "Member checked in successfully";
        }

        validator.validateRegistrationEligibility(member, event, null);
        EventRegistration newRegistration = EventRegistrationMapper.createOfflineRegistration(member, event, jsonForm);
        event.setRegisteredMemberCount(event.getRegisteredMemberCount() - 1);
        eventRegistrationRepository.save(newRegistration);
        return "Member registered and checked in successfully";
    }

    @Transactional
    public ProfileDto registerForGuest(Long eventId, @Valid ProfileDto profileDto, String userEmail, String jsonForm) {
        // Fetch Data
        Account staff = accountRepository.findByEmail(userEmail);
        DonationEvent event = validator.getEventOrThrow(eventId);

        // Check if profile with personalId already exists
        Optional<Profile> existingProfile = profileRepository.findByPersonalId(profileDto.getPersonalId());
        if (existingProfile.isPresent()) {
            throw new RuntimeException("Profile with this Personal ID already exists");
        }

        // Create and save profile
        Profile profile = ProfileMapper.toEntity(profileDto);
        Profile savedProfile = profileRepository.save(profile);

        // Create event registration for guest
        EventRegistration registration = EventRegistrationMapper.createGuestRegistration(event, staff, savedProfile, jsonForm);
        event.setRegisteredMemberCount(event.getRegisteredMemberCount() - 1);
        eventRegistrationRepository.save(registration);

        return ProfileMapper.toDto(savedProfile);
    }

    @Transactional
    public String cancelEventRegistration(Long eventId, String userEmail) {
        // Fetch Data
        Account member = accountRepository.findByEmail(userEmail);
        DonationEvent event = validator.getEventOrThrow(eventId);
        EventRegistration registration = eventRegistrationRepository.findByEventAndAccount(event, member)
                .orElseThrow(() -> new RuntimeException("No registration found for this event"));

        // Validate cancellation eligibility
        validator.validateCancellation(event, registration);

        // Update registration status and decrease counts
        registration.setStatus(DonationRegistrationStatus.CANCELLED);
        eventRegistrationRepository.save(registration);
        event.setRegisteredMemberCount(event.getRegisteredMemberCount() - 1);
        donationEventRepository.save(event);
        DonationTimeSlot timeSlot = registration.getTimeSlot();
        if (timeSlot != null) {
            timeSlot.setCurrentRegistrations(timeSlot.getCurrentRegistrations() - 1);
            donationTimeSlotRepository.save(timeSlot);
        }

        return "Registration cancelled successfully";
    }

    @Transactional
    public byte[] getQRCodeForUser(Long eventId, String email) {
        // Fetch Data
        Account member = accountRepository.findByEmail(email);
        Account validatedMember = validator.validateAndGetMemberAccount(member.getProfile().getPersonalId());
        DonationEvent event = validator.getEventOrThrow(eventId);
        EventRegistration registration = validator.validateAndGetExistingRegistration(validatedMember, event);
        return validator.validateQRCode(registration.getQrCode());
    }


    @Transactional
    public String checkInMember(Long eventId, String action, String userEmail, ProfileDto profileDto) {
        // Validate Input
        validator.validateCheckinVerification(action);
        // Fetch Data
        DonationEvent event = validator.getEventOrThrow(eventId);
        EventRegistration registration = validator.getRegistrationOrThrow(profileDto.getPersonalId(), event);

        if (action.equals("approve")) {
            registration.setStatus(DonationRegistrationStatus.CHECKED_IN);
            eventRegistrationRepository.save(registration);
        } else if (action.equals("reject")) {
            registration.setStatus(DonationRegistrationStatus.REJECTED);
            eventRegistrationRepository.save(registration);
        }
        return "Checked-in " + action + " successfully";
    }
}
