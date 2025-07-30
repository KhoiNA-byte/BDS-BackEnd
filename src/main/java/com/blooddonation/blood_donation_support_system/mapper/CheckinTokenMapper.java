package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.CheckinTokenDto;
import com.blooddonation.blood_donation_support_system.entity.CheckinToken;
import org.springframework.stereotype.Component;

@Component
public class CheckinTokenMapper {
    public static CheckinTokenDto toDto(CheckinToken checkinToken) {
        if (checkinToken == null) {
            return null;
        }

        return CheckinTokenDto.builder()
                .id(checkinToken.getId())
                .token(checkinToken.getToken())
                .creationDate(checkinToken.getCreationDate())
                .expirationDate(checkinToken.getExpirationDate())
                .build();
    }

    public static CheckinToken toEntity(CheckinTokenDto checkinTokenDto) {
        if (checkinTokenDto == null) {
            return null;
        }

        return CheckinToken.builder()
                .id(checkinTokenDto.getId())
                .token(checkinTokenDto.getToken())
                .creationDate(checkinTokenDto.getCreationDate())
                .expirationDate(checkinTokenDto.getExpirationDate())
                .build();
    }
}
