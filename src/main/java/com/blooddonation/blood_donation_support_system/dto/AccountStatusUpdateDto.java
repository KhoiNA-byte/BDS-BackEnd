package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusUpdateDto {
    @NotNull(message = "Status is required")
    private AccountStatus status;
}
