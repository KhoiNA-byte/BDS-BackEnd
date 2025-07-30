package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileWithFormResponseDto;
import com.blooddonation.blood_donation_support_system.service.CheckinTokenService;
import com.blooddonation.blood_donation_support_system.service.EventRegistrationService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin
public class CheckInController {
    @Autowired
    private EventRegistrationService eventRegistrationService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CheckinTokenService checkinTokenService;
    @GetMapping("/{eventId}/qr-code")
    public ResponseEntity<Object> getUserQRCode(
            @PathVariable Long eventId,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            byte[] qrCode = eventRegistrationService.getQRCodeForUser(eventId, accountDto.getEmail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                    .body(qrCode);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving QR Code");
        }
    }

    @GetMapping("/{eventId}/checkin-token")
    public ResponseEntity<Object> getCheckinToken(
            @PathVariable Long eventId,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String checkinToken = checkinTokenService.generateTokenForUser(eventId, accountDto.getEmail());
            return ResponseEntity.ok().body(Map.of("checkinToken", checkinToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while generating checkin token");
        }
    }

    @GetMapping("/info/{eventId}")
    public ResponseEntity<Object> checkinInfo(@PathVariable Long eventId,
                                              @RequestParam String checkinToken,
                                              @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            ProfileWithFormResponseDto profileDto = checkinTokenService.getProfileFromToken(checkinToken, accountDto.getEmail(), eventId);
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information");
        }
    }

    @GetMapping("/info/profile/{eventId}")
    public ResponseEntity<Object> checkinInfoWithPersonalId(@PathVariable Long eventId,
                                              @RequestParam String personal_id,
                                              @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            ProfileWithFormResponseDto profileDto = checkinTokenService.getProfileFromPersonalId(personal_id, accountDto.getEmail(), eventId);
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information");
        }
    }

    @PostMapping("/action/{eventId}")
    public ResponseEntity<String> checkinToEvent(
            @PathVariable Long eventId,
            @RequestParam String action,
            @RequestParam String checkinToken,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            ProfileWithFormResponseDto profileDto = checkinTokenService.getProfileFromToken(checkinToken, accountDto.getEmail(), eventId);
            String result = eventRegistrationService.checkInMember(eventId, action, accountDto.getEmail(), profileDto.getProfile());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while checking-in the donation event: " + e.getMessage());
        }
    }
}
