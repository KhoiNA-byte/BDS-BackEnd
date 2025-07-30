package com.blooddonation.blood_donation_support_system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "donation_time_slots")
public class DonationTimeSlot {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false)
    private Integer currentRegistrations = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private DonationEvent event;

    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventRegistration> registrations = new ArrayList<>();
}
