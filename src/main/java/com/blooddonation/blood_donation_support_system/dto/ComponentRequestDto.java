package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentRequestDto {
    private Long id;
    private ComponentType componentType;
    private double volume;
    private Long request_id;
    private LocalDate expiredDate;
}
