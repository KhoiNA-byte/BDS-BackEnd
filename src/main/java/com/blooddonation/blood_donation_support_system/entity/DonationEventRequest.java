package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.converters.DonationEventDtoConverter;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.enums.CRUDType;
import com.blooddonation.blood_donation_support_system.enums.DonationRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "donation_event_requests")
public class DonationEventRequest {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private DonationEvent donationEvent;

    @Convert(converter = DonationEventDtoConverter.class)
    @Column(columnDefinition = "text")
    private DonationEventDto oldDonationEventDto;

    @Convert(converter = DonationEventDtoConverter.class)
    @Column(columnDefinition = "text")
    private DonationEventDto newDonationEventDto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private DonationRequestStatus status;

    @Enumerated(EnumType.STRING)
    private CRUDType crudType;
}