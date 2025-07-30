package com.blooddonation.blood_donation_support_system.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BloodRequestBatchDto {
    private BloodRequestDto bloodRequestDto;
    private ProfileDto profileDto;
    private BloodUnitDto bloodUnitDto;
}
