package com.blooddonation.blood_donation_support_system.controller;


import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileWithFormResponseDto;
import com.blooddonation.blood_donation_support_system.service.CheckinTokenService;
import com.blooddonation.blood_donation_support_system.service.DonationEventService;
import com.blooddonation.blood_donation_support_system.service.EventRegistrationService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event-registration")
@CrossOrigin
public class EventRegistrationController {
    @Autowired
    private DonationEventService donationEventService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CheckinTokenService checkinTokenService;
    @Autowired
    private EventRegistrationService eventRegistrationService;

    @PostMapping("/{eventId}/{timeSlotId}/register")
    public ResponseEntity<String> registerForEvent(@PathVariable Long eventId,
                                                   @PathVariable Long timeSlotId,
                                                   @RequestBody String jsonForm,
                                                   @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = eventRegistrationService.registerForEventOnline(eventId, timeSlotId, accountDto.getEmail(), jsonForm);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering for the event: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/registerOffline")
    public ResponseEntity<String> registerForEventOffline(
            @PathVariable Long eventId,
            @RequestParam String personalId,
            @RequestBody String jsonForm,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = eventRegistrationService.registerForEventOffline(eventId, personalId, accountDto.getEmail(), jsonForm);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering for the event: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/register-guest")
    public ResponseEntity<Object> registerGuest(
            @PathVariable Long eventId,
            @RequestBody ProfileWithFormResponseDto profileWithFormResponseDto,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto staff = jwtUtil.extractUser(token);
            ProfileDto registeredProfile = eventRegistrationService.registerForGuest(eventId, profileWithFormResponseDto.getProfile(), staff.getEmail(), profileWithFormResponseDto.getJsonForm());
            return ResponseEntity.ok(registeredProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error registering guest: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<String> cancelDonationEvent(@PathVariable Long eventId,
                                                      @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = eventRegistrationService.cancelEventRegistration(eventId, accountDto.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while cancelling the donation event: " + e.getMessage());
        }
    }

}
