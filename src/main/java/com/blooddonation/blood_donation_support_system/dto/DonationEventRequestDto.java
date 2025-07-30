package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.CRUDType;
import com.blooddonation.blood_donation_support_system.enums.DonationRequestStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationEventRequestDto {
    private Long id;
    private Long eventId;
    private Long authorId;
    private DonationEventDto oldDonationEventDto;
    private DonationEventDto newDonationEventDto;
    private DonationRequestStatus status;
    private CRUDType crudType;
}
