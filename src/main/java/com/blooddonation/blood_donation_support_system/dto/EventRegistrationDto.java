package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRegistrationDto {
    private Long id;
    private Long accountId;         // Representing account by ID
    private Long eventId;        // Representing event by ID
    private Long timeSlotId;     // Representing time slot by ID
    private Long profileId;
    private BloodType bloodType;
    private DonationType donationType;
    private byte[] qrCode;
    @JsonRawValue
    private String jsonForm;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate registrationDate;

    private DonationRegistrationStatus status;
}
