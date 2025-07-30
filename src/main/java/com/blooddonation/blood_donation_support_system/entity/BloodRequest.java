package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BloodRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Enumerated(EnumType.STRING)
    private BloodRequestStatus status;

    private LocalDateTime createdTime;

    private LocalDateTime endTime;

    private LocalDate requiredDate;

    @Enumerated(EnumType.STRING)
    private Urgency urgency;

    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @OneToMany(mappedBy = "bloodRequest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ComponentRequest> componentRequests;

    @OneToMany(mappedBy = "bloodRequest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BloodUnit> bloodUnits;

    @Enumerated(EnumType.STRING)
    private Set<MedicalCondition> medicalConditions;

    private String additionalMedicalInformation;

    private String additionalNotes;

    private boolean isPregnant;

    private boolean isDisabled;

    private boolean haveServed;

    @Builder.Default
    private boolean isAutomation = true;
}
