package com.blooddonation.blood_donation_support_system.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Checkin-Token")
@Entity

public class CheckinToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate creationDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
}
