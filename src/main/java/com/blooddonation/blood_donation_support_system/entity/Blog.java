package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.BlogStatus;
import com.blooddonation.blood_donation_support_system.enums.DonationRegistrationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

//    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    @Enumerated(EnumType.STRING)
    private BlogStatus status;
    private LocalDate creationDate;
    private LocalDate lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
        lastModifiedDate = LocalDate.now();
        status = BlogStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDate.now();
    }
}
