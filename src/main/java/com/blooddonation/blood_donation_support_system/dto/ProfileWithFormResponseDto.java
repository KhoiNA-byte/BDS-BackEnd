package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.DonationEventStatus;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import com.blooddonation.blood_donation_support_system.enums.ProfileStatus;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileWithFormResponseDto {
    private ProfileDto profile;
    private String jsonForm;
    private DonationRegistrationStatus status;
    private String checkinToken;
}
