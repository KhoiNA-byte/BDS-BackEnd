package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.BloodUnitStatus;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "blood_units")
public class BloodUnit {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = true)
    private DonationEvent event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = true)
    private Account donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profileId;

    @Column(nullable = false)
    private Double volume;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ComponentType componentType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BloodUnitStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_request_id", nullable = true)
    private BloodRequest bloodRequest;
}
