package com.blooddonation.blood_donation_support_system.validator;

import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.DonationEventStatus;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import com.blooddonation.blood_donation_support_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DonationEventValidator {
    @Autowired
    private DonationEventRepository donationEventRepository;

    @Autowired
    private DonationEventRequestRepository donationEventRequestRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private DonationTimeSlotRepository donationTimeSlotRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogRequestRepository blogRequestRepository;

    public DonationEvent getEventOrThrow(Long eventId) {
        return donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));
    }

    public DonationEventRequest getRequestOrThrow(Long requestId) {
        return donationEventRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Donation request not found"));
    }

    public Blog getBlogOrThrow(Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + blogId));
    }

    public BlogRequest getBlogRequestOrThrow(Long blogRequestId) {
        return blogRequestRepository.findById(blogRequestId)
                .orElseThrow(() -> new RuntimeException("Blog request not found with id: " + blogRequestId));
    }

    public DonationTimeSlot getSlotOrThrow(Long timeSlotId) {
        return donationTimeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found with id: " + timeSlotId));
    }

    public Account getDonorOrThrow(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException(("Donor not found with id: " + accountId)));
    }

    public Profile getProfileOrThrow(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException(("Profile not found with id: " + profileId)));
    }

    public Profile getProfileOrThrow(String personalId) {
        return profileRepository.findByPersonalId(personalId)
                .orElseThrow(() -> new RuntimeException(("Profile not found with id: " + personalId)));
    }

    public EventRegistration getRegistrationOrThrow(String personalId, DonationEvent event) {
        Profile profile = profileRepository.findByPersonalId(personalId)
                .orElseThrow(() -> new RuntimeException("Profile not found with personal ID: " + personalId));

        Account account = accountRepository.findById(profile.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found for personal ID: " + personalId));
        return eventRegistrationRepository.findByEventAndAccount(event, account)
                .orElseThrow(() -> new RuntimeException("No pending registration found for personal ID: " + personalId));
    }

    public void validateEventVerification(String action) {
        if (!action.equalsIgnoreCase("approve") && !action.equalsIgnoreCase("reject")) {
            throw new RuntimeException("Invalid action: " + action);
        }
    }

    public void validateCorrectAuthor(Account account, DonationEvent event) {
        if (event != null && !account.equals(event.getAccount())) {
            throw new RuntimeException("You don't have the right to edit this event");
        }
    }

    public void validateCorrectAuthor(Account account, Blog blog) {
        if (!account.equals(blog.getAuthor())) {
            throw new RuntimeException("You don't have the right to edit this blog");
        }
    }

    public void validateCheckinVerification(String action) {
        if (!action.equals("approve") && !action.equals("reject")) {
            throw new RuntimeException("Invalid action: " + action);
        }
    }

    public void validateBloodDonationRecording(DonationEvent event, List<SingleBloodUnitRecordDto> records) {
        if (event.getStatus().equals(DonationEventStatus.COMPLETED)) {
            throw new RuntimeException("Event is already recorded");
        }

        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Blood donation records cannot be empty");
        }
    }

    public void validateRegistrationEligibility(Account account, DonationEvent event, DonationTimeSlot timeSlot) {
        Profile profile = account.getProfile();
        // Check event status
        if (event.getStatus() != DonationEventStatus.AVAILABLE) {
            throw new RuntimeException("Cannot register for an event that is not available");
        }

        // Check registration deadline
//        if (event.getDonationDate().minusDays(1).isBefore(LocalDate.now()) ||
//                event.getDonationDate().minusDays(1).isEqual(LocalDate.now())) {
//            throw new RuntimeException("Registration is closed. You cannot register one day before the event.");
//        }

        // Check event capacity
        if (event.getRegisteredMemberCount() >= event.getTotalMemberCount()) {
            throw new RuntimeException("Event has reached maximum capacity");
        }

        // Validate time slot belongs to event
        if (timeSlot != null && timeSlot.getEvent() != null && !timeSlot.getEvent().getId().equals(event.getId())) {
            throw new RuntimeException("Time slot does not belong to this event");
        }

        // Check duplicate registration
        if (eventRegistrationRepository.existsByAccountAndEvent(account, event)) {
            throw new RuntimeException("You have already registered for this event");
        }

        // Check donation eligibility
        if (profile.getNextEligibleDonationDate() != null &&
                profile.getNextEligibleDonationDate().isAfter(event.getDonationDate())) {
            throw new RuntimeException("You are not eligible to donate on this date. Please check your next eligible donation date.");
        }

        if (profile.getBloodType() == null) {
            throw new RuntimeException("Blood type cannot be null. Please update your profile.");
        }
    }

    public void validateCheckIn(DonationEvent event, EventRegistration registration, Account member) {
        if (!registration.getEvent().getId().equals(event.getId())) {
            throw new RuntimeException("Registration does not belong to this event");
        }
        if (!registration.getAccount().getId().equals(member.getId())) {
            throw new RuntimeException("Registration does not belong to this user");
        }
        if (registration.getStatus() == DonationRegistrationStatus.CHECKED_IN) {
            throw new RuntimeException("User is already checked-in for this event");
        }
    }

    public byte[] validateQRCode(byte[] qrCode) {
        if (qrCode == null) {
            throw new RuntimeException("QR code not generated for this registration");
        }
        return qrCode;
    }

    public Account validateAndGetMemberAccount(String personalId) {
        Profile profile = profileRepository.findByPersonalId(personalId)
                .orElseThrow(() -> new RuntimeException("Not a member: No profile found with personal ID " + personalId));

        return accountRepository.findById(profile.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found for member"));
    }

    public EventRegistration validateAndGetExistingRegistration(Account member, DonationEvent event) {
        if (eventRegistrationRepository.existsByAccountAndEvent(member, event)) {
            return eventRegistrationRepository.findByAccountAndEventAndStatus(member, event, DonationRegistrationStatus.PENDING)
                    .orElseThrow(() -> new RuntimeException("Registration not found or already checkin for this event"));
        }
        return null;
    }

    public void validateCancellation(DonationEvent event, EventRegistration registration) {
        // Can't cancel if already checked in or completed
        if (registration.getStatus() == DonationRegistrationStatus.CHECKED_IN || registration.getStatus() == DonationRegistrationStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel registration after check-in or completion");
        }

        // Can't cancel one day before the event
        if (event.getDonationDate().minusDays(1).isBefore(LocalDate.now()) ||
                event.getDonationDate().minusDays(1).isEqual(LocalDate.now())) {
            throw new RuntimeException("Cannot cancel registration one day before the event");
        }

        // Can't cancel if event is completed or cancelled
        if (event.getStatus() == DonationEventStatus.COMPLETED || event.getStatus() == DonationEventStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel registration for a completed or cancelled event");
        }
    }
}
