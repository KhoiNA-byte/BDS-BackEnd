
package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.Gender;
import com.blooddonation.blood_donation_support_system.enums.ProfileStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private Long id;
    private Long accountId;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private String name;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "Ward cannot be blank")
    private String ward;

    @NotBlank(message = "District cannot be blank")
    private String district;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotNull(message = "Blood type is required")
    private BloodType bloodType;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Past(message = "Date of birth must be in the past")
//    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    @PastOrPresent(message = "Last donation date cannot be in the future")
//    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate lastDonationDate;

    //    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate nextEligibleDonationDate;

    //    @NotNull(message = "Status is required")
    private ProfileStatus status;

    @Pattern(regexp = "^[0-9]{12}$", message = "Personal ID must be 12 digits")
    private String personalId;

    // Distance fields (optional, used when filtering by distance)
    private Double distanceInKilometers;
    private String distanceText;
    private String durationText;
}
