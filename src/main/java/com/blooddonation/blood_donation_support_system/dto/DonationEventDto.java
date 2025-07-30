package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.DonationEventStatus;
import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationEventDto {
    private Long id;
    @NotBlank(message = "Event name cannot be blank")
    @Size(min = 5, max = 100, message = "Event name must be between 5 and 100 characters")
    private String name;

    @NotBlank(message = "Hospital cannot be blank")
    private String hospital;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "Ward cannot be blank")
    private String ward;

    @NotBlank(message = "District cannot be blank")
    private String district;

    @NotBlank(message = "City cannot be blank")
    private String city;

    private Integer registeredMemberCount;

    @NotNull(message = "Total member count is required")
    @Min(value = 1, message = "Total member count must be at least 1")
    private Integer totalMemberCount;
    private DonationEventStatus status;
    @NotNull(message = "Donation type is required")
    private DonationType donationType;
    private Long accountId;
    private Long organizerId;
    private OrganizerDto organizer;
    @Valid
    @NotEmpty(message = "At least one time slot is required")
    private List<DonationTimeSlotDto> timeSlotDtos;
    private LocalDate createdDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "Donation date is required")
    @FutureOrPresent(message = "Donation date must be in the future or present")
    private LocalDate donationDate;

}