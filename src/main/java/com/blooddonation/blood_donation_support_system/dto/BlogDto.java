package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.BlogStatus;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogDto {
    private Long id;
    private Long authorId;
    private String authorName;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters long")
    private String content;


    private String thumbnail;

    @NotNull(message = "Status is required")
    private BlogStatus status;

    private LocalDate creationDate;
    private LocalDate lastModifiedDate;
}
