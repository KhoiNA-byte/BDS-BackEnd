package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.OrganizerDto;
import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import com.blooddonation.blood_donation_support_system.service.OrganizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/organizers")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<OrganizerDto>> getAllOrganizers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "organizationName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AccountStatus status) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrganizerDto> organizers = organizerService.searchOrganizers(search, status, pageable);
        
        return ResponseEntity.ok(organizers);
    }

    @GetMapping("/active")
    public ResponseEntity<List<OrganizerDto>> getActiveOrganizers() {
        List<OrganizerDto> organizers = organizerService.getActiveOrganizers();
        return ResponseEntity.ok(organizers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerDto> getOrganizerById(@PathVariable Long id) {
        OrganizerDto organizer = organizerService.getOrganizerById(id);
        return ResponseEntity.ok(organizer);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrganizerDto> getOrganizerByEmail(@PathVariable String email) {
        OrganizerDto organizer = organizerService.getOrganizerByEmail(email);
        return ResponseEntity.ok(organizer);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrganizerDto> createOrganizer(
            @Valid @RequestBody OrganizerDto organizerDto,
            Principal principal) {
        
        // In a real application, you would get the account ID from the authenticated principal
        // For now, using a placeholder approach
        Long createdByAccountId = 1L; // This should be extracted from the authenticated user
        
        OrganizerDto createdOrganizer = organizerService.createOrganizer(organizerDto, createdByAccountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganizer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrganizerDto> updateOrganizer(
            @PathVariable Long id,
            @Valid @RequestBody OrganizerDto organizerDto) {
        
        OrganizerDto updatedOrganizer = organizerService.updateOrganizer(id, organizerDto);
        return ResponseEntity.ok(updatedOrganizer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganizer(@PathVariable Long id) {
        organizerService.deleteOrganizer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrganizerDto> activateOrganizer(@PathVariable Long id) {
        OrganizerDto organizer = organizerService.activateOrganizer(id);
        return ResponseEntity.ok(organizer);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrganizerDto> deactivateOrganizer(@PathVariable Long id) {
        OrganizerDto organizer = organizerService.deactivateOrganizer(id);
        return ResponseEntity.ok(organizer);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<OrganizerDto>> getOrganizersByCity(@PathVariable String city) {
        List<OrganizerDto> organizers = organizerService.getOrganizersByCity(city);
        return ResponseEntity.ok(organizers);
    }

    @GetMapping("/check-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = organizerService.isEmailExists(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Long> getTotalOrganizersCount() {
        long count = organizerService.getTotalOrganizersCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/active-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Long> getActiveOrganizersCount() {
        long count = organizerService.getActiveOrganizersCount();
        return ResponseEntity.ok(count);
    }
}
