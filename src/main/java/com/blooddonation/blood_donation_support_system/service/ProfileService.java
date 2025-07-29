package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.UserDonationHistoryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProfileService {
    ProfileDto updateUser(AccountDto accountDto, ProfileDto profileDto);

    ProfileDto getProfileById(Long accountId);

    List<ProfileDto> getProfileByPersonalId(String personalId);

    Page<UserDonationHistoryDto> getDonationHistory(long accountId, int pageNumber, int pageSize, String sortBy, boolean ascending);

    Page<ProfileDto> getAllProfiles(int pageNumber, int pageSize, String sortBy, boolean ascending);

    List<ProfileDto> searchProfiles(String query);

    Page<UserDonationHistoryDto> getDonationHistoryById(Long profileId, int pageNumber, int pageSize, String sortBy, boolean ascending);

    void notifyEligibleDonors();
}
