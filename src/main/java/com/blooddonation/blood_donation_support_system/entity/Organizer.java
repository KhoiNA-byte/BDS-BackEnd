package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "organizers")
public class Organizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 200, message = "Organization name must be between 2 and 200 characters")
    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @NotBlank(message = "Contact person name is required")
    @Size(min = 2, max = 100, message = "Contact person name must be between 2 and 100 characters")
    @Column(name = "contact_person_name", nullable = false)
    private String contactPersonName;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 300, message = "Address cannot exceed 300 characters")
    @Column(name = "address")
    private String address;

    @Size(max = 100, message = "Ward cannot exceed 100 characters")
    @Column(name = "ward")
    private String ward;

    @Size(max = 100, message = "District cannot exceed 100 characters")
    @Column(name = "district")
    private String district;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(name = "city")
    private String city;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "website_url")
    private String websiteUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ENABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Account createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DonationEvent> donationEvents = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
