package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerDto {
    private Long id;
    private String organizationName;
    private String contactPersonName;
    private String email;
    private String phoneNumber;
    private String address;
    private String ward;
    private String district;
    private String city;
    private String description;
    private String websiteUrl;
    private AccountStatus status;
    private Long createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

