package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.GoogleMapsDistanceResponse;
import com.blooddonation.blood_donation_support_system.dto.ProfileDistanceDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.entity.ProfileDistance;
import com.blooddonation.blood_donation_support_system.mapper.ProfileDistanceMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.repository.ProfileDistanceRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileDistanceService {

    private final ProfileDistanceRepository profileDistanceRepository;
    private final ProfileRepository profileRepository;
    private final GoogleMapsService googleMapsService;

    @Transactional
    public ProfileDistanceDto calculateAndSaveDistance(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found with ID: " + profileId));

        return calculateAndSaveDistance(profile);
    }

    @Transactional
    public ProfileDistanceDto calculateAndSaveDistance(Profile profile) {
        try {
            // Check if profile has complete address information
            if (isAddressIncomplete(profile)) {
                log.warn("Profile ID {} has incomplete address information", profile.getId());
                throw new RuntimeException("Profile address is incomplete");
            }

            // Calculate distance using Google Maps API
            GoogleMapsDistanceResponse response = googleMapsService.calculateDistance(profile);

            if (response.getRows() == null || response.getRows().isEmpty() ||
                    response.getRows().get(0).getElements() == null || response.getRows().get(0).getElements().isEmpty()) {
                throw new RuntimeException("Invalid response from Google Maps API");
            }

            GoogleMapsDistanceResponse.Element element = response.getRows().get(0).getElements().get(0);

            // Log the addresses being processed for debugging
            String originAddress = response.getOriginAddresses() != null && !response.getOriginAddresses().isEmpty()
                    ? response.getOriginAddresses().get(0) : "Unknown origin";
            String destinationAddress = response.getDestinationAddresses() != null && !response.getDestinationAddresses().isEmpty()
                    ? response.getDestinationAddresses().get(0) : "Unknown destination";

            log.info("Processing distance calculation for profile ID: {} | Origin: {} | Destination: {}",
                    profile.getId(), originAddress, destinationAddress);

            if (!"OK".equals(element.getStatus())) {
                log.warn("Google Maps API element status: {} for profile ID: {} | Origin: {} | Destination: {}",
                        element.getStatus(), profile.getId(), originAddress, destinationAddress);

                // Handle different element status codes
                if ("NOT_FOUND".equals(element.getStatus())) {
                    throw new RuntimeException("Address not found or no route available. Origin: " + originAddress +
                            " | Destination: " + destinationAddress);
                } else if ("ZERO_RESULTS".equals(element.getStatus())) {
                    throw new RuntimeException("No route found between addresses. Origin: " + originAddress +
                            " | Destination: " + destinationAddress);
                } else {
                    throw new RuntimeException("Google Maps API returned error status: " + element.getStatus() +
                            " for addresses - Origin: " + originAddress + " | Destination: " + destinationAddress);
                }
            }

            // Check if distance already exists
            Optional<ProfileDistance> existingDistance = profileDistanceRepository.findByProfile(profile);

            ProfileDistance profileDistance;
            if (existingDistance.isPresent()) {
                // Update existing record
                profileDistance = existingDistance.get();
                profileDistance.setLastUpdated(LocalDateTime.now());
            } else {
                // Create new record
                profileDistance = new ProfileDistance();
                profileDistance.setProfile(profile);
                profileDistance.setCalculatedAt(LocalDateTime.now());
            }

            // Update distance information
            profileDistance.setDistanceInMeters(element.getDistance().getValue().doubleValue());
            profileDistance.setDistanceInKilometers(element.getDistance().getValue().doubleValue() / 1000.0);
            profileDistance.setDurationInSeconds(element.getDuration().getValue());
            profileDistance.setDurationText(element.getDuration().getText());
            profileDistance.setDistanceText(element.getDistance().getText());
            profileDistance.setProfileAddress(buildProfileAddressForStorage(profile));
            profileDistance.setMedicalFacilityAddress(googleMapsService.getMedicalFacilityAddress());

            profileDistance = profileDistanceRepository.save(profileDistance);

            log.info("Successfully calculated and saved distance for profile ID: {}", profile.getId());
            return ProfileDistanceMapper.toDto(profileDistance);

        } catch (Exception e) {
            log.error("Error calculating distance for profile ID: {}", profile.getId(), e);
            throw new RuntimeException("Failed to calculate distance: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void calculateMissingDistances() {
        List<Profile> profilesWithoutDistance = profileDistanceRepository.findProfilesWithoutDistance();

        log.info("Found {} profiles without distance calculations", profilesWithoutDistance.size());

        for (Profile profile : profilesWithoutDistance) {
            try {
                calculateAndSaveDistance(profile);
                log.info("Successfully calculated distance for profile ID: {}", profile.getId());
            } catch (Exception e) {
                log.error("Failed to calculate distance for profile ID: {}", profile.getId(), e);
                // Continue with next profile instead of stopping the entire process
            }
        }

        log.info("Completed calculating missing distances");
    }

    public ProfileDistanceDto getDistanceByProfileId(Long profileId) {
        ProfileDistance profileDistance = profileDistanceRepository.findByProfileId(profileId)
                .orElseThrow(() -> new RuntimeException("Distance not found for profile ID: " + profileId));

        return ProfileDistanceMapper.toDto(profileDistance);
    }

    public List<ProfileDistanceDto> getProfilesWithinDistance(Double maxDistanceKm) {
        List<ProfileDistance> profileDistances = profileDistanceRepository.findProfilesWithinDistance(maxDistanceKm);

        return profileDistances.stream()
                .map(ProfileDistanceMapper::toDto)
                .toList();
    }

    public List<ProfileDistanceDto> getAllProfilesOrderedByDistance() {
        List<ProfileDistance> profileDistances = profileDistanceRepository.findAllOrderByDistanceAsc();

        return profileDistances.stream()
                .map(ProfileDistanceMapper::toDto)
                .toList();
    }

    // New methods that return ProfileDto with distance information for frontend pagination
    public Page<ProfileDto> getProfilesWithinDistanceAsProfileDto(Double maxDistanceKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProfileDistance> profileDistances = profileDistanceRepository.findProfilesWithinDistancePageable(maxDistanceKm, pageable);

        return profileDistances.map(this::convertToProfileDtoWithDistance);
    }

    public Page<ProfileDto> getAllProfilesOrderedByDistanceAsProfileDto(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProfileDistance> profileDistances = profileDistanceRepository.findAllOrderByDistanceAscPageable(pageable);

        return profileDistances.map(this::convertToProfileDtoWithDistance);
    }

    private ProfileDto convertToProfileDtoWithDistance(ProfileDistance profileDistance) {
        ProfileDto profileDto = ProfileMapper.toDto(profileDistance.getProfile());

        // Add distance information
        profileDto.setDistanceInKilometers(profileDistance.getDistanceInKilometers());
        profileDto.setDistanceText(profileDistance.getDistanceText());
        profileDto.setDurationText(profileDistance.getDurationText());

        return profileDto;
    }

    @Transactional
    public void deleteDistanceByProfileId(Long profileId) {
        profileDistanceRepository.deleteByProfileId(profileId);
    }

    @Transactional
    public ProfileDistanceDto recalculateDistance(Long profileId) {
        // Delete existing distance if exists
        profileDistanceRepository.deleteByProfileId(profileId);

        // Calculate new distance
        return calculateAndSaveDistance(profileId);
    }

    private boolean isAddressIncomplete(Profile profile) {
        return profile.getAddress() == null || profile.getAddress().trim().isEmpty() ||
                profile.getWard() == null || profile.getWard().trim().isEmpty() ||
                profile.getDistrict() == null || profile.getDistrict().trim().isEmpty() ||
                profile.getCity() == null || profile.getCity().trim().isEmpty();
    }

    private String buildProfileAddressForStorage(Profile profile) {
        StringBuilder address = new StringBuilder();

        if (profile.getAddress() != null && !profile.getAddress().trim().isEmpty()) {
            address.append(profile.getAddress().trim());
        }

        if (profile.getWard() != null && !profile.getWard().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(profile.getWard().trim());
        }

        if (profile.getDistrict() != null && !profile.getDistrict().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(profile.getDistrict().trim());
        }

        if (profile.getCity() != null && !profile.getCity().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(profile.getCity().trim());
        }

        return address.toString();
    }
}