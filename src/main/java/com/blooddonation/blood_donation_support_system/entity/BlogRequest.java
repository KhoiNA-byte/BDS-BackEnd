package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.converters.BlogDtoConverter;
import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import com.blooddonation.blood_donation_support_system.enums.BlogRequestStatus;
import com.blooddonation.blood_donation_support_system.enums.CRUDType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "blog_requests")
public class BlogRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Convert(converter = BlogDtoConverter.class)
    @Column(columnDefinition = "text")
    private BlogDto blogDto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private BlogRequestStatus status;

    @Enumerated(EnumType.STRING)
    private CRUDType crudType;
}
