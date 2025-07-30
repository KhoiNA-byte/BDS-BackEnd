package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.OrganizerDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Organizer;
import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import com.blooddonation.blood_donation_support_system.exception.ResourceNotFoundException;
import com.blooddonation.blood_donation_support_system.mapper.OrganizerMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<OrganizerDto> getAllOrganizers() {
        return organizerRepository.findAll().stream()
                .map(OrganizerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrganizerDto> getAllOrganizers(Pageable pageable) {
        return organizerRepository.findAll(pageable)
                .map(OrganizerMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<OrganizerDto> getActiveOrganizers() {
        return organizerRepository.findByStatus(AccountStatus.ENABLE).stream()
                .map(OrganizerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrganizerDto> getActiveOrganizers(Pageable pageable) {
        return organizerRepository.findByStatus(AccountStatus.ENABLE, pageable)
                .map(OrganizerMapper::toDto);
    }

    @Transactional(readOnly = true)
    public OrganizerDto getOrganizerById(Long id) {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + id));
        return OrganizerMapper.toDto(organizer);
    }

    @Transactional(readOnly = true)
    public OrganizerDto getOrganizerByEmail(String email) {
        Organizer organizer = organizerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with email: " + email));
        return OrganizerMapper.toDto(organizer);
    }

    public OrganizerDto createOrganizer(OrganizerDto organizerDto, Long createdByAccountId) {
        // Check if email already exists
        if (organizerRepository.existsByEmail(organizerDto.getEmail())) {
            throw new IllegalArgumentException("An organizer with this email already exists");
        }

        Account createdByAccount = accountRepository.findById(createdByAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + createdByAccountId));

        Organizer organizer = OrganizerMapper.toEntity(organizerDto, createdByAccount);
        organizer = organizerRepository.save(organizer);
        
        return OrganizerMapper.toDto(organizer);
    }

    public OrganizerDto updateOrganizer(Long id, OrganizerDto organizerDto) {
        Organizer existingOrganizer = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + id));

        // Check if email is being changed and if the new email already exists
        if (!existingOrganizer.getEmail().equals(organizerDto.getEmail()) &&
            organizerRepository.existsByEmailAndIdNot(organizerDto.getEmail(), id)) {
            throw new IllegalArgumentException("An organizer with this email already exists");
        }

        OrganizerMapper.updateEntityFromDto(existingOrganizer, organizerDto);
        existingOrganizer = organizerRepository.save(existingOrganizer);
        
        return OrganizerMapper.toDto(existingOrganizer);
    }

    public void deleteOrganizer(Long id) {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + id));
        
        // Check if organizer has any associated events
        long eventCount = organizerRepository.countEventsByOrganizerId(id);
        if (eventCount > 0) {
            // Soft delete by setting status to DISABLE
            organizer.setStatus(AccountStatus.DISABLE);
            organizerRepository.save(organizer);
        } else {
            // Hard delete if no associated events
            organizerRepository.delete(organizer);
        }
    }

    public OrganizerDto activateOrganizer(Long id) {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + id));
        
        organizer.setStatus(AccountStatus.ENABLE);
        organizer = organizerRepository.save(organizer);
        
        return OrganizerMapper.toDto(organizer);
    }

    public OrganizerDto deactivateOrganizer(Long id) {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + id));
        
        organizer.setStatus(AccountStatus.DISABLE);
        organizer = organizerRepository.save(organizer);
        
        return OrganizerMapper.toDto(organizer);
    }

    @Transactional(readOnly = true)
    public Page<OrganizerDto> searchOrganizers(String searchTerm, AccountStatus status, Pageable pageable) {
        Page<Organizer> organizers;
        
        if (status != null && searchTerm != null && !searchTerm.trim().isEmpty()) {
            organizers = organizerRepository.findByStatusAndSearchTerm(status, searchTerm.trim(), pageable);
        } else if (status != null) {
            organizers = organizerRepository.findByStatus(status, pageable);
        } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            organizers = organizerRepository.findBySearchTerm(searchTerm.trim(), pageable);
        } else {
            organizers = organizerRepository.findAll(pageable);
        }
        
        return organizers.map(OrganizerMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<OrganizerDto> getOrganizersByCity(String city) {
        return organizerRepository.findByCityAndStatus(city, AccountStatus.ENABLE).stream()
                .map(OrganizerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrganizerDto> getOrganizersByCreatedBy(Long accountId) {
        return organizerRepository.findByCreatedByAccountIdAndStatus(accountId, AccountStatus.ENABLE).stream()
                .map(OrganizerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return organizerRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public long getTotalOrganizersCount() {
        return organizerRepository.count();
    }

    @Transactional(readOnly = true)
    public long getActiveOrganizersCount() {
        return organizerRepository.countByStatus(AccountStatus.ENABLE);
    }
}
