package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.BlogRequestStatus;
import com.blooddonation.blood_donation_support_system.enums.CRUDType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequestDto {
    private Long id;
    private Long accountId;
    private Long blogId;
    private BlogDto blog;
    private BlogRequestStatus status;
    private CRUDType crudType;
}
