package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.CheckinTokenDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileWithFormResponseDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.CheckinToken;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.mapper.CheckinTokenMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.CheckinTokenRepository;
import com.blooddonation.blood_donation_support_system.repository.EventRegistrationRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import com.blooddonation.blood_donation_support_system.service.CheckinTokenService;
import com.blooddonation.blood_donation_support_system.validator.DonationEventValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CheckinTokenServiceImpl implements CheckinTokenService {
    @Autowired
    private CheckinTokenRepository checkinTokenRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;
    @Autowired
    private DonationEventValidator validator;

    @Override
    public CheckinTokenDto generateTokenForProfile(Profile profile, DonationEvent donationEvent) {
        CheckinToken token = new CheckinToken();
        token.setToken(UUID.randomUUID().toString());
        token.setProfile(profile);
        token.setCreationDate(LocalDate.now());
        token.setExpirationDate(donationEvent.getDonationDate().plusDays(1));

        checkinTokenRepository.save(token);
        return CheckinTokenMapper.toDto(token);
    }

//    @Transactional
//    public void deleteToken(String token) {
//        CheckinToken checkinToken = checkinTokenRepository.findByToken(token)
//                .orElseThrow(() -> new RuntimeException("Token not found"));
//        checkinTokenRepository.delete(checkinToken);
//    }

    @Override
    @Transactional
    public ProfileWithFormResponseDto getProfileFromToken(String token, String email, Long eventId) {
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        CheckinToken checkinToken = checkinTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (checkinToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Token has expired");
        }
        Profile profile = checkinToken.getProfile();
        EventRegistration eventRegistration = validator.getRegistrationOrThrow(profile.getPersonalId(), donationEvent);
        String jsonForm = eventRegistration.getJsonForm();
//        deleteToken(token);
        return new ProfileWithFormResponseDto(ProfileMapper.toDto(profile), jsonForm, eventRegistration.getStatus(), checkinToken.getToken());// Convert to DTO
    }

    @Override
    @Transactional
    public ProfileWithFormResponseDto getProfileFromPersonalId(String personalId, String email, Long eventId) {
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);

        EventRegistration eventRegistration = validator.getRegistrationOrThrow(personalId, donationEvent);
        CheckinToken checkinToken = eventRegistration.getCheckinToken();

        if (checkinToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Token has expired");
        }
        Profile profile = validator.getProfileOrThrow(personalId);
        String jsonForm = eventRegistration.getJsonForm();
//        deleteToken(token);
        return new ProfileWithFormResponseDto(ProfileMapper.toDto(profile), jsonForm, eventRegistration.getStatus(), checkinToken.getToken());// Convert to DTO
    }

    @Override
    public String generateTokenForUser(Long eventId, String email) {
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);

        // Find the account by email
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("Account not found with email: " + email);
        }

        // Find the event registration
        EventRegistration eventRegistration = eventRegistrationRepository.findByEventAndAccount(donationEvent, account)
                .orElseThrow(() -> new RuntimeException("No registration found for this user and event"));

        // Get the existing checkin token from the registration
        CheckinToken checkinToken = eventRegistration.getCheckinToken();

        if (checkinToken == null) {
            throw new RuntimeException("Checkin token not found for this registration");
        }

        // Check if token is still valid
        if (checkinToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Checkin token has expired");
        }

        return checkinToken.getToken();
    }

    @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
    public void cleanExpiredTokens() {
        for (CheckinToken checkinToken : checkinTokenRepository.findAll()) {
            if (checkinToken.getExpirationDate().isBefore(LocalDate.now())) {
                throw new RuntimeException("Checkin token has expired");
            }
        }
    }


}
