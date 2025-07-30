package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventRequestDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.CRUDType;
import com.blooddonation.blood_donation_support_system.enums.DonationEventStatus;
import com.blooddonation.blood_donation_support_system.enums.DonationRequestStatus;
import com.blooddonation.blood_donation_support_system.mapper.DonationEventMapper;
import com.blooddonation.blood_donation_support_system.mapper.DonationEventRequestMapper;
import com.blooddonation.blood_donation_support_system.repository.*;
import com.blooddonation.blood_donation_support_system.service.DonationEventRequestService;
import com.blooddonation.blood_donation_support_system.service.DonationTimeSlotService;
import com.blooddonation.blood_donation_support_system.service.EmailService;
import com.blooddonation.blood_donation_support_system.validator.DonationEventValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationEventRequestServiceImpl implements DonationEventRequestService {
    @Autowired
    private DonationEventRepository donationEventRepository;

    @Autowired
    private DonationTimeSlotService donationTimeSlotService;

    @Autowired
    private DonationEventValidator validator;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DonationEventRequestRepository donationEventRequestRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public String createDonationRequest(DonationEventDto donationEventDto, String staffEmail) {
        Account staff = accountRepository.findByEmail(staffEmail);
        DonationEventRequest donationEventRequest = DonationEventRequestMapper.createDonation(donationEventDto, staff);
        donationEventRequestRepository.save(donationEventRequest);
        return "Donation request created successfully";
    }

    public String updateDonationRequest(Long accountId, Long eventId, DonationEventDto donationEventDto) {
        Account staff = validator.getDonorOrThrow(accountId);
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        validator.validateCorrectAuthor(staff, donationEvent);

        DonationEventRequest donationEventRequest = DonationEventRequestMapper.updateDonation(donationEventDto, staff, donationEvent);
        donationEventRequestRepository.save(donationEventRequest);
        return "Donation request updated successfully";
    }

    public String deleteDonationRequest(Long accountId, Long eventId) {
        Account staff = validator.getDonorOrThrow(accountId);
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        validator.validateCorrectAuthor(staff, donationEvent);
        DonationEventDto donationEventDto = DonationEventMapper.toDto(donationEvent);

        DonationEventRequest donationEventRequest = DonationEventRequestMapper.deleteDonation(donationEventDto, staff, donationEvent);
        donationEventRequestRepository.save(donationEventRequest);
        return "Donation request deleted successfully";
    }

    @Transactional
    public String verifyDonation(Long requestId, String action) {
        DonationEventRequest request = validator.getRequestOrThrow(requestId);
        if (!request.getStatus().equals(DonationRequestStatus.PENDING)) {
            return "Donation request has already been verified";
        }
        validator.validateEventVerification(action);
        if (action.equalsIgnoreCase("reject")) {
            request.setStatus(DonationRequestStatus.REJECTED);
            donationEventRequestRepository.save(request);
            return "Donation request rejected";
        }

        CRUDType type = request.getCrudType();
        switch (type) {
            case CREATE:
                createDonation(request.getNewDonationEventDto(), request.getAccount().getEmail());
                request.setStatus(DonationRequestStatus.APPROVED);
                donationEventRequestRepository.save(request);
                return "Donation request approved, Donation event created successfully";
            case UPDATE:
                updateDonation(request.getNewDonationEventDto(), request.getAccount().getEmail(), request);
                request.setStatus(DonationRequestStatus.APPROVED);
                donationEventRequestRepository.save(request);
                return "Donation request approved, Donation event updated successfully";
            case DELETE:
                deleteDonation(request.getDonationEvent());
                request.setStatus(DonationRequestStatus.APPROVED);
                donationEventRequestRepository.save(request);
                return "Donation request approved, Donation event deleted successfully";
            default:
                return "Invalid request type";
        }
    }

    public Page<DonationEventRequestDto> getSortedPaginatedRequests(int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return donationEventRequestRepository.findAll(pageable).map(DonationEventRequestMapper::toDto);
    }

    public Page<DonationEventRequestDto> getSortedPaginatedRequestsByAccount(String email, int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Account account = accountRepository.findByEmail(email);
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return donationEventRequestRepository.findByAccount(account, pageable).map(DonationEventRequestMapper::toDto);
    }

    public DonationEventRequestDto getDonationRequestById(Long requestId) {
        DonationEventRequest donationEventRequest = validator.getRequestOrThrow(requestId);
        return DonationEventRequestMapper.toDto(donationEventRequest);
    }

    public DonationEventRequestDto getDonationRequestByAuthor(Long requestId, Long accountId) {
        DonationEventRequest donationEventRequest = validator.getRequestOrThrow(requestId);
        validator.validateCorrectAuthor(donationEventRequest.getAccount(), donationEventRequest.getDonationEvent());
        return DonationEventRequestMapper.toDto(donationEventRequest);
    }

    public void createDonation(DonationEventDto donationEventDto, String staffEmail) {
        // Fetch Data
        Account staff = accountRepository.findByEmail(staffEmail);

        // Create And Save Donation Event
        DonationEvent donationEvent = DonationEventMapper.createDonation(donationEventDto, staff);
        DonationEvent savedDonationEvent = donationEventRepository.save(donationEvent);

        // Create time slots for the event
        List<DonationTimeSlot> timeSlots = donationTimeSlotService.createTimeSlotsForEvent(donationEventDto.getTimeSlotDtos(), savedDonationEvent);
        savedDonationEvent.setTimeSlots(timeSlots);
    }

    private void sendDonationEventNotification(DonationEvent event, String subject) {
        List<EventRegistration> eventRegistrations = event.getRegistrations();
        event.getRegistrations().forEach(registration -> {
            try {
                String htmlMessage = "<html>"
                        + "<body>"
                        + "<h2>Donation Event Update Notice</h2>"
                        + "<p>Dear " + registration.getProfileId().getName() + ",</p>"
                        + "<p>The donation event '" + event.getName() + "' has been updated.</p>"
                        + "<p>Event details:</p>"
                        + "<ul>"
                        + "<li>Date: " + event.getDonationDate() + "</li>"
                        + "<li>Location: " + event.getHospital() + "</li>"
                        + "<li>Address: " + event.getAddress() + "</li>"
                        + "<li>Donation Type: " + event.getDonationType() + "</li>"
                        + "<li>Status: " + event.getStatus() + "</li>"
                        + "</ul>"
                        + "<p><a href='" + System.getenv("FRONTEND_URL") + "/donation-events/"  + event.getId() + "'>View Event Details</a></p>"
                        + "<p>Best regards,<br>Blood Donation Support System</p>"
                        + "</body>"
                        + "</html>";

                emailService.sendVerificationEmail(
                        registration.getAccount().getEmail(),
                        subject,
                        htmlMessage
                );
            } catch (Exception e) {
                System.err.println("Failed to send email to " + registration.getAccount().getEmail() + ": " + e.getMessage());
            }
        });
    }

    @Transactional
    public void updateDonation(DonationEventDto donationEventDto, String staffEmail, DonationEventRequest request) {
        Account staff = accountRepository.findByEmail(staffEmail);
        DonationEvent existingEvent = request.getDonationEvent();

        DonationEvent donationEvent = DonationEventMapper.updateDonation(donationEventDto, staff, existingEvent.getId());
        sendDonationEventNotification(existingEvent, "Donation Event Update Notice");
        donationEventRepository.save(donationEvent);
    }

    @Transactional
    public void deleteDonation(DonationEvent donationEvent) {
        donationEvent.setStatus(DonationEventStatus.CANCELLED);
        donationEventRepository.save(donationEvent);
        sendDonationEventNotification(donationEvent, "Donation Event Update Notice");
    }
}
