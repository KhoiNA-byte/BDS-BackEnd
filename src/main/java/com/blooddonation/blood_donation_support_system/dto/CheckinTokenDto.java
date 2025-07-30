package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckinTokenDto {
    private Long id;
    private String token;
    private Profile profile;
    private LocalDate creationDate;
    private LocalDate expirationDate;

}
