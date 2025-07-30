package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComponentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ComponentType componentType;

    private double volume;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id", nullable = false)
    private BloodRequest bloodRequest;

    private LocalDate expiredDate;
}
