package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.ProfileDistanceDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.service.ProfileDistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile-distances")
@RequiredArgsConstructor
public class ProfileDistanceController {

    private final ProfileDistanceService profileDistanceService;

    @GetMapping("/{profileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProfileDistanceDto> getDistanceByProfileId(@PathVariable Long profileId) {
        try {
            ProfileDistanceDto distance = profileDistanceService.getDistanceByProfileId(profileId);
            return ResponseEntity.ok(distance);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/calculate/{profileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProfileDistanceDto> calculateDistance(@PathVariable Long profileId) {
        try {
            ProfileDistanceDto distance = profileDistanceService.calculateAndSaveDistance(profileId);
            return ResponseEntity.ok(distance);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/recalculate/{profileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProfileDistanceDto> recalculateDistance(@PathVariable Long profileId) {
        try {
            ProfileDistanceDto distance = profileDistanceService.recalculateDistance(profileId);
            return ResponseEntity.ok(distance);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/within-distance")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ProfileDistanceDto>> getProfilesWithinDistance(
            @RequestParam Double maxDistanceKm) {
        try {
            List<ProfileDistanceDto> profiles = profileDistanceService.getProfilesWithinDistance(maxDistanceKm);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all-ordered")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ProfileDistanceDto>> getAllProfilesOrderedByDistance() {
        try {
            List<ProfileDistanceDto> profiles = profileDistanceService.getAllProfilesOrderedByDistance();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/calculate-missing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> calculateMissingDistances() {
        try {
            profileDistanceService.calculateMissingDistances();
            return ResponseEntity.ok("Missing distances calculated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating missing distances: " + e.getMessage());
        }
    }

    // New endpoints that return ProfileDto with distance information for frontend pagination
    @GetMapping("/profiles/within-distance")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<ProfileDto>> getProfilesWithinDistanceAsProfileDto(
            @RequestParam Double maxDistanceKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProfileDto> profiles = profileDistanceService.getProfilesWithinDistanceAsProfileDto(maxDistanceKm, page, size);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/profiles/all-ordered")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<ProfileDto>> getAllProfilesOrderedByDistanceAsProfileDto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProfileDto> profiles = profileDistanceService.getAllProfilesOrderedByDistanceAsProfileDto(page, size);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDistance(@PathVariable Long profileId) {
        try {
            profileDistanceService.deleteDistanceByProfileId(profileId);
            return ResponseEntity.ok("Distance deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting distance: " + e.getMessage());
        }
    }
}