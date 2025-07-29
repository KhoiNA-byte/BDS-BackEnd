package com.blooddonation.blood_donation_support_system.entity;

    import com.blooddonation.blood_donation_support_system.enums.*;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDate;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Builder
    @Table(name = "profiles")
    public class Profile {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "account_id", nullable = true)
        private Long accountId;

        private String name;

        private String phone;

        private String address;

        private String ward;

        private String district;

        private String city;

        @Enumerated(EnumType.STRING)
        private BloodType bloodType;

        @Enumerated(EnumType.STRING)
        private Gender gender;

        private LocalDate dateOfBirth;

        private LocalDate lastDonationDate;

        private LocalDate nextEligibleDonationDate;

        @Enumerated(EnumType.STRING)
        private ProfileStatus status = ProfileStatus.AVAILABLE;

        @Column(unique = true)
        private String personalId;
    }