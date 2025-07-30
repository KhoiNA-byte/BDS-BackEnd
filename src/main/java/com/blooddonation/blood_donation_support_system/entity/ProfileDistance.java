package com.blooddonation.blood_donation_support_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "profile_distances")
public class ProfileDistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @Column(name = "distance_in_meters", nullable = false)
    private Double distanceInMeters;

    @Column(name = "distance_in_kilometers", nullable = false)
    private Double distanceInKilometers;

    @Column(name = "duration_in_seconds", nullable = false)
    private Long durationInSeconds;

    @Column(name = "duration_text", nullable = false)
    private String durationText;

    @Column(name = "distance_text", nullable = false)
    private String distanceText;

    @Column(name = "profile_address", nullable = false, length = 1000)
    private String profileAddress;

    @Column(name = "medical_facility_address", nullable = false, length = 1000)
    private String medicalFacilityAddress;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
