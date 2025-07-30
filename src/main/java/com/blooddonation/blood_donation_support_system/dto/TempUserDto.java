package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.entity.Account;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TempUserDto {
    private Account account;
    private String name;
}
