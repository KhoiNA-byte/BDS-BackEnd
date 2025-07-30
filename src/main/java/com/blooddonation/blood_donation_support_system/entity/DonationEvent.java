package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.DonationEventStatus;
import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "donation_events")
public class DonationEvent {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String hospital;
    @Column(nullable = false)
    private String address;

    private String ward;

    private String district;

    private String city;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate donationDate;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<DonationTimeSlot> timeSlots = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<EventRegistration> registrations = new ArrayList<>();

    @Column(name = "registered_member_count")
    private Integer registeredMemberCount = 0;

    @Column(nullable = false)
    private Integer totalMemberCount;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BloodUnit> bloodUnits = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DonationEventStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DonationType donationType;    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = true)
    private Organizer organizer;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate createdDate;

}