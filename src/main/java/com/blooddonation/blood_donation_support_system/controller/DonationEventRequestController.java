package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventRequestDto;
import com.blooddonation.blood_donation_support_system.service.DonationEventRequestService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donation-event-request")
public class DonationEventRequestController {
    @Autowired
    private DonationEventRequestService donationEventRequestService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<String> createDonationRequest(
            @CookieValue("jwt-token") String token,
            @RequestBody @Valid DonationEventDto donationEventDto) {
        AccountDto staff = jwtUtil.extractUser(token);
        String response = donationEventRequestService.createDonationRequest(donationEventDto, staff.getEmail());
        return ResponseEntity.ok(response);
    }

    @PutMapping("pending/{requestId}/verify")
    public ResponseEntity<String> verifyDonationRequest(
            @PathVariable Long requestId,
            @RequestParam String action) {
        try {
            String result = donationEventRequestService.verifyDonation(requestId, action);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<DonationEventRequestDto>> getPaginatedRequests(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        Page<DonationEventRequestDto> requests = donationEventRequestService.getSortedPaginatedRequests(
                pageNumber, pageSize, sortBy, ascending);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending/{requestId}")
    public ResponseEntity<Object> getEventRequestDetails(@PathVariable Long requestId) {
        try {
            DonationEventRequestDto requestDetails = donationEventRequestService.getDonationRequestById(requestId);
            return ResponseEntity.ok(requestDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Page<DonationEventRequestDto>> getPaginatedRequestsByAccount(
            @CookieValue("jwt-token") String token,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        AccountDto staff = jwtUtil.extractUser(token);
        Page<DonationEventRequestDto> requests = donationEventRequestService.getSortedPaginatedRequestsByAccount(
                staff.getEmail(), pageNumber, pageSize, sortBy, ascending);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/my-requests/{requestId}")
    public ResponseEntity<Object> getEventRequestDetailsByAccount(
            @CookieValue("jwt-token") String token,
            @PathVariable Long requestId) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            DonationEventRequestDto requestDetails = donationEventRequestService.getDonationRequestByAuthor(requestId, accountDto.getId());
            return ResponseEntity.ok(requestDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update/{eventId}")
    public ResponseEntity<String> updateDonationRequest(
            @CookieValue("jwt-token") String token,
            @PathVariable Long eventId,
            @RequestBody @Valid DonationEventDto donationEventDto) {
        try {
            AccountDto staff = jwtUtil.extractUser(token);
            String response = donationEventRequestService.updateDonationRequest(staff.getId(), eventId, donationEventDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<String> deleteDonationRequest(
            @CookieValue("jwt-token") String token,
            @PathVariable Long eventId) {
        try {
            AccountDto staff = jwtUtil.extractUser(token);
            String response = donationEventRequestService.deleteDonationRequest(staff.getId(), eventId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
