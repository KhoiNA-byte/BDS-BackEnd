package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.BulkBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.service.DonationEventRequestService;
import com.blooddonation.blood_donation_support_system.service.DonationEventService;
import com.blooddonation.blood_donation_support_system.service.MedicalFacilityStockService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/donation-event")
@CrossOrigin
public class DonationEventController {
    @Autowired
    private DonationEventService donationEventService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private DonationEventRequestService donationEventRequestService;
    @Autowired
    private MedicalFacilityStockService medicalFacilityStockService;

    @GetMapping("/list-donation/{eventId}")
    public ResponseEntity<Object> getEventDetails(@PathVariable Long eventId) {
        try {
            DonationEventDto eventDetails = donationEventService.getDonationEventById(eventId);
            return ResponseEntity.ok(eventDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/my-donations")
    public ResponseEntity<Page<DonationEventDto>> getDonationByAccount(
            @CookieValue("jwt-token") String token,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        AccountDto staff = jwtUtil.extractUser(token);
        Page<DonationEventDto> events = donationEventService.getSortedPaginatedEventsByAccount(staff.getId(), pageNumber, pageSize, sortBy, ascending);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/my-donations/{eventId}")
    public ResponseEntity<Object> getEventDetailsByAccount(
            @CookieValue("jwt-token") String token,
            @PathVariable Long eventId) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            DonationEventDto eventDetails = donationEventService.getDonationEventByAuthor(eventId, accountDto.getId());
            return ResponseEntity.ok(eventDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/my-donations/{eventId}/update")
    public ResponseEntity<String> updateDonationEvent(
            @CookieValue("jwt-token") String token,
            @PathVariable Long eventId,
            @RequestBody @Valid DonationEventDto donationEventDto) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = donationEventRequestService.updateDonationRequest(accountDto.getId(), eventId, donationEventDto);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating update request" + e.getMessage());
        }
    }

    @PostMapping("/my-donations/{eventId}/delete")
    public ResponseEntity<String> deleteDonationEvent(
            @CookieValue("jwt-token") String token,
            @PathVariable Long eventId) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = donationEventRequestService.deleteDonationRequest(accountDto.getId(), eventId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating delete request" + e.getMessage());
        }

    }

    @GetMapping("/list-donation")
    public ResponseEntity<Page<DonationEventDto>> getDonationEventsPaginatedAndSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Page<DonationEventDto> events = donationEventService.getSortedPaginatedEvents(page, size, sortBy, ascending);
        return ResponseEntity.ok(events);
    }


    @GetMapping("/list-donation/{startDate}/{endDate}")
    public ResponseEntity<Page<DonationEventDto>> getDonationEventsByDateRange(
            @PathVariable LocalDate startDate,
            @PathVariable LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Page<DonationEventDto> events = donationEventService.getPaginatedEventsByDateRange(
                startDate, endDate, page, size, sortBy, ascending);

        return ResponseEntity.ok(events);
    }


    @PostMapping("/list-donation/{eventId}/record-donations")
    public ResponseEntity<Object> recordDonation(
            @CookieValue("jwt-token") String token,
            @PathVariable Long eventId,
            @Valid @RequestBody BulkBloodUnitRecordDto bulkRecordDto) {
        try {
            AccountDto staff = jwtUtil.extractUser(token);
            donationEventService.recordMultipleBloodDonations(eventId, bulkRecordDto.getSingleBloodUnitRecords(), staff.getEmail());
            return ResponseEntity.ok(medicalFacilityStockService.addBloodUnitsToStockByEventId(eventId, staff.getEmail()));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error recording multiple donations: " + e.getMessage());
        }
    }

    @GetMapping("/list-donation/{eventId}/time-slots/{timeSlotId}/donors")
    public ResponseEntity<Page<AccountDto>> getEventDonors(@PathVariable Long eventId,
                                                           @PathVariable Long timeSlotId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "id") String sortBy,
                                                           @RequestParam(defaultValue = "true") boolean ascending
    ) {
        return ResponseEntity.ok(donationEventService.getEventDonors(eventId, timeSlotId, page, size, sortBy, ascending));
    }

    @GetMapping("/list-donation/on-going")
    public ResponseEntity<List<DonationEventDto>> getOngoingDonationEvents() {
        List<DonationEventDto> ongoingEvents = donationEventService.getOngoingDonationEvents();
        return ResponseEntity.ok(ongoingEvents);
    }

    @GetMapping("/list-donation/{eventId}/donors")
    public ResponseEntity<Page<ProfileDto>> getEventDonors(@PathVariable Long eventId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "id") String sortBy,
                                                           @RequestParam(defaultValue = "true") boolean ascending) {
        return ResponseEntity.ok(donationEventService.getEventDonorProfilesPage(eventId, page, size, sortBy, ascending));
    }
}


