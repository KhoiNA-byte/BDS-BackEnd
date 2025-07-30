package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDonationHistoryDto {
    private Long registrationId;
    private LocalDate registrationDate;
    private DonationRegistrationStatus registrationDonationRegistrationStatus;
    private Double donationVolume;
    private LocalDate donationDate;
    private DonationType donationType;
    private String donationLocation;
    private String donationName;
    private Long accountId;
    private Long profileId;
    private Long donationId;
}
