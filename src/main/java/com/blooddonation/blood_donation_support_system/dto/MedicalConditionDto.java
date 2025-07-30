package com.blooddonation.blood_donation_support_system.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalConditionDto {
    private Long id;
    private String condition;
    private String description;
    private String urgencyLevel;
}
